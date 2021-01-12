package ru.andrewalehin.restaurantrating.web.restaurant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.andrewalehin.restaurantrating.RestaurantTestData.RESTAURANT3_ID;
import static ru.andrewalehin.restaurantrating.RestaurantTestData.RESTAURANT4_ID;
import static ru.andrewalehin.restaurantrating.RestaurantTestData.RESTAURANT5_ID;
import static ru.andrewalehin.restaurantrating.RestaurantTestData.RESTAURANT_TO_MATCHER;
import static ru.andrewalehin.restaurantrating.RestaurantTestData.restaurant1;
import static ru.andrewalehin.restaurantrating.RestaurantTestData.restaurants;
import static ru.andrewalehin.restaurantrating.TestUtil.userHttpBasic;
import static ru.andrewalehin.restaurantrating.UserTestData.user;
import static ru.andrewalehin.restaurantrating.util.RestaurantsUtil.getTos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.andrewalehin.restaurantrating.repository.RestaurantRepository;
import ru.andrewalehin.restaurantrating.web.AbstractControllerTest;

class RestaurantRestControllerTest extends AbstractControllerTest {

  private static final String REST_URL = RestaurantRestController.REST_URL + '/';

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Test
  void getAll() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL)
        .param("date", restaurant1.getDate().toString())
        .with(userHttpBasic(user)))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(RESTAURANT_TO_MATCHER.contentJson(getTos(restaurants)));
  }

  @Test
  void increaseVote() throws Exception {
    int vote = restaurantRepository.get(RESTAURANT3_ID).get().getVotes().size();
    perform(MockMvcRequestBuilders.get(REST_URL + "increase/" + RESTAURANT3_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(user)))
        .andDo(print())
        .andExpect(status().isNoContent());
    int voteUpdate = restaurantRepository.get(RESTAURANT3_ID).get().getVotes().size();
    assertEquals(vote + 1, voteUpdate);
  }

  @Test
  void updateVote() throws Exception {
    perform(MockMvcRequestBuilders.get(REST_URL + "increase/" + RESTAURANT5_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(user)))
        .andDo(print())
        .andExpect(status().isNoContent());

    int voteRest5 = restaurantRepository.get(RESTAURANT5_ID).get().getVotes().size();
    int voteRest4 = restaurantRepository.get(RESTAURANT4_ID).get().getVotes().size();

    perform(MockMvcRequestBuilders.get(REST_URL + "increase/" + RESTAURANT4_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(user)))
        .andDo(print())
        .andExpect(status().isNoContent());

    int voteRest5Update = restaurantRepository.get(RESTAURANT5_ID).get().getVotes().size();
    int voteRest4Update = restaurantRepository.get(RESTAURANT4_ID).get().getVotes().size();
    assertEquals(voteRest5 - 1, voteRest5Update);
    assertEquals(voteRest4 + 1, voteRest4Update);
  }
}