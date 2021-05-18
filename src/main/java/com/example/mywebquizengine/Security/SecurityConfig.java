package com.example.mywebquizengine.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



@Configuration
@EnableWebSecurity(debug=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Qualifier("userService")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http
                .authorizeRequests()
                    .antMatchers("/api/register", "/activate/*",
                            "/api/quizzes", "/reg",
                            "/","/signin", "/checkyandex", "/h2-console/**").permitAll()
                    .anyRequest().authenticated()
                //.antMatchers("/api/quizzes/**").authenticated()
                //.and().httpBasic();
                .and()
                    .formLogin()
                    .loginPage("/signin")
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
                .sameOrigin();
                //.and().oauth2Login();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider()  {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

}
