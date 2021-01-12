package ru.andrewalehin.restaurantrating.model;

import java.time.LocalDate;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "restaurants", uniqueConstraints = {@UniqueConstraint(columnNames = {"name",
    "date"}, name = "restaurants_unique_name_date_idx")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true, exclude = {"votes"})
public class Restaurant extends AbstractNamedEntity {

  @Column(name = "date", nullable = false)
  @NotNull
  private LocalDate date;

  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinTable(name = "votes", joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "rest_id"),
      uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "rest_id"},
          name = "users_unique_rest_idx")})
  private Set<User> votes;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant")
  @OrderBy("name DESC")
  @OnDelete(action = OnDeleteAction.CASCADE) //https://stackoverflow.com/a/44988100/548473
  private Set<Meal> meals;

  public Restaurant(Integer id, String name,
      @NotNull LocalDate date, Set<User> votes, Set<Meal> meals) {
    super(id, name);
    this.date = date;
    this.votes = votes;
    this.meals = meals;
  }

  public Restaurant(Integer id, String name,
      @NotNull LocalDate date, Set<User> votes) {
    this(id, name, date, votes, null);
  }

  public Restaurant(Integer id, String name,
      @NotNull LocalDate date) {
    this(id, name, date, null, null);
  }

  public Restaurant(String name,
      @NotNull LocalDate date) {
    this(null, name, date, null, null);
  }
}
