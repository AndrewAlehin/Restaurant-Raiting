package ru.andrewalehin.restaurantrating.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.andrewalehin.restaurantrating.model.Restaurant;

@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant> {

  @Modifying
  @Transactional
  @Query("DELETE FROM Restaurant r WHERE r.id=:id")
  int delete(int id);

  @EntityGraph(attributePaths = {"meals", "votes"}, type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT r FROM Restaurant r WHERE r.date=:date ORDER BY r.name DESC")
  List<Restaurant> getAll(LocalDate date);

  @Query("SELECT r FROM Restaurant r JOIN FETCH r.votes WHERE r.date=:date ORDER BY r.name DESC")
  List<Restaurant> getAllWithVotes(LocalDate date);

  @Query("SELECT r FROM Restaurant r WHERE r.id = :id")
  Optional<Restaurant> get(int id);
}