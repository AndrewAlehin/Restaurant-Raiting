package ru.andrewalehin.restaurantrating.web.user;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.andrewalehin.restaurantrating.TestUtil.readFromJson;
import static ru.andrewalehin.restaurantrating.TestUtil.userHttpBasic;
import static ru.andrewalehin.restaurantrating.UserTestData.USER_ID;
import static ru.andrewalehin.restaurantrating.UserTestData.USER_MATCHER;
import static ru.andrewalehin.restaurantrating.UserTestData.admin;
import static ru.andrewalehin.restaurantrating.UserTestData.user;
import static ru.andrewalehin.restaurantrating.web.user.ProfileRestController.REST_URL;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.andrewalehin.restaurantrating.model.User;
import ru.andrewalehin.restaurantrating.repository.UserRepository;
import ru.andrewalehin.restaurantrating.to.UserTo;
import ru.andrewalehin.restaurantrating.util.JsonUtil;
import ru.andrewalehin.restaurantrating.util.UserUtil;
import ru.andrewalehin.restaurantrating.web.AbstractControllerTest;
import ru.andrewalehin.restaurantrating.web.GlobalExceptionHandler;

class ProfileRestControllerTest extends AbstractControllerTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void get() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL)
        .with(userHttpBasic(user)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(USER_MATCHER.contentJson(user));
  }

  @Test
  void getUnAuth() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void delete() throws Exception {
    perform(MockMvcRequestBuilders.delete(REST_URL)
        .with(userHttpBasic(user)))
        .andExpect(status().isNoContent());
    USER_MATCHER.assertMatch(userRepository.findAll(), admin);
  }

  @Test
  void register() throws Exception {
    UserTo newTo = new UserTo(null, "newName", "newemail@ya.ru", "newPassword");
    User newUser = UserUtil.createNewFromTo(newTo);
    ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtil.writeValue(newTo)))
        .andDo(print())
        .andExpect(status().isCreated());

    User created = readFromJson(action, User.class);
    int newId = created.id();
    newUser.setId(newId);
    USER_MATCHER.assertMatch(created, newUser);
    USER_MATCHER.assertMatch(userRepository.getExisted(newId), newUser);
  }

  @Test
  void update() throws Exception {
    UserTo updatedTo = new UserTo(null, "newName", "user@yandex.ru", "newPassword");
    perform(MockMvcRequestBuilders.put(REST_URL).contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(user))
        .content(JsonUtil.writeValue(updatedTo)))
        .andDo(print())
        .andExpect(status().isNoContent());

    USER_MATCHER.assertMatch(userRepository.getExisted(USER_ID),
        UserUtil.updateFromTo(new User(user), updatedTo));
  }

  @Test
  void registerInvalid() throws Exception {
    UserTo newTo = new UserTo(null, null, null, null);
    perform(MockMvcRequestBuilders.post(REST_URL + "/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtil.writeValue(newTo)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void updateInvalid() throws Exception {
    UserTo updatedTo = new UserTo(null, null, "password", null);
    perform(MockMvcRequestBuilders.put(REST_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(user))
        .content(JsonUtil.writeValue(updatedTo)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void updateDuplicate() throws Exception {
    UserTo updatedTo = new UserTo(null, "newName", "admin@gmail.com", "newPassword");
    perform(MockMvcRequestBuilders.put(REST_URL).contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(user))
        .content(JsonUtil.writeValue(updatedTo)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(
            content().string(containsString(GlobalExceptionHandler.EXCEPTION_DUPLICATE_EMAIL)));
  }
}