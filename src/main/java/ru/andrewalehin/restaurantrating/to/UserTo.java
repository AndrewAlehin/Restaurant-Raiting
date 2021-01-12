package ru.andrewalehin.restaurantrating.to;

import static org.hibernate.validator.constraints.SafeHtml.WhiteListType.NONE;

import java.io.Serial;
import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.hibernate.validator.constraints.SafeHtml;
import ru.andrewalehin.restaurantrating.HasIdAndEmail;

@Value
@EqualsAndHashCode(callSuper = true)
public class UserTo extends BaseTo implements HasIdAndEmail, Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @NotBlank
  @Size(min = 2, max = 100)
  @SafeHtml(whitelistType = NONE)
  String name;

  @Email
  @NotBlank
  @Size(max = 100)
  @SafeHtml(whitelistType = NONE) // https://stackoverflow.com/questions/17480809
  String email;

  @NotBlank
  @Size(min = 5, max = 32)
  String password;

  public UserTo(Integer id, String name, String email, String password) {
    super(id);
    this.name = name;
    this.email = email;
    this.password = password;
  }
}
