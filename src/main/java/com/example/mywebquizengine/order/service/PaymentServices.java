package com.example.mywebquizengine.order.service;

/*import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;*/
import com.example.mywebquizengine.order.repository.OrderRepository;
import com.example.mywebquizengine.order.model.domain.Order;
import com.example.mywebquizengine.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class PaymentServices {

    @Value("${notification-secret}")
    String notification_secret;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    public Order saveFinalOrder(Order order) {

        Order finalOrder = orderRepository.findById(order.getOrder_id()).get();
        finalOrder.setCoins(order.getCoins());
        finalOrder.setAmount(order.getAmount());
        finalOrder.setOperation_id(order.getOperation_id());

        orderRepository.save(finalOrder);

        return finalOrder;
    }

    public void saveStartOrder(Order order) {
        orderRepository.save(order);
    }

    public void processPayment(String notification_type, String operation_id, Number amount, Number withdraw_amount, String currency, String datetime, String sender, Boolean codepro, String label, String sha1_hash, Boolean test_notification, Boolean unaccepted, String lastname, String firstname, String fathersname, String email, String phone, String city, String street, String building, String suite, String flat, String zip) throws NoSuchAlgorithmException {
        String mySha = notification_type + "&" + operation_id + "&" + amount + "&" + currency + "&" +
                datetime + "&" + sender + "&" + codepro + "&" + notification_secret + "&" + label;

        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(mySha.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        if (sb.toString().equals(sha1_hash)) {
            System.out.println("Пришло уведомление");
            System.out.println("notification_type = " + notification_type + ", operation_id = " + operation_id +
                    ", amount = " + amount + ", withdraw_amount = " + withdraw_amount + ", currency = " + currency +
                    ", datetime = " + datetime + ", sender = " + sender + ", codepro = " + codepro + ", label = " + label +
                    ", sha1_hash = " + sha1_hash + ", test_notification = " + test_notification + ", unaccepted = " + unaccepted + ", lastname = " + lastname + ", firstname = " + firstname + ", fathersname = " + fathersname + ", email = " + email + ", phone = " + phone + ", city = " + city + ", street = " + street + ", building = " + building + ", suite = " + suite + ", flat = " + flat + ", zip = " + zip);
            System.out.println();

            Order order = new Order();

            order.setAmount(String.valueOf(amount));
            order.setCoins((int) (Double.parseDouble(String.valueOf(withdraw_amount)) * 100.0));
            order.setOperation_id(operation_id);
            order.setOrder_id(Integer.valueOf(label));

            order = saveFinalOrder(order);
            userService.updateBalance(order.getCoins(), order.getUser().getUserId());

        } else {
            System.out.println("Неправильный хэш");
        }
    }

    public Order findById(String label) {
        return orderRepository.findById(Integer.valueOf(label)).get();
    }
}
