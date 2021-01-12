package ru.andrewalehin.restaurantrating.web.restaurant;

import static ru.andrewalehin.restaurantrating.util.ValidationUtil.assureIdConsistent;
import static ru.andrewalehin.restaurantrating.util.ValidationUtil.checkNew;
import static ru.andrewalehin.restaurantrating.util.ValidationUtil.checkSingleModification;

import java.net.URI;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.andrewalehin.restaurantrating.model.Restaurant;
import ru.andrewalehin.restaurantrating.repository.RestaurantRepository;
import ru.andrewalehin.restaurantrating.util.ValidationUtil;
import ru.andrewalehin.restaurantrating.web.AuthUser;

@RestController
@RequestMapping(value = RestaurantAdminRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "restaurants")
public class RestaurantAdminRestController {

  static final String REST_URL = "/rest/admin/restaurants";

  private final RestaurantRepository restaurantRepository;

  @GetMapping("/{id}")
  public ResponseEntity<Restaurant> get(@AuthenticationPrincipal AuthUser authUser,
      @PathVariable int id) {
    log.info("get restaurant {} for user {}", id, authUser.id());
    return ResponseEntity.of(restaurantRepository.get(id));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @CacheEvict(value = "restaurants", key = "#id")
  public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
    log.info("delete {} for user {}", id, authUser.id());
    checkSingleModification(restaurantRepository.delete(id),
        "Restaurant id=" + id + ", user id=" + authUser.id() + " missed");
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @CacheEvict(value = "restaurants", key = "#id")
  public void update(@AuthenticationPrincipal AuthUser authUser,
      @Valid @RequestBody Restaurant restaurant, @PathVariable int id) {
    int userId = authUser.id();
    log.info("update {} for user {}", restaurant, userId);
    assureIdConsistent(restaurant, id);
    ValidationUtil
        .checkNotFoundWithId(restaurantRepository.get(id),
            "Restaurant id=" + id + " doesn't belong to user id=" + userId);
    restaurantRepository.save(restaurant);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @CachePut(value = "restaurants", key = "#restaurant.id")
  public ResponseEntity<Restaurant> createWithLocation(@AuthenticationPrincipal AuthUser authUser,
      @Valid @RequestBody Restaurant restaurant) {
    int userId = authUser.id();
    log.info("create {} for user {}", restaurant, userId);
    checkNew(restaurant);
    Restaurant created = restaurantRepository.save(restaurant);
    URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path(REST_URL + "/{id}")
        .buildAndExpand(created.getId()).toUri();
    return ResponseEntity.created(uriOfNewResource).body(created);
  }
}