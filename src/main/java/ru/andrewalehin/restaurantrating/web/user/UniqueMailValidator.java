package ru.andrewalehin.restaurantrating.web.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.andrewalehin.restaurantrating.HasIdAndEmail;
import ru.andrewalehin.restaurantrating.repository.UserRepository;
import ru.andrewalehin.restaurantrating.web.GlobalExceptionHandler;

@Component
@AllArgsConstructor
public class UniqueMailValidator implements Validator {

  private final UserRepository repository;

  @Override
  public boolean supports(Class<?> clazz) {
    return HasIdAndEmail.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    HasIdAndEmail user = ((HasIdAndEmail) target);
    if (StringUtils.hasText(user.getEmail())) {
      if (repository.getByEmail(user.getEmail().toLowerCase())
          .filter(u -> !u.getId().equals(user.getId())).isPresent()) {
        errors.rejectValue("email", "", GlobalExceptionHandler.EXCEPTION_DUPLICATE_EMAIL);
      }
    }
  }
}
