package ru.andrewalehin.restaurantrating.web.meal;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.andrewalehin.restaurantrating.MealTestData.MEAL1_ID;
import static ru.andrewalehin.restaurantrating.MealTestData.MEAL_MATCHER;
import static ru.andrewalehin.restaurantrating.MealTestData.MEAL_TO_MATCHER;
import static ru.andrewalehin.restaurantrating.MealTestData.REST_MEAL_ID;
import static ru.andrewalehin.restaurantrating.MealTestData.meal2;
import static ru.andrewalehin.restaurantrating.MealTestData.meals;
import static ru.andrewalehin.restaurantrating.RestaurantTestData.RESTAURANT1_ID;
import static ru.andrewalehin.restaurantrating.RestaurantTestData.RESTAURANT2_ID;
import static ru.andrewalehin.restaurantrating.TestUtil.readFromJson;
import static ru.andrewalehin.restaurantrating.TestUtil.userHttpBasic;
import static ru.andrewalehin.restaurantrating.UserTestData.USER_ID;
import static ru.andrewalehin.restaurantrating.UserTestData.admin;
import static ru.andrewalehin.restaurantrating.util.MealsUtil.getTos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.andrewalehin.restaurantrating.MealTestData;
import ru.andrewalehin.restaurantrating.model.Meal;
import ru.andrewalehin.restaurantrating.repository.MealRepository;
import ru.andrewalehin.restaurantrating.util.JsonUtil;
import ru.andrewalehin.restaurantrating.web.AbstractControllerTest;

class MealRestControllerTest extends AbstractControllerTest {

  private static final String REST_URL = MealRestController.REST_URL + '/';

  @Autowired
  private MealRepository mealRepository;

  @Test
  void get() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL + RESTAURANT1_ID + '/' + meal2.getId())
        .with(userHttpBasic(admin)))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(MEAL_MATCHER.contentJson(meal2));
  }

  @Test
  void getUnauth() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL + RESTAURANT1_ID + '/' + MEAL1_ID))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getNotFound() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL + RESTAURANT1_ID + '/' + REST_MEAL_ID)
        .with(userHttpBasic(admin)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void delete() throws Exception {
    perform(MockMvcRequestBuilders.delete(REST_URL + RESTAURANT1_ID + '/' + MEAL1_ID)
        .with(userHttpBasic(admin)))
        .andExpect(status().isNoContent());
    assertFalse(mealRepository.get(MEAL1_ID, USER_ID).isPresent());
  }

  @Test
  void deleteNotFound() throws Exception {
    perform(MockMvcRequestBuilders.delete(REST_URL + RESTAURANT1_ID + '/' + REST_MEAL_ID)
        .with(userHttpBasic(admin)))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void update() throws Exception {
    Meal updated = MealTestData.getUpdated();
    perform(MockMvcRequestBuilders.put(REST_URL + RESTAURANT1_ID + '/' + MEAL1_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtil.writeValue(updated))
        .with(userHttpBasic(admin)))
        .andExpect(status().isNoContent());

    MEAL_MATCHER.assertMatch(mealRepository.get(MEAL1_ID, USER_ID).get(), updated);
  }

  @Test
  void createWithLocation() throws Exception {
    Meal newMeal = MealTestData.getNew();
    ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + RESTAURANT1_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtil.writeValue(newMeal))
        .with(userHttpBasic(admin)));

    Meal created = readFromJson(action, Meal.class);
    int newId = created.id();
    newMeal.setId(newId);
    MEAL_MATCHER.assertMatch(created, newMeal);
    MEAL_MATCHER.assertMatch(mealRepository.get(newId, USER_ID).get(), newMeal);
  }

  @Test
  void getAll() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL + RESTAURANT2_ID)
        .with(userHttpBasic(admin)))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(MEAL_TO_MATCHER.contentJson(getTos(meals)));
  }


  @Test
  void createInvalid() throws Exception {
    Meal invalid = new Meal(null, 200);
    perform(MockMvcRequestBuilders.post(REST_URL + RESTAURANT1_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtil.writeValue(invalid))
        .with(userHttpBasic(admin)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void updateInvalid() throws Exception {
    Meal invalid = new Meal(MEAL1_ID, null, 6000);
    perform(MockMvcRequestBuilders.put(REST_URL + RESTAURANT1_ID + '/' + MEAL1_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtil.writeValue(invalid))
        .with(userHttpBasic(admin)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void updateHtmlUnsafe() throws Exception {
    Meal invalid = new Meal(MEAL1_ID, "<script>alert(123)</script>", 200);
    perform(MockMvcRequestBuilders.put(REST_URL + RESTAURANT1_ID + '/' + MEAL1_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtil.writeValue(invalid))
        .with(userHttpBasic(admin)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @Transactional(propagation = Propagation.NEVER)
//  TODO throw and check exception in controller
  void updateDuplicate() {
    Meal invalid = new Meal(MEAL1_ID, "Завтрак", 200);
    assertThrows(Exception.class, () ->
        perform(MockMvcRequestBuilders.put(REST_URL + RESTAURANT1_ID + '/' + MEAL1_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.writeValue(invalid))
            .with(userHttpBasic(admin)))
            .andDo(print())
    );
  }

  @Test
  @Transactional(propagation = Propagation.NEVER)
//  TODO throw and check exception in controller
  void createDuplicate() throws Exception {
    Meal invalid = new Meal(null, "Завтрак", 200);
    assertThrows(Exception.class, () ->
        perform(MockMvcRequestBuilders.post(REST_URL + RESTAURANT1_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.writeValue(invalid))
            .with(userHttpBasic(admin)))
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
    );
  }
}