package ru.andrewalehin.restaurantrating.web.user;


import static ru.andrewalehin.restaurantrating.util.ValidationUtil.assureIdConsistent;
import static ru.andrewalehin.restaurantrating.util.ValidationUtil.checkSingleModification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import ru.andrewalehin.restaurantrating.HasId;
import ru.andrewalehin.restaurantrating.model.User;
import ru.andrewalehin.restaurantrating.repository.UserRepository;
import ru.andrewalehin.restaurantrating.util.UserUtil;

@Slf4j
public abstract class AbstractUserController {

  @Autowired
  protected UserRepository repository;

  @Autowired
  private UniqueMailValidator emailValidator;

  @Autowired
  private LocalValidatorFactoryBean defaultValidator;

  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(emailValidator);
  }

  public ResponseEntity<User> get(int id) {
    log.info("get {}", id);
    return ResponseEntity.of(repository.findById(id));
  }

  @CacheEvict(value = "users", allEntries = true)
  public void delete(int id) {
    log.info("delete {}", id);
    checkSingleModification(repository.delete(id), "User id=" + id + " not found");
  }

  protected User prepareAndSave(User user) {
    return repository.save(UserUtil.prepareToSave(user));
  }

  protected void validateBeforeUpdate(HasId user, int id) throws BindException {
    assureIdConsistent(user, id);
    DataBinder binder = new DataBinder(user);
    binder.addValidators(emailValidator, defaultValidator);
    binder.validate();
    if (binder.getBindingResult().hasErrors()) {
      throw new BindException(binder.getBindingResult());
    }
  }
}