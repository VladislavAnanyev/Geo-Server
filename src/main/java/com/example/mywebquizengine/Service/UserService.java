package com.example.mywebquizengine.Service;

import com.example.mywebquizengine.Model.Role;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Repos.UserRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import static com.example.mywebquizengine.Controller.QuizController.getAuthUser;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailSender mailSender;

    @Value("${hostname}")
    private String hostname;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findById(username);

        if (user.isPresent()) {
            return user.get();
        }else {
            throw new UsernameNotFoundException(String.format("Username[%s] not found",username));
        }
    }

    public void setAvatar(String avatar, User user) {
        userRepository.setAvatar(avatar, user.getUsername());
    }

    public void saveUser(User user){
        if (userRepository.findById(user.getUsername()).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            user.setActivationCode(UUID.randomUUID().toString());
            user.setChangePasswordCode(UUID.randomUUID().toString());
            userRepository.save(user);
            loadUserByUsername(user.getUsername());

            String mes = user.getFirstName() + " " + user.getLastName() + ", Добро пожаловать в WebQuizzes! "
                    + "Для активации аккаунта перейдите по ссылке: https://" + hostname + "/activate/" + user.getActivationCode();

            mailSender.send(user.getEmail(),"Активация аккаунта в WebQuizzes", mes);

        }
    }




    public void updateUser(String lastName, String firstName, String username) {
        userRepository.updateUserInfo(firstName, lastName, username);
    }

    public void sendCodeForChangePassword(String host, User user) {
        //User userLogin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //User user = reloadUser(userLogin.getUsername()).get();
        String mes = user.getChangePasswordCode();
        mailSender.send(user.getEmail(),"Смена пароля в WebQuizzes", "Для смены пароля в WebQuizzes" +
                " перейдите по ссылке: https://" + hostname + "/updatepass/" + mes);
    }

    //@Transactional
    public Optional<User> reloadUser(String username) {
        return userRepository.findById(username);
    }

    public void updatePassword(User user) {
        userRepository.changePassword(user.getPassword(), user.getUsername(), user.getChangePasswordCode());
    }

    public void activateAccount(String activationCode) {
        User user = userRepository.findByActivationCode(activationCode);
        if (user != null) {
            user.setStatus(true);
            userRepository.activateAccount(user.getUsername());
        }

    }

    public Optional<User> getUserViaChangePasswordCode(String changePasswordCode) {


        return userRepository.findByChangePasswordCode(changePasswordCode);
    }

    public void tryToSaveUser(User user) {

        if (!userRepository.findById(user.getUsername()).isPresent()) {
            userRepository.save(user);
        }

    }

    public User castToUser(OAuth2AuthenticationToken authentication) {

        User user = new User();

        if (authentication.getAuthorizedClientRegistrationId().equals("google")) {



            String username = ((String) authentication.getPrincipal().getAttributes()
                    .get("email")).replace("@gmail.com", "");

            if (userRepository.findById(username).isPresent()) {
                user = userRepository.findById(username).get();
            } else {

            user.setEmail((String) authentication.getPrincipal().getAttributes().get("email"));
            user.setFirstName((String) authentication.getPrincipal().getAttributes().get("given_name"));
            user.setLastName((String) authentication.getPrincipal().getAttributes().get("family_name"));

            user.setStatus(true);
            user.setUsername(((String) authentication.getPrincipal().getAttributes()
                    .get("email")).replace("@gmail.com", ""));

        }


    } else if (authentication.getAuthorizedClientRegistrationId().equals("github")) {
            user.setUsername(authentication.getPrincipal().getAttributes().get("login").toString());
            user.setStatus(false);
            user.setFirstName(authentication.getPrincipal().getAttributes().get("name").toString());
            user.setLastName(authentication.getPrincipal().getAttributes().get("name").toString());

            if (authentication.getPrincipal().getAttributes().get("email") != null) {
                user.setEmail(authentication.getPrincipal().getAttributes().get("email").toString());
            } else {
                user.setEmail("default@default.com");
            }

        }

        user.setAvatar("default");
        user.setEnabled(true);
        user.grantAuthority(Role.ROLE_USER);
        user.setChangePasswordCode(UUID.randomUUID().toString());

        return user;
    }



}
