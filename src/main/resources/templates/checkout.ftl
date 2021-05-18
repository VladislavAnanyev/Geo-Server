<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Check Out</title>
    <style type="text/css">
        table { border: 0; }
        table td { padding: 10px; }
    </style>
</head>
<body>
<div align="center">
    <h1>Check Out</h1>
    <br/>
    <form action="authorize_payment" method="post">
        <table>
            <tr>
                <td>Product/Service:</td>
                <td><input type="text" name="productName" value="Одиндва" /></td>
            </tr>
            <tr>
                <td>Sub Total:</td>
                <td><input type="text" name="subtotal" value="100" /></td>
            </tr>
            <tr>
                <td>Shipping:</td>
                <td><input type="text" name="shipping" value="10" /></td>
            </tr>
            <tr>
                <td>Tax:</td>
                <td><input type="text" name="tax" value="10" /></td>
            </tr>
            <tr>
                <td>Total Amount:</td>
                <td><input type="text" name="total" value="120" /></td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <input type="submit" value="Checkout" />
                </td>
            </tr>
        </table>
    </form>
</div>

<div id="smart-button-container">
    <div style="text-align: center;">
        <div id="paypal-button-container"></div>
    </div>
</div>
<script src="https://www.paypal.com/sdk/js?client-id=AZhcxVtP4Zd8xT9YsigI82RHCoT3TgR-15KbWBKgODth8tEBAKirS40yYRN_RnZD-2I7q9I-UGT-23nS&currency=USD" data-sdk-integration-source="button-factory"></script>
<script>
    function initPayPalButton() {
        paypal.Buttons({
            style: {
                shape: 'rect',
                color: 'gold',
                layout: 'vertical',
                label: 'paypal',

            },

            createOrder: function(data, actions) {
                return actions.order.create({
                    purchase_units: [{"amount":{"currency_code":"USD","value":1}}]
                });
            },

            onApprove: function(data, actions) {
                return actions.order.capture().then(function(details) {
                    alert('Transaction completed by ' + details.payer.name.given_name + '!');
                });
            },

            onError: function(err) {
                console.log(err);
            }
        }).render('#paypal-button-container');
    }
    initPayPalButton();
</script>


<iframe src="https://yoomoney.ru/quickpay/shop-widget?writer=seller&targets=%D0%A2%D0%B5%D1%81%D1%82%D0%BE%D0%B2%D0%B0%D1%8F%20%D0%BE%D0%BF%D0%BB%D0%B0%D1%82%D0%B0&targets-hint=&default-sum=10&button-text=11&payment-type-choice=on&hint=&successURL=http%3A%2F%2F192.168.1.11%3A8080%2F&quickpay=shop&account=410012943784354" width="100%" height="223" frameborder="0" allowtransparency="true" scrolling="no"></iframe>


<form method="POST" action="https://yoomoney.ru/quickpay/confirm.xml">
    <input type="hidden" name="receiver" value="410012943784354">
    <input type="hidden" name="formcomment" value="Проект «Железный человек»: реактор холодного ядерного синтеза">
    <input type="hidden" name="short-dest" value="Проект «Железный человек»: реактор холодного ядерного синтеза">
    <input type="hidden" name="label" value="$order_id">
    <input type="hidden" name="quickpay-form" value="donate">
    <input type="hidden" name="targets" value="транзакция {order_id}">
    <input type="hidden" name="sum" value="4568.25" data-type="number">
    <input type="hidden" name="comment" value="Хотелось бы получить дистанционное управление.">
    <input type="hidden" name="need-fio" value="true">
    <input type="hidden" name="need-email" value="true">
    <input type="hidden" name="need-phone" value="false">
    <input type="hidden" name="need-address" value="false">
    <label><input type="radio" name="paymentType" value="PC">ЮMoney</label>
    <label><input type="radio" name="paymentType" value="AC">Банковской картой</label>
    <input type="submit" value="Перевести"></form>


</body>
</html>