package ru.andrewalehin.restaurantrating.web.restaurant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.andrewalehin.restaurantrating.RestaurantTestData.RESTAURANT1_ID;
import static ru.andrewalehin.restaurantrating.RestaurantTestData.RESTAURANT_MATCHER;
import static ru.andrewalehin.restaurantrating.RestaurantTestData.REST_RESTAURANT_ID;
import static ru.andrewalehin.restaurantrating.RestaurantTestData.restaurant1;
import static ru.andrewalehin.restaurantrating.TestUtil.readFromJson;
import static ru.andrewalehin.restaurantrating.TestUtil.userHttpBasic;
import static ru.andrewalehin.restaurantrating.UserTestData.admin;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.andrewalehin.restaurantrating.RestaurantTestData;
import ru.andrewalehin.restaurantrating.model.Restaurant;
import ru.andrewalehin.restaurantrating.repository.RestaurantRepository;
import ru.andrewalehin.restaurantrating.util.JsonUtil;
import ru.andrewalehin.restaurantrating.web.AbstractControllerTest;

class RestaurantAdminRestControllerTest extends AbstractControllerTest {

  private static final String REST_URL = RestaurantAdminRestController.REST_URL + '/';

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Test
  void get() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL + RESTAURANT1_ID)
        .with(userHttpBasic(admin)))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(RESTAURANT_MATCHER.contentJson(restaurant1));
  }

  @Test
  void getUnauth() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL + RESTAURANT1_ID))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getNotFound() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL + REST_RESTAURANT_ID)
        .with(userHttpBasic(admin)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void delete() throws Exception {
    perform(MockMvcRequestBuilders.delete(REST_URL + RESTAURANT1_ID)
        .with(userHttpBasic(admin)))
        .andExpect(status().isNoContent());
    assertFalse(restaurantRepository.get(RESTAURANT1_ID).isPresent());
  }

  @Test
  void deleteNotFound() throws Exception {
    perform(MockMvcRequestBuilders.delete(REST_URL + REST_RESTAURANT_ID)
        .with(userHttpBasic(admin)))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void update() throws Exception {
    Restaurant updated = RestaurantTestData.getUpdated();
    perform(MockMvcRequestBuilders.put(REST_URL + RESTAURANT1_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtil.writeValue(updated))
        .with(userHttpBasic(admin)))
        .andExpect(status().isNoContent());

    RESTAURANT_MATCHER.assertMatch(restaurantRepository.get(RESTAURANT1_ID).get(), updated);
  }

  @Test
  void updateInvalid() throws Exception {
    Restaurant invalid = new Restaurant(RESTAURANT1_ID, null, null);
    perform(MockMvcRequestBuilders.put(REST_URL + RESTAURANT1_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtil.writeValue(invalid))
        .with(userHttpBasic(admin)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void updateHtmlUnsafe() throws Exception {
    Restaurant invalid = new Restaurant(RESTAURANT1_ID, "<script>alert(123)</script>",
        LocalDate.now());
    perform(MockMvcRequestBuilders.put(REST_URL + RESTAURANT1_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtil.writeValue(invalid))
        .with(userHttpBasic(admin)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void createWithLocation() throws Exception {
    Restaurant newRestaurant = RestaurantTestData.getNew();
    ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtil.writeValue(newRestaurant))
        .with(userHttpBasic(admin)));

    Restaurant created = readFromJson(action, Restaurant.class);
    int newId = created.id();
    newRestaurant.setId(newId);
    RESTAURANT_MATCHER.assertMatch(created, newRestaurant);
    RESTAURANT_MATCHER.assertMatch(restaurantRepository.get(newId).get(), newRestaurant);
  }

  @Test
  void createInvalid() throws Exception {
    Restaurant invalid = new Restaurant(null, null, null);
    perform(MockMvcRequestBuilders.post(REST_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtil.writeValue(invalid))
        .with(userHttpBasic(admin)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }
}