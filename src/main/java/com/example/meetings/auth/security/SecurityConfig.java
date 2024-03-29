package com.example.meetings.auth.security;

import com.example.meetings.auth.security.handler.*;
import com.example.meetings.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.*;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.*;


@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    protected AuthService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }


    @Configuration
    @Order(1)
    public class ApiConfigurationAdapter extends
            WebSecurityConfigurerAdapter {

        @Autowired
        private JWTFilter jwtFilter;

        @Autowired
        private ApiLogoutHandler apiLogoutHandler;

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.csrf().disable();

            http.antMatcher("/api/v1/**")

                    .authorizeRequests()
                    .antMatchers("/api/v1/register", "/api/v1/jwt", "/img/**",
                            "/api/v1/quizzes", "/api/v1/signin", "/api/v1/googleauth", "/api/v1/signup",
                            "/api/v1/user/check-username", "/api/v1/user/send-change-password-code",
                            "/api/v1/signup/phone", "/api/v1/signin/phone",
                            "/api/v1/user/verify-password-code", "/api/v1/user/password").permitAll()

                    .anyRequest().authenticated()
                    .and()
                    .logout(logout -> logout.logoutUrl("/api/v1/logout")
                            .addLogoutHandler(apiLogoutHandler)
                            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))

                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);

            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        }
    }


    @Order(2)
    @Configuration
    @ServletComponentScan("com.example.meetings.Security")
    public class FormConfigurationAdapter extends
            WebSecurityConfigurerAdapter {

        @Autowired
        private DataSource dataSource;

        @Autowired
        private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

        @Autowired
        private MyLogoutSuccessHandler myLogoutSuccessHandler;

        @Bean
        public String getKey() {
            return "secretkey";
        }

        @Bean
        public PersistentTokenRepository persistentTokenRepository() {
            JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
            tokenRepository.setDataSource(dataSource);
            return tokenRepository;
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.debug(true).ignoring().antMatchers("/img/**", "/static/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.csrf().disable();

            http

                    .authorizeRequests()
                    .antMatchers(
                            "/**"
                    ).permitAll()

                    .and()
                    .formLogin()
                    .loginPage("/signin")
                    .successHandler(myAuthenticationSuccessHandler)

                    .and()
                    .oauth2Login()
                    .userInfoEndpoint()
                    .oidcUserService(this.oidcUserService()).userService(this.oAuth2UserService())
                    .userAuthoritiesMapper(this.userAuthoritiesMapper()).and()
                    .successHandler(myAuthenticationSuccessHandler)
                    .loginPage("/signin")


                    .and()
                    .rememberMe()
                    .key("secretkey").alwaysRemember(true).userDetailsService(userDetailsService)
                    .tokenRepository(persistentTokenRepository())
                    .authenticationSuccessHandler(myAuthenticationSuccessHandler)

                    .and()
                    .logout()
                    .logoutUrl("/logout")
                    .addLogoutHandler(new SecurityContextLogoutHandler())
                    .logoutSuccessHandler(myLogoutSuccessHandler)


                    .and()
                    // for h2-console correct view
                    .headers()
                    .frameOptions()
                    .sameOrigin().and()
                    .requiresChannel()
                    /*.anyRequest()
                    .requiresSecure()*/

                    .and()
                    .sessionManagement()
                    .maximumSessions(100).sessionRegistry(sessionRegistry())
                    .and().sessionCreationPolicy(SessionCreationPolicy.NEVER).sessionFixation().none();
        }


        private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {


            final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

            return userRequest -> {

                OAuth2User defaultOAuth2User = delegate.loadUser(userRequest);

                return new DefaultOAuth2User(defaultOAuth2User.getAuthorities(), defaultOAuth2User.getAttributes(), "login");


            };
        }


        private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
            final OidcUserService delegate = new OidcUserService();

            return (userRequest) -> {

                OidcUser oidcUser = delegate.loadUser(userRequest);

                Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());

                Map<String, Object> map = new HashMap<>(oidcUser.getIdToken().getClaims());
                map.put("myLogin", oidcUser.getIdToken().getEmail().replace("@gmail.com", ""));

                OidcIdToken oidcIdToken = new OidcIdToken(oidcUser.getIdToken().getTokenValue(), oidcUser.getIssuedAt(),
                        oidcUser.getExpiresAt(), map);

                oidcUser = new DefaultOidcUser(mappedAuthorities, oidcIdToken, oidcUser.getUserInfo(), "myLogin");


                return oidcUser;

            };
        }


        private GrantedAuthoritiesMapper userAuthoritiesMapper() {
            return (authorities) -> {
                Set<GrantedAuthority> mappedAuthorities = new HashSet<>();


                authorities.forEach(authority -> {
                    if (authority instanceof OidcUserAuthority) {
                        OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;

                        OidcIdToken idToken = oidcUserAuthority.getIdToken();
                        OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();

                        // Map the claims found in idToken and/or userInfo
                        // to one or more GrantedAuthority's and add it to mappedAuthorities

                    } else if (authority instanceof OAuth2UserAuthority) {
                        OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;

                        Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();

                        // Map the attributes found in userAttributes
                        // to one or more GrantedAuthority's and add it to mappedAuthorities

                    }
                });

                return mappedAuthorities;
            };
        }


    }
}
