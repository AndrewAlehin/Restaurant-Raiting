package ru.andrewalehin.restaurantrating.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.andrewalehin.restaurantrating.model.Meal;

@Transactional(readOnly = true)
public interface MealRepository extends BaseRepository<Meal> {

  @Modifying
  @Transactional
  @Query("DELETE FROM Meal m WHERE m.id=:id AND m.restaurant.id=:restId")
  int delete(int id, int restId);

  @Query("SELECT m FROM Meal m WHERE m.restaurant.id=:restId ORDER BY m.name DESC")
  List<Meal> getAll(int restId);

  @Query("SELECT m FROM Meal m WHERE m.id = :id and m.restaurant.id = :restId")
  Optional<Meal> get(int id, int restId);
}