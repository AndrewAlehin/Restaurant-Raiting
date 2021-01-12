package ru.andrewalehin.restaurantrating.web.user;

import static ru.andrewalehin.restaurantrating.util.ValidationUtil.checkNew;

import java.net.URI;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.andrewalehin.restaurantrating.model.User;
import ru.andrewalehin.restaurantrating.to.UserTo;
import ru.andrewalehin.restaurantrating.util.UserUtil;
import ru.andrewalehin.restaurantrating.web.AuthUser;

@RestController
@RequestMapping(value = ProfileRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@CacheConfig(cacheNames = "users")
public class ProfileRestController extends AbstractUserController {

  static final String REST_URL = "/rest/profile";

  @GetMapping
  public HttpEntity<User> get(@AuthenticationPrincipal AuthUser authUser) {
    return super.get(authUser.id());
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@AuthenticationPrincipal AuthUser authUser) {
    super.delete(authUser.id());
  }

  @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @CacheEvict(allEntries = true)
  public ResponseEntity<User> register(@Valid @RequestBody UserTo userTo) {
    log.info("register {}", userTo);
    checkNew(userTo);
    User created = prepareAndSave(UserUtil.createNewFromTo(userTo));
    URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path(REST_URL).build().toUri();
    return ResponseEntity.created(uriOfNewResource).body(created);
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Transactional
  @CacheEvict(allEntries = true)
  public void update(@RequestBody UserTo userTo, @AuthenticationPrincipal AuthUser authUser)
      throws BindException {
    validateBeforeUpdate(userTo, authUser.id());
    User user = repository.getExisted(userTo.id());
    prepareAndSave(UserUtil.updateFromTo(user, userTo));
  }
}