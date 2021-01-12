package ru.andrewalehin.restaurantrating;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import ru.andrewalehin.restaurantrating.model.Role;
import ru.andrewalehin.restaurantrating.model.User;
import ru.andrewalehin.restaurantrating.util.JsonUtil;

public class UserTestData {

  public static final TestMatcher<User> USER_MATCHER = TestMatcher
      .usingIgnoringFieldsComparator(User.class, "registered", "meals", "password");

  public static TestMatcher<User> USER_WITH_MEALS_MATCHER =
      TestMatcher.usingAssertions(User.class,
//     No need use ignoringAllOverriddenEquals, see https://assertj.github.io/doc/#breaking-changes
          (a, e) -> assertThat(a).usingRecursiveComparison()
              .ignoringFields("registered", "meals.user", "password").isEqualTo(e),
          (a, e) -> {
            throw new UnsupportedOperationException();
          });

  public static final int USER_ID = 1;
  public static final int ADMIN_ID = 2;
  public static final int NOT_FOUND = 100;

  public static final User user = new User(USER_ID, "User", "user@yandex.ru", "password",
      Role.USER);
  public static final User admin = new User(ADMIN_ID, "Admin", "admin@gmail.com", "admin",
      Role.ADMIN, Role.USER);

  public static final Set<User> users = Set.of(user, admin);


  public static User getNew() {
    return new User(null, "New", "new@gmail.com", "newPass", false, new Date(),
        Collections.singleton(Role.USER));
  }

  public static User getUpdated() {
    User updated = new User(user);
    updated.setName("UpdatedName");
    updated.setPassword("newPass");
    updated.setEnabled(false);
    updated.setRoles(Collections.singletonList(Role.ADMIN));
    return updated;
  }

  public static String jsonWithPassword(User user, String passw) {
    return JsonUtil.writeAdditionProps(user, "password", passw);
  }
}
