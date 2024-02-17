package com.example.meetings.auth.security;

import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.List;
import java.util.Optional;

import static com.example.meetings.MeetingsApplication.ctx;

@Component
public class LoggedUser implements HttpSessionBindingListener {

    private Long userId;
    private ActiveUserStore activeUserStore;

    public LoggedUser(Long userId, ActiveUserStore activeUserStore) {
        this.userId = userId;
        this.activeUserStore = activeUserStore;
    }

    public LoggedUser() {
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {

        if (activeUserStore != null) {
            List<Long> users = activeUserStore.getUsers();

            boolean flag = true;

            LoggedUser user = (LoggedUser) event.getValue();

            if (users.contains(user.getUserId())) {
                flag = false;
            }
            users.add(user.getUserId());

            if (flag) {
                if (ctx.getBean(UserRepository.class).findById(user.getUserId()).isPresent()) {
                    User authUser = ctx.getBean(UserRepository.class).findById(user.getUserId()).get();
                    authUser.setOnline(true);
                    ctx.getBean(UserRepository.class).save(authUser);
                }
            }

        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        List<Long> users = activeUserStore.getUsers();
        LoggedUser user = (LoggedUser) event.getValue();

        boolean flag = true;

        if (users != null) {
            users.remove(user.getUserId());
            if (users.contains(user.getUserId())) {
                flag = false;
            }
        }

        if (flag) {
            Optional<User> optionalUser = ctx
                    .getBean(UserRepository.class)
                    .findById(user
                            .getUserId()
                    );

            if (optionalUser.isPresent()) {
                User authUser = optionalUser.get();
                authUser.setOnline(false);
                ctx.getBean(UserRepository.class).save(authUser);
            }
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
