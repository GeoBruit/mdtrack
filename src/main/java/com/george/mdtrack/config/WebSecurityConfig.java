package com.george.mdtrack.config;

import com.george.mdtrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {


    UserService userService;

    public WebSecurityConfig(@Autowired UserService userService) {
        this.userService = userService;
    }

    //Create new password encoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures and provides an {@link AuthenticationProvider} bean for authentication purposes.
     * This method uses {@link DaoAuthenticationProvider} to integrate a user details service
     * and password encoder for authenticating users.
     *
     * @return an instance of {@link AuthenticationProvider} configured with a user details service
     *         and password encoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/", "/register", "/login", "/register-form", "/register", "/images/**", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated())
                .formLogin((login) -> login

                        .loginPage("/login-form")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true))
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login-form")
                        .permitAll());

        return http.build();
    }
}
