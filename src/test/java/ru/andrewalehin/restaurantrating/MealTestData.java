package ru.andrewalehin.restaurantrating;

import static ru.andrewalehin.restaurantrating.RestaurantTestData.restaurant1;
import static ru.andrewalehin.restaurantrating.RestaurantTestData.restaurant2;

import java.util.Set;
import ru.andrewalehin.restaurantrating.model.Meal;
import ru.andrewalehin.restaurantrating.to.MealTo;

public class MealTestData {

  public static final TestMatcher<Meal> MEAL_MATCHER = TestMatcher
      .usingIgnoringFieldsComparator(Meal.class, "restaurant");
  public static TestMatcher<MealTo> MEAL_TO_MATCHER = TestMatcher
      .usingEqualsComparator(MealTo.class);

  public static final int MEAL1_ID = 1;
  public static final int REST_MEAL_ID = 8;

  public static final Meal meal2 = new Meal(MEAL1_ID + 1, "Обед", 1000);
  public static final Meal restMeal1 = new Meal(REST_MEAL_ID, "Админ ланч", 510);
  public static final Meal restMeal2 = new Meal(REST_MEAL_ID + 1, "Админ ужин", 1500);

  public static final Set<Meal> meals = Set.of(restMeal1, restMeal2);

  static {
    restaurant1.setMeals(meals);
    restaurant2.setMeals(Set.of(restMeal1, restMeal2));
  }

  public static Meal getNew() {
    return new Meal(null, "Созданный ужин", 300);
  }

  public static Meal getUpdated() {
    return new Meal(MEAL1_ID, "Обновленный завтрак", 200);
  }
}
