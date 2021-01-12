package ru.andrewalehin.restaurantrating.web.meal;


import static ru.andrewalehin.restaurantrating.util.ValidationUtil.assureIdConsistent;
import static ru.andrewalehin.restaurantrating.util.ValidationUtil.checkNew;
import static ru.andrewalehin.restaurantrating.util.ValidationUtil.checkSingleModification;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import ru.andrewalehin.restaurantrating.model.Meal;
import ru.andrewalehin.restaurantrating.repository.MealRepository;
import ru.andrewalehin.restaurantrating.repository.RestaurantRepository;
import ru.andrewalehin.restaurantrating.to.MealTo;
import ru.andrewalehin.restaurantrating.util.MealsUtil;
import ru.andrewalehin.restaurantrating.util.ValidationUtil;

@RestController
@RequestMapping(value = MealRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "meals")
public class MealRestController {

  static final String REST_URL = "/rest/admin/meals";

  private final MealRepository mealRepository;
  private final RestaurantRepository restaurantRepository;

  @GetMapping("/{restId}/{id}")
  public ResponseEntity<Meal> get(@PathVariable int restId, @PathVariable int id) {
    log.info("get meal {} for restaurant {}", id, restId);
    return ResponseEntity.of(mealRepository.get(id, restId));
  }

  @DeleteMapping("/{restId}/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @CacheEvict(allEntries = true)
  public void delete(@PathVariable int restId, @PathVariable int id) {
    log.info("delete {} for restaurant {}", id, restId);
    checkSingleModification(mealRepository.delete(id, restId),
        "Meal id=" + id + ", restaurant id=" + restId + " missed");
  }

  @GetMapping("/{restId}")
  @Cacheable
  public List<MealTo> getAll(@PathVariable int restId) {
    log.info("getAll for restaurant {}", restId);
    return MealsUtil.getTos(mealRepository.getAll(restId));
  }


  @PutMapping(value = "/{restId}/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @CacheEvict(allEntries = true)
  public void update(@PathVariable int restId, @Valid @RequestBody Meal meal,
      @PathVariable int id) {
    log.info("update {} for restaurant {}", meal, restId);
    assureIdConsistent(meal, id);
    ValidationUtil.checkNotFoundWithId(mealRepository.get(id, restId),
        "Meal id=" + id + " doesn't belong to restaurant id=" + restId);
    meal.setRestaurant(restaurantRepository.getOne(restId));
    mealRepository.save(meal);
  }

  @PostMapping(value = "/{restId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @CacheEvict(allEntries = true)
  public ResponseEntity<Meal> createWithLocation(@PathVariable int restId,
      @Valid @RequestBody Meal meal) {
    log.info("create {} for restaurant {}", meal, restId);
    checkNew(meal);
    meal.setRestaurant(restaurantRepository.getOne(restId));
    Meal created = mealRepository.save(meal);
    URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path(REST_URL + "/{id}")
        .buildAndExpand(created.getId()).toUri();
    return ResponseEntity.created(uriOfNewResource).body(created);
  }
}