package com.example.mywebquizengine.service.sender;

import com.example.mywebquizengine.model.common.Client;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.service.sender.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BusinessEmailSender {

    @Autowired
    private MailSender mailSender;
    @Value("${hostname}")
    private String hostname;

    public void sendWelcomeMessage(User user) {
        String mes = String.format("""
                %s %s, Добро пожаловать в %s! Для активации аккаунта перейдите по ссылке:\040
                %s . Если вы не регистрировались на данном ресурсе, то проигнорируйте это сообщение"
                """, user.getFirstName(), user.getLastName(), hostname, hostname + "/activate/" + user.getActivationCode());

        mailSender.send(user.getEmail(), "Активация аккаунта в " + hostname, mes);
    }

    public void sendChangePasswordMessage(User user, String code, Client client) {

        String mes;
        if (client.equals(Client.MOBILE)) {
            mes = String.format("Для смены пароля в %s введите в приложении код: %s. " +
                    "Если вы не меняли пароль на данном ресурсе, то проигнорируйте сообщение", hostname, code);
        } else {
            mes = String.format("""
                    Для смены пароля в %s
                    перейдите по ссылке: %s\040
                    Если вы не меняли пароль на данном ресурсе, то проигнорируйте сообщение""",
                    hostname,  hostname + "/updatepass/" + code
            );
        }

        mailSender.send(user.getEmail(), "Смена пароля в " + hostname, mes);
    }

    public void sendCodeForSignInViaPhone(String code, String email) {
        String message = "Ваш код для входа " + code;
        mailSender.send(email, "Код для входа в " + hostname, message);
    }
}
