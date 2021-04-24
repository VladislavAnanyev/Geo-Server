package com.example.mywebquizengine.Service;

import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Repos.UserRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailSender mailSender;

    private User thisUser;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findById(username);



        if (user.isPresent()) {
            setThisUser(user.get());
            return user.get();
        }else {
            throw new UsernameNotFoundException(String.format("Username[%s] not found",username));
        }
    }

    public void setAvatar(String avatar) {
        User userLogin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userRepository.setAvatar(avatar, userLogin.getUsername());
    }

    public void saveUser(User user){
        if (userRepository.findById(user.getUsername()).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            user.setActivationCode(UUID.randomUUID().toString());
            userRepository.save(user);
            loadUserByUsername(user.getUsername());
            setThisUser(user); //? проверить нужно это вообще или нет

            String mes = user.getFirstName() + " " + user.getLastName() + ", Добро пожаловать в Quizzes! "
                    + "Для активации аккаунта перейдите по ссылке: http://localhost:8080/activate/" + user.getActivationCode();

            //mailSender.send(thisUser.getEmail(),"Активация аккаунта в Quizzes", mes);

        }
    }

    public void setThisUser(User thisUser) {
        this.thisUser = thisUser;
    }

    public User getThisUser() {
        return thisUser;
    }

    public void updateUser(String lastName, String firstName, String username) {
        userRepository.updateUserInfo(firstName, lastName, username);
    }

    //@Transactional
    public Optional<User> reloadUser(String username) {
        return userRepository.findById(username);
    }

    public void updatePassword(User user) {
        userRepository.changePassword(user.getPassword(), user.getUsername());
    }

    public void activateAccount(String activationCode) {
        User user = userRepository.findByActivationCode(activationCode);
        if (user != null) {
            user.setStatus(true);
            userRepository.activateAccount(user.getUsername());
        }

    }
}
