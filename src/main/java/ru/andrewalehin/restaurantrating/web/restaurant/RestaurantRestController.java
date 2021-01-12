package ru.andrewalehin.restaurantrating.web.restaurant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.andrewalehin.restaurantrating.model.Restaurant;
import ru.andrewalehin.restaurantrating.model.User;
import ru.andrewalehin.restaurantrating.repository.RestaurantRepository;
import ru.andrewalehin.restaurantrating.to.RestaurantTo;
import ru.andrewalehin.restaurantrating.util.RestaurantsUtil;
import ru.andrewalehin.restaurantrating.util.ValidationUtil;
import ru.andrewalehin.restaurantrating.web.AuthUser;

@RestController
@RequestMapping(value = RestaurantRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "restaurants")
public class RestaurantRestController {

  static final String REST_URL = "/rest/profile/restaurants";

  private final RestaurantRepository restaurantRepository;

  @GetMapping
  @Cacheable
  public List<RestaurantTo> getAll(@AuthenticationPrincipal AuthUser authUser,
      @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    log.info("getAll for user {}", authUser.id());
    return RestaurantsUtil
        .getTos(restaurantRepository.getAll(date));
  }

  @GetMapping("/increase/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Transactional
  @CacheEvict(value = "restaurants", key = "#id")
  public void increaseVote(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
    log.info("increase vote restaurant {}  user {}", id, authUser.id());
    Restaurant restaurant = ValidationUtil
        .checkNotFoundWithId(restaurantRepository.get(id),
            "Restaurant id=" + id + " doesn't belong");
    if (restaurant.getDate().atTime(11, 0).compareTo(LocalDateTime.now()) > 0) {
      Restaurant restaurantFind = RestaurantsUtil
          .findUserVote(restaurantRepository.getAllWithVotes(restaurant.getDate()),
              authUser.getUser());
      Set<User> votes = restaurant.getVotes();
      votes.add(authUser.getUser());
      restaurant.setVotes(votes);
      restaurantRepository.save(restaurant);
      if (restaurantFind != null) {
        decreaseVote(authUser, restaurantFind.id());
      }
    }
  }

  @GetMapping("/decrease/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Transactional
  @CacheEvict(value = "restaurants", key = "#id")
  public void decreaseVote(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
    log.info("decrease vote restaurant {}  user {}", id, authUser.id());
    Restaurant restaurant = ValidationUtil
        .checkNotFoundWithId(restaurantRepository.get(id),
            "Restaurant id=" + id + " doesn't belong");
    Set<User> votes = restaurant.getVotes();
    votes.remove(authUser.getUser());
    restaurant.setVotes(votes);
    restaurantRepository.save(restaurant);
  }
}