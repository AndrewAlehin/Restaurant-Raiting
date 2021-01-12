package ru.andrewalehin.restaurantrating.to;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class MealTo extends BaseTo {

  String name;

  int price;

  public MealTo(Integer id, String name, int price) {
    super(id);
    this.name = name;
    this.price = price;
  }
}
