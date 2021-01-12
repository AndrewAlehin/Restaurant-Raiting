package ru.andrewalehin.restaurantrating.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "meals", uniqueConstraints = {@UniqueConstraint(columnNames = {"rest_id",
    "name"}, name = "meals_unique_rest_id_name_idx")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true, exclude = {"restaurant"})
public class Meal extends AbstractNamedEntity {

  @Column(name = "price", nullable = false)
  @Range(min = 10, max = 50000)
  private int price;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rest_id")
  @JsonIgnore
  private Restaurant restaurant;

  public Meal(Integer id, String name, int price) {
    super(id, name);
    this.price = price;
  }

  public Meal(String name, int price) {
    this(null, name, price);
  }
}
