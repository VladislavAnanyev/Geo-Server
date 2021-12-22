package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.Order;
import com.example.mywebquizengine.repos.OrderRepository;
/*import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class PaymentServices {
    private static final String CLIENT_ID = "AZhcxVtP4Zd8xT9YsigI82RHCoT3TgR-15KbWBKgODth8tEBAKirS40yYRN_RnZD-2I7q9I-UGT-23nS";
    private static final String CLIENT_SECRET = "EIIuBSiMIrEcN8B9YWGzm6Okyn34-MDhP1_AM-DNQ-NM2U-g6v8rN4PQLQtMEPT7WhnmACDqrifFrw33";
    private static final String MODE = "live";
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

        //System.out.println(mySha);

        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(mySha.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
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
            userService.updateBalance(order.getCoins(), order.getUser().getUsername());

        } else {
            System.out.println("Неправильный хэш");
        }
    }


    /*public String authorizePayment(OrderDetail orderDetail)
            throws PayPalRESTException {

        Payer payer = getPayerInformation();
        RedirectUrls redirectUrls = getRedirectURLs();
        List<Transaction> listTransaction = getTransactionInformation(orderDetail);

        Payment requestPayment = new Payment();
        requestPayment.setTransactions(listTransaction);
        requestPayment.setRedirectUrls(redirectUrls);
        requestPayment.setPayer(payer);
        requestPayment.setIntent("sale");

        APIContext apiContext = new APIContext(CLIENT_ID, CLIENT_SECRET, MODE);

        Payment approvedPayment = requestPayment.create(apiContext);

        return getApprovalLink(approvedPayment);

    }

    private Payer getPayerInformation() {
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        PayerInfo payerInfo = new PayerInfo();
        payerInfo.setFirstName("Владислав")
                .setLastName("Ананьев")
                .setEmail("a.vlad.v@yandex.ru");

        payer.setPayerInfo(payerInfo);

        return payer;
    }

    private RedirectUrls getRedirectURLs() {
        RedirectUrls redirectUrls = new RedirectUrls();
        String url = "";
        try {
           url = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        redirectUrls.setCancelUrl("http://" + url + ":8080" + "/error");
        redirectUrls.setReturnUrl("http://" + url + ":8080" + "/review_payment");

        return redirectUrls;
    }

    private List<Transaction> getTransactionInformation(OrderDetail orderDetail) {
        Details details = new Details();

        details.setShipping(orderDetail.getShipping());
        details.setSubtotal(orderDetail.getSubtotal());
        details.setTax(orderDetail.getTax());

        Amount amount = new Amount();
        amount.setCurrency("RUB");
        amount.setTotal(orderDetail.getTotal());
        amount.setDetails(details);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(orderDetail.getProductName());


        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();

        Item item = new Item();
        item.setCurrency("RUB");
        item.setName(orderDetail.getProductName());
        item.setPrice(orderDetail.getSubtotal());
        item.setTax(orderDetail.getTax());
        item.setQuantity("1");
        item.setSku("1");


        items.add(item);
        itemList.setItems(items);

        *//*ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setCountryCode("US");
        shippingAddress.setRecipientName("Александр");
        shippingAddress.setLine1("Первая линия");
        shippingAddress.setCity("Москва");
        shippingAddress.setState("Москва");
        shippingAddress.setPostalCode("345654");
        itemList.setShippingAddress(shippingAddress);*//*

        transaction.setItemList(itemList);

        //

        //shippingAddress.setCountryCode("RU");

        //transaction.getItemList().setShippingAddress(shippingAddress);

        List<Transaction> listTransaction = new ArrayList<>();
        listTransaction.add(transaction);

        return listTransaction;
    }

    private String getApprovalLink(Payment approvedPayment) {
        List<Links> links = approvedPayment.getLinks();
        String approvalLink = null;

        for (Links link : links) {
            if (link.getRel().equalsIgnoreCase("approval_url")) {
                approvalLink = link.getHref();
                break;
            }
        }

        return approvalLink;
    }

    public Payment getPaymentDetails(String paymentId) throws PayPalRESTException {
        APIContext apiContext = new APIContext(CLIENT_ID, CLIENT_SECRET, MODE);
        return Payment.get(apiContext, paymentId);
    }

    public Payment executePayment(String paymentId, String payerId)
            throws PayPalRESTException {
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        Payment payment = new Payment().setId(paymentId);

        APIContext apiContext = new APIContext(CLIENT_ID, CLIENT_SECRET, MODE);

        return payment.execute(apiContext, paymentExecution);
    }
*/
    public Order findById(String label) {
        return orderRepository.findById(Integer.valueOf(label)).get();
    }
}
