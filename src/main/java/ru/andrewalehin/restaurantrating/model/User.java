package ru.andrewalehin.restaurantrating.model;

import static org.hibernate.validator.constraints.SafeHtml.WhiteListType.NONE;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.util.CollectionUtils;
import ru.andrewalehin.restaurantrating.HasIdAndEmail;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email", name = "users_unique_email_idx")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true, exclude = {"password"})
public class User extends AbstractNamedEntity implements HasIdAndEmail {

  @Column(name = "email", nullable = false, unique = true)
  @Email
  @NotBlank
  @Size(max = 100)
  @SafeHtml(whitelistType = NONE)  // https://stackoverflow.com/questions/17480809
  private String email;

  @Column(name = "password", nullable = false)
  @NotBlank
  @Size(min = 5, max = 100)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @Column(name = "enabled", nullable = false, columnDefinition = "bool default true")
  private boolean enabled = true;

  @Column(name = "registered", nullable = false, columnDefinition = "timestamp default now()")
  @NotNull
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date registered = new Date();

  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
      uniqueConstraints = {
          @UniqueConstraint(columnNames = {"user_id", "role"}, name = "user_roles_unique_idx")})
  @Column(name = "role")
  @ElementCollection(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id") //https://stackoverflow.com/a/62848296/548473
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Set<Role> roles;

  public User(User u) {
    this(u.getId(), u.getName(), u.getEmail(), u.getPassword(), u.isEnabled(), u.getRegistered(),
        u.getRoles());
  }

  public User(Integer id, String name, String email, String password, Role role, Role... roles) {
    this(id, name, email, password, true, new Date(), EnumSet.of(role, roles));
  }

  public User(Integer id, String name, String email, String password, boolean enabled,
      Date registered, Collection<Role> roles) {
    super(id, name);
    this.email = email;
    this.password = password;
    this.enabled = enabled;
    this.registered = registered;
    setRoles(roles);
  }

  public void setRoles(Collection<Role> roles) {
    this.roles =
        CollectionUtils.isEmpty(roles) ? EnumSet.noneOf(Role.class) : EnumSet.copyOf(roles);
  }
}