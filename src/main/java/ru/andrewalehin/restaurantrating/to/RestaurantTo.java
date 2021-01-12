package ru.andrewalehin.restaurantrating.to;

import java.time.LocalDate;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class RestaurantTo extends BaseTo {

  String name;

  LocalDate date;

  int votes;

  List<MealTo> meals;

  public RestaurantTo(Integer id, String name, LocalDate date, int votes,
      List<MealTo> meals) {
    super(id);
    this.name = name;
    this.date = date;
    this.votes = votes;
    this.meals = meals;
  }
}
