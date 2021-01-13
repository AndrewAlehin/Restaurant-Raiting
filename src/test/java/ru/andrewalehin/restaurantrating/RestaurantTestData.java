package ru.andrewalehin.restaurantrating;

import java.time.LocalDate;
import java.util.Set;
import ru.andrewalehin.restaurantrating.model.Restaurant;
import ru.andrewalehin.restaurantrating.to.RestaurantTo;

public class RestaurantTestData {

  public static final TestMatcher<Restaurant> RESTAURANT_MATCHER = TestMatcher
      .usingIgnoringFieldsComparator(Restaurant.class, "votes", "meals");

  public static TestMatcher<RestaurantTo> RESTAURANT_TO_MATCHER = TestMatcher
      .usingIgnoringFieldsComparator(RestaurantTo.class, "votes", "meals");

  public static final int RESTAURANT1_ID = 1;
  public static final int RESTAURANT2_ID = 2;
  public static final int RESTAURANT3_ID = 3;
  public static final int RESTAURANT4_ID = 4;
  public static final int RESTAURANT5_ID = 5;
  public static final int REST_RESTAURANT_ID = 80;

  public static final Restaurant restaurant1 = new Restaurant(RESTAURANT1_ID, "Ресторан",
      LocalDate.of(2021, 1, 8), UserTestData.users, Set.of());
  public static final Restaurant restaurant2 = new Restaurant(RESTAURANT2_ID, "Ресторан2",
      LocalDate.of(2021, 1, 8), Set.of(UserTestData.admin), Set.of());

  public static final Set<Restaurant> restaurants = Set.of(restaurant1, restaurant2);

  public static Restaurant getNew() {
    return new Restaurant("Ресторан3", LocalDate.now());
  }

  public static Restaurant getUpdated() {
    return new Restaurant(RESTAURANT1_ID, "РесторанNew", LocalDate.now());
  }
}
