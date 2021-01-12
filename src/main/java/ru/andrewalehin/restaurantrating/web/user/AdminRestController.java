package ru.andrewalehin.restaurantrating.web.user;

import static ru.andrewalehin.restaurantrating.util.ValidationUtil.checkNew;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.andrewalehin.restaurantrating.model.User;

@RestController
@RequestMapping(value = AdminRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@CacheConfig(cacheNames = "users")
public class AdminRestController extends AbstractUserController {

  static final String REST_URL = "/rest/admin/users";

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<User> get(@PathVariable int id) {
    return super.get(id);
  }

  @Override
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable int id) {
    super.delete(id);
  }

  @GetMapping
  @Cacheable
  public List<User> getAll() {
    log.info("getAll");
    return repository.findAll(Sort.by(Sort.Direction.ASC, "name", "email"));
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @CacheEvict(allEntries = true)
  public ResponseEntity<User> createWithLocation(@Valid @RequestBody User user) {
    log.info("create {}", user);
    checkNew(user);
    User created = prepareAndSave(user);
    URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path(REST_URL + "/{id}")
        .buildAndExpand(created.getId()).toUri();
    return ResponseEntity.created(uriOfNewResource).body(created);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @CacheEvict(allEntries = true)
  public void update(@RequestBody User user, @PathVariable int id) throws BindException {
    validateBeforeUpdate(user, id);
    log.info("update {} with id={}", user, id);
    prepareAndSave(user);
  }

  @GetMapping("/by")
  public ResponseEntity<User> getByEmail(@RequestParam String email) {
    log.info("getByEmail {}", email);
    return ResponseEntity.of(repository.getByEmail(email));
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Transactional
  @CacheEvict(allEntries = true)
  public void enable(@PathVariable int id, @RequestParam boolean enabled) {
    log.info(enabled ? "enable {}" : "disable {}", id);
    User user = repository.getExisted(id);
    user.setEnabled(enabled);
  }
}