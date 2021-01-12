package ru.andrewalehin.restaurantrating.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.andrewalehin.restaurantrating.model.Meal;
import ru.andrewalehin.restaurantrating.to.MealTo;

@UtilityClass
public class MealsUtil {

  public static List<MealTo> getTos(Collection<Meal> meals) {
    return meals.stream()
        .map(meal -> new MealTo(meal.getId(), meal.getName(), meal.getPrice()))
        .collect(Collectors.toList());
  }
}
