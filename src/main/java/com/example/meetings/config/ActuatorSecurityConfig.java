package com.example.meetings.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@Order(0)
public class ActuatorSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService actuatorUserDetailsService;

    public ActuatorSecurityConfig(
            @Qualifier("actuatorUserDetailsService") UserDetailsService actuatorUserDetailsService
    ) {
        this.actuatorUserDetailsService = actuatorUserDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatcher(EndpointRequest.toAnyEndpoint())
                .authorizeRequests()
                .requestMatchers(EndpointRequest.to("info")).permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .userDetailsService(actuatorUserDetailsService)
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable();
    }

    @Configuration
    static class ActuatorUserDetailsServiceConfig {

        @Bean
        UserDetailsService actuatorUserDetailsService(
                PasswordEncoder passwordEncoder,
                @Value("${management.logfile.basic-auth.username}") String username,
                @Value("${management.logfile.basic-auth.password}") String password
        ) {
            UserDetails user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .roles("ACTUATOR")
                    .build();

            return new InMemoryUserDetailsManager(user);
        }
    }
}
