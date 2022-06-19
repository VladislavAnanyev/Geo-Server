package com.example.mywebquizengine.controller.web;

import com.example.mywebquizengine.model.order.Order;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.service.PaymentServices;
import com.example.mywebquizengine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;

@Controller
public class PayPalController {

    @Autowired
    private PaymentServices paymentServices;

    @Autowired
    private UserService userService;

    @PostMapping(path = "/checkyandex")
    @ResponseBody
    @Transactional
    public void checkyandex(String notification_type, String operation_id, Number amount, Number withdraw_amount,
                            String currency, String datetime, String sender, Boolean codepro, String label,
                            String sha1_hash, Boolean test_notification, Boolean unaccepted, String lastname,
                            String firstname, String fathersname, String email, String phone, String city,
                            String street, String building, String suite, String flat, String zip) throws NoSuchAlgorithmException {

        paymentServices.processPayment(
                notification_type, operation_id, amount, withdraw_amount, currency, datetime,
                sender, codepro, label, sha1_hash, test_notification, unaccepted, lastname,
                firstname, fathersname, email, phone, city, street, building, suite, flat, zip
        );

    }

    @GetMapping(path = "/checkout")
    public String getCheckOut(Model model, @AuthenticationPrincipal User principal) {
        Order order = new Order();
        order.setUser(userService.loadUserByUserId(principal.getUserId()));
        paymentServices.saveStartOrder(order);
        model.addAttribute("order", order);
        return "checkout";
    }

}
