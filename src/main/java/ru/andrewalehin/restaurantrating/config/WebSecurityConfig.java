package ru.andrewalehin.restaurantrating.config;

import static ru.andrewalehin.restaurantrating.util.UserUtil.PASSWORD_ENCODER;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.andrewalehin.restaurantrating.model.Role;
import ru.andrewalehin.restaurantrating.model.User;
import ru.andrewalehin.restaurantrating.repository.UserRepository;
import ru.andrewalehin.restaurantrating.web.AuthUser;

@Configuration
@EnableWebSecurity
@Slf4j
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserRepository userRepository;

  @Bean
  public UserDetailsService userDetailsService() {
    return email -> {
      log.debug("Authenticating '{}'", email);
      Optional<User> optionalUser = userRepository.getByEmail(email);
      return new AuthUser(optionalUser.orElseThrow(
          () -> new UsernameNotFoundException("User '" + email + "' was not found")));
    };
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService())
        .passwordEncoder(PASSWORD_ENCODER);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/rest/admin/**").hasRole(Role.ADMIN.name())
        .antMatchers("/rest/profile/register").anonymous()
        .antMatchers("/rest/**").authenticated()
        .and().httpBasic()
        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().csrf().disable();
  }
}