package ru.andrewalehin.restaurantrating.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.andrewalehin.restaurantrating.model.Restaurant;
import ru.andrewalehin.restaurantrating.model.User;
import ru.andrewalehin.restaurantrating.to.RestaurantTo;

@UtilityClass
public class RestaurantsUtil {

  public static List<RestaurantTo> getTos(Collection<Restaurant> restaurants) {
    return restaurants.stream()
        .map(restaurant -> new RestaurantTo(restaurant.getId(), restaurant.getName(),
            restaurant.getDate(), restaurant.getVotes().size(),
            MealsUtil.getTos(restaurant.getMeals())))
        .sorted(Comparator.comparing(RestaurantTo::getName))
        .collect(Collectors.toList());
  }

  public static Restaurant findUserVote(List<Restaurant> allWithVotes, User user) {
    int userId = user.id();
    return allWithVotes.stream()
        .filter(r -> r.getVotes().stream().anyMatch(u -> u.id() == userId)).findFirst()
        .orElse(null);
  }
}
