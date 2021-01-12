package ru.andrewalehin.restaurantrating.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.andrewalehin.restaurantrating.model.User;

@Transactional(readOnly = true)
public interface UserRepository extends BaseRepository<User> {

  @Transactional
  @Modifying
  @Query("DELETE FROM User u WHERE u.id=:id")
  int delete(@Param("id") int id);

  Optional<User> getByEmail(String email);
}