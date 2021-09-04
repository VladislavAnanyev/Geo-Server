package com.example.mywebquizengine.Security;
import com.example.mywebquizengine.Model.JWTFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import java.util.Calendar;
import java.util.GregorianCalendar;



@Configuration
@EnableWebSecurity(debug=true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Qualifier("userService")
    @Autowired
    private UserDetailsService userDetailsService;



    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider()  {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }


    @Configuration
    @Order(1)
    public static class ApiConfigurationAdapter extends
            WebSecurityConfigurerAdapter {

        @Autowired
        private JWTFilter jwtFilter;

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.csrf().disable();

            http.antMatcher("/api/**")

                    .authorizeRequests()
                    .antMatchers("/api/register", "/api/jwt", "/activate/*", "/img/**",
                            "/api/quizzes", "/reg", "/geo", "/androidSign", "/api/signin",
                            "/", "/signin", "/checkyandex", "/h2-console/**", "/.well-known/pki-validation/**",
                            "/static/forgotPassword.js", "/static/changePassword.js", "/update/userinfo/pswrdwithoutauth",
                            "/updatepass/**", "/pass/**", "/updatepassword/{activationCOde}", "/yandex_135f209071de02b1.html").permitAll()
                    .anyRequest().authenticated()

                    .and()
                    .formLogin()

                    /*.loginPage("/api/jwt")*//*.successForwardUrl("/api/jwt")*/.and().logout().logoutUrl("/api/logout").permitAll()

                    .and().oauth2Login().defaultSuccessUrl("/loginSuccess")
                    .permitAll()
                    .and()
                    .logout()
                    .permitAll()
                    .and()
                    //.rememberMe().and()
                    // for h2-console correct view
                    .headers()
                    .frameOptions()
                    .sameOrigin().and()
                    .requiresChannel()
                    .anyRequest()
                    .requiresSecure().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        }
    }



    @Order(2)
    @Configuration
    public static class FormConfigurationAdapter extends
            WebSecurityConfigurerAdapter {


        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.csrf().disable();

            http

                    .authorizeRequests()
                    .antMatchers("/api/register", "/activate/*", "/img/**",
                            "/api/quizzes", "/reg",  "/androidSign",
                            "/", "/signin", "/checkyandex", "/h2-console/**", "/.well-known/pki-validation/**",
                            "/static/forgotPassword.js", "/static/changePassword.js", "/update/userinfo/pswrdwithoutauth",
                            "/updatepass/**", "/pass/**", "/updatepassword/{activationCOde}", "/yandex_135f209071de02b1.html").permitAll()
                    .anyRequest().authenticated()

                    .and()
                    .formLogin()

                    .loginPage("/signin").defaultSuccessUrl("/profile").failureUrl("/signin?error")

                    .and().oauth2Login().defaultSuccessUrl("/loginSuccess")
                    .permitAll()
                    .and()
                    .logout()
                    .permitAll()
                    .and()
                    .rememberMe().and()
                    // for h2-console correct view
                    .headers()
                    .frameOptions()
                    .sameOrigin().and()
                    .requiresChannel()
                    .anyRequest()
                    .requiresSecure();
    }

}
}
