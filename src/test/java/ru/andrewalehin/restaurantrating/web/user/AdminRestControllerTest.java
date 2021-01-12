package ru.andrewalehin.restaurantrating.web.user;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.andrewalehin.restaurantrating.TestUtil.readFromJson;
import static ru.andrewalehin.restaurantrating.TestUtil.userHttpBasic;
import static ru.andrewalehin.restaurantrating.UserTestData.ADMIN_ID;
import static ru.andrewalehin.restaurantrating.UserTestData.NOT_FOUND;
import static ru.andrewalehin.restaurantrating.UserTestData.USER_ID;
import static ru.andrewalehin.restaurantrating.UserTestData.USER_MATCHER;
import static ru.andrewalehin.restaurantrating.UserTestData.admin;
import static ru.andrewalehin.restaurantrating.UserTestData.getNew;
import static ru.andrewalehin.restaurantrating.UserTestData.getUpdated;
import static ru.andrewalehin.restaurantrating.UserTestData.jsonWithPassword;
import static ru.andrewalehin.restaurantrating.UserTestData.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.andrewalehin.restaurantrating.error.NotFoundException;
import ru.andrewalehin.restaurantrating.model.Role;
import ru.andrewalehin.restaurantrating.model.User;
import ru.andrewalehin.restaurantrating.repository.UserRepository;
import ru.andrewalehin.restaurantrating.web.AbstractControllerTest;
import ru.andrewalehin.restaurantrating.web.GlobalExceptionHandler;

class AdminRestControllerTest extends AbstractControllerTest {

  private static final String REST_URL = AdminRestController.REST_URL + '/';

  @Autowired
  private UserRepository userRepository;

  @Test
  void get() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_ID)
        .with(userHttpBasic(admin)))
        .andExpect(status().isOk())
        .andDo(print())
        // https://jira.spring.io/browse/SPR-14472
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(USER_MATCHER.contentJson(admin));
  }

  @Test
  void getNotFound() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND)
        .with(userHttpBasic(admin)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void getByEmail() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL + "by?email=" + admin.getEmail())
        .with(userHttpBasic(admin)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(USER_MATCHER.contentJson(admin));
  }

  @Test
  void delete() throws Exception {
    perform(MockMvcRequestBuilders.delete(REST_URL + USER_ID)
        .with(userHttpBasic(admin)))
        .andDo(print())
        .andExpect(status().isNoContent());
    assertThrows(NotFoundException.class, () -> userRepository.getExisted(USER_ID));
  }

  @Test
  void deleteNotFound() throws Exception {
    perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND)
        .with(userHttpBasic(admin)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void getUnAuth() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getForbidden() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL)
        .with(userHttpBasic(user)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update() throws Exception {
    User updated = getUpdated();
    updated.setId(null);
    perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(admin))
        .content(jsonWithPassword(updated, "newPass")))
        .andDo(print())
        .andExpect(status().isNoContent());

    USER_MATCHER.assertMatch(userRepository.getExisted(USER_ID), getUpdated());
  }

  @Test
  void createWithLocation() throws Exception {
    User newUser = getNew();
    ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(admin))
        .content(jsonWithPassword(newUser, "newPass")))
        .andExpect(status().isCreated());

    User created = readFromJson(action, User.class);
    int newId = created.id();
    newUser.setId(newId);
    USER_MATCHER.assertMatch(created, newUser);
    USER_MATCHER.assertMatch(userRepository.getExisted(newId), newUser);
  }

  @Test
  void getAll() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL)
        .with(userHttpBasic(admin)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(USER_MATCHER.contentJson(admin, user));
  }

  @Test
  void enable() throws Exception {
    perform(MockMvcRequestBuilders.patch(REST_URL + USER_ID)
        .param("enabled", "false")
        .contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(admin)))
        .andDo(print())
        .andExpect(status().isNoContent());

    assertFalse(userRepository.getExisted(USER_ID).isEnabled());
  }

  @Test
  void createInvalid() throws Exception {
    User invalid = new User(null, null, "", "newPass", Role.USER, Role.ADMIN);
    perform(MockMvcRequestBuilders.post(REST_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(admin))
        .content(jsonWithPassword(invalid, "newPass")))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void updateInvalid() throws Exception {
    User invalid = new User(user);
    invalid.setName("");
    perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(admin))
        .content(jsonWithPassword(invalid, "password")))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void updateHtmlUnsafe() throws Exception {
    User updated = new User(user);
    updated.setName("<script>alert(123)</script>");
    perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(admin))
        .content(jsonWithPassword(updated, "password")))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @Transactional(propagation = Propagation.NEVER)
  void updateDuplicate() throws Exception {
    User updated = new User(user);
    updated.setEmail("admin@gmail.com");
    perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(admin))
        .content(jsonWithPassword(updated, "password")))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(
            content().string(containsString(GlobalExceptionHandler.EXCEPTION_DUPLICATE_EMAIL)));
  }

  @Test
  @Transactional(propagation = Propagation.NEVER)
  void createDuplicate() throws Exception {
    User expected = new User(null, "New", "user@yandex.ru", "newPass", Role.USER, Role.ADMIN);
    perform(MockMvcRequestBuilders.post(REST_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(admin))
        .content(jsonWithPassword(expected, "newPass")))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(
            content().string(containsString(GlobalExceptionHandler.EXCEPTION_DUPLICATE_EMAIL)));
  }
}