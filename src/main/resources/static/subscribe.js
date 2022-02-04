'use strict';

var stompClient = null;
var username = null;
let exhcangeName = null
let uniqueQueueName = new Date().valueOf() + ""
let activeTyping = true
let jwt = null
var colors2 = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];



function notificationConnect(exchange) {

    exhcangeName = exchange

    console.log("Go")
    /*var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);*/

    var url = "wss://" + location.host + ":15673/ws";
    stompClient = Stomp.client(url);

    stompClient.heartbeat.outgoing = 10000; // client will send heartbeats every 20000ms
    stompClient.heartbeat.incoming = 10000;
    //stompClient.reconnect_delay = 5000;
    stompClient.connect('test', 'test', onConnectedNotif, onErrorNotif)

}

function onConnectedNotif() {

    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/authuser', false);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            username = xhr.responseText
        }
    }
    xhr.send();


    let xhrJwt = new XMLHttpRequest();
    xhrJwt.open('GET', '/jwt', false);
    xhrJwt.onreadystatechange = function () {
        if (xhrJwt.readyState === XMLHttpRequest.DONE && xhrJwt.status === 200) {
            jwt = xhrJwt.responseText
        }
    }
    xhrJwt.send();

/*    let xhrExchange = new XMLHttpRequest();
    xhrExchange.open('GET', '/exchange', false);
    xhrExchange.onreadystatechange = function () {
        if (xhrExchange.readyState === XMLHttpRequest.DONE && xhrExchange.status === 200) {
            xhrExchange.responseText - имя обмена
        }
    }
    xhrExchange.send();*/

    if (location.pathname.includes("chat")) {
        stompClient.subscribe('/exchange/' + exhcangeName, onMessageReceived,
            {
                "id": "sub", "auto-delete": false, "x-queue-name": uniqueQueueName,
                "x-expires": 300000, "ack": "client"
            });
    } else {
        stompClient.subscribe('/exchange/' + exhcangeName, onMessageReceived, {"ack": "client"})
    }
    //stompClient.subscribe('/queue/' + username, onMessageReceived);
    geo()

    /* stompClient.disconnect(function(frame) {
         //debug("STOMP Client disconnecting ...");
         console.log("STOMP client succesfully disconnected.");
     })*/
}


function onErrorNotif(error) {
    console.log("fail")
    console.log('STOMP: ' + error);
    stompClient.disconnect()
    //stompClient.unsubscribe("sub")
    setTimeout( () => {
            notificationConnect()
        }
    , 5000);
    console.log('STOMP: Reconecting in 5 seconds');
    //notificationConnect()
    console.log("try")
}





function onMessageReceived(payload) {

    payload.ack()
    let message = JSON.parse(payload.body);
    let toastLiveExample

    let toast
    // username = frame.headers['type'];


    if (message.type === "MESSAGE" && !location.pathname.includes(message.payload.dialogId)) {

        if (username !== message.payload.sender.username) {

            toastLiveExample = document.getElementById('liveToastMessage')

            let body = document.getElementById("toast-text-msg")
            let head = document.getElementById("toast-head-msg")

            body.textContent = message.payload.content
            head.textContent = message.payload.sender.username

            toast = new bootstrap.Toast(toastLiveExample, null)
            toast.show()
        }
    }

    if (message.type === "MEETING") {

        toastLiveExample = document.getElementById('liveToastMeet')

        let body = document.getElementById("toast-text-meet")
        let head = document.getElementById("toast-head-meet")

        body.textContent = "Скорее посмотрите их"
        head.textContent = "У вас новые встречи"

        toast = new bootstrap.Toast(toastLiveExample, null)

        toast.show()
    }

    if (message.type === "MESSAGE" && !location.pathname.includes(message.payload.dialogId)
        && location.pathname.includes("/chat")) {

            console.log(message)

            let dialog = document.getElementById("dialogs")

            //console.log(freme.headers['type'])

            let dialogsName = document.getElementsByClassName("dialogsuser")
            let dialogsNameArr = []

            for (let i = 0; i < dialogsName.length; i++) {
                dialogsNameArr.push(dialogsName[i].textContent)
                //  console.log(dialogsName[i].textContent)
            }


            let div2 = document.createElement("div")
            div2.setAttribute('class', "chat_list")
            div2.setAttribute('id', message.payload.dialogId)
            //div2.setAttribute('onclick', "activeChat(" + message.payload.dialogId + ")")


            if (username !== message.payload.sender.username) {

                div2.innerHTML = "<a id="+ message.payload.dialogId + "href\" href=\"/chat/" + message.payload.dialogId + "\">" +
                    "                                    <div class=\"chat_people\">\n" +
                    "                                        <div class=\"chat_img\"> <img class='rounded-circle' src=" + message.payload.sender.avatar + " alt=\"sunil\"> </div>\n" +
                    "                                        <div class=\"chat_ib\">\n" +
                    "                                            <h5 class=\"dialogsuser\">" + message.payload.sender.username + "<span class=\"chat_date\"></span></h5>\n" +
                    "                                            <p id=\"lastMsg" + message.payload.dialogId + "\">" + message.payload.content + "</p>\n" +
                    "                                        </div>\n" +
                    "                                    </div></a>"



                if (dialogsNameArr.indexOf(message.payload.sender.username) === -1) {
                    dialog.prepend(div2)
                }


                //if (dialogsNameArr.indexOf(message.payload.sender.username) === 0) {
                let existDialog = document.getElementById(message.payload.dialogId)
                existDialog.remove()
                dialog.prepend(existDialog)
                document.getElementById("lastMsg" + message.payload.dialogId).textContent = message.payload.content
                //}



            
        }







    }


    if (message.type === "MESSAGE" && location.pathname.includes(message.payload.dialogId)) {
        console.log(message)

        let uniqueCodeFromMsg = message.payload.uniqueCode

        let msgWithUniqueCode = document.getElementById(uniqueCodeFromMsg)

        if (msgWithUniqueCode !== null) {
          msgWithUniqueCode.id = message.payload.id
        }

        if (username !== message.payload.sender.username ||
            (username === message.payload.sender.username && msgWithUniqueCode === null)) {
            let dialog = document.getElementById("dialogs")

            //console.log(freme.headers['type'])

            let dialogsName = document.getElementsByClassName("dialogsuser")
            let dialogsNameArr = []

            for (let i = 0; i < dialogsName.length; i++) {
                dialogsNameArr.push(dialogsName[i].textContent)
                //  console.log(dialogsName[i].textContent)
            }


            let div2 = document.createElement("div")
            div2.setAttribute('class', "chat_list")
            div2.setAttribute('id', message.payload.dialogId)
            //div2.setAttribute('onclick', "activeChat(" + message.payload.sender.username + ")")
            //div2.setAttribute('data-barba-prevent', "")

            if (username !== message.payload.sender.username) {

                div2.innerHTML="<a data-barba-prevent id="+ message.payload.dialogId + "href\" href=\"/chat/" + message.payload.dialogId + "\">" +
                    "                                    <div class=\"chat_people\">\n" +
                    "                                        <div class=\"chat_img\"> <img class='rounded-circle' src=" + message.payload.sender.avatar + " alt=\"sunil\"> </div>\n" +
                    "                                        <div class=\"chat_ib\">\n" +
                    "                                            <h5 class=\"dialogsuser\">" + message.payload.sender.username + "<span class=\"chat_date\"></span></h5>\n" +
                    "                                            <p id=\"lastMsg" + message.payload.dialogId + "\">" + message.payload.content + "</p>\n" +
                    "                                        </div>\n" +
                    "                                    </div></a>"


                if (dialogsNameArr.indexOf(message.payload.sender.username) === -1) {
                    dialog.prepend(div2)
                }


                //if (dialogsNameArr.indexOf(message.payload.sender.username) === 0) {
                //document.getElementById("lastMsg" + message.payload.dialogId).textContent = message.payload.content
                //}
                let existDialog = document.getElementById(message.payload.dialogId)
                existDialog.remove()
                dialog.prepend(existDialog)
                document.getElementById("lastMsg" + message.payload.dialogId).textContent = message.payload.content


            }


            let div = document.createElement("div");

            if (username !== message.payload.sender.username) {
                div.setAttribute('class', "incoming_msg")
                let date = new Date(message.payload.timestamp)
                div.innerHTML =
                    "<div class=\"incoming_msg_img\"> <img class='rounded-circle' src=" + message.payload.sender.avatar + " alt=\"sunil\"> </div>" +
                    "                        <div class=\"received_msg\">\n" +
                    "                        <div class=\"received_withd_msg\">\n" +
                    "                            <p>" + message.payload.content + "</p>\n" +
                    "                            <span id=\"" + message.payload.id + "\" class=\"time_date\">" + date.toLocaleDateString() + " " + date.toLocaleTimeString() + "</span> </div> </div>\n"

            } else {
                div.setAttribute('class', "outgoing_msg")
                let date = new Date(message.payload.timestamp)
                div.innerHTML = "<div class=\"sent_msg\">\n" +
                    "                                    <p>" + message.payload.content + "</p>\n" +
                    "                                    <span id=\"" + message.payload.id + "\" class=\"time_date\">" + date.toLocaleDateString() + " " + date.toLocaleTimeString() + "</span> </div>\n" +
                    "                            "


                //if (dialogsNameArr.indexOf(message.payload.sender.username) === 0) {
                document.getElementById("lastMsg" + message.payload.dialogId).textContent = message.payload.content
                //}
            }


            let last = document.getElementById("msg");


            last.append(div)


            var div3 = $("#msg");
            div3.scrollTop(div3.prop('scrollHeight'));
        }
    }
}



function logKey(dialog) {

    if (activeTyping) {

        if (stompClient) {
            var typing = {
                dialogId: dialog
            };

            let rabbitMessage = {
                type: "TYPING",
                payload: typing
            }
            stompClient.send("/queue/incoming-messages", {"Authorization": jwt + ""}, JSON.stringify(rabbitMessage));
            activeTyping = false

            setTimeout(() => {
                activeTyping = true
            }, 5000)

        }
    }

    /*let div = document.createElement("div");
    div.setAttribute('class', "outgoing_msg")

    div.innerHTML =
        "                        <div class=\"sent_msg\">\n" +
        "                            <p>" + messageContent + "</p>\n" +
        "                            <span id=\"" + uniqueCode + "\" class=\"time_date\">" + date.toLocaleDateString() + " " + date.toLocaleTimeString() + "</span> </div>\n"


    let last = document.getElementById("msg");

    last.append(div)

    var div2 = $("#msg");
    div2.scrollTop(div2.prop('scrollHeight'));

    let lastMsg = document.getElementById("lastMsg" + dialog)
    //console.log(lastMsg)
    //let msgInput = messageInput.value

    let dialogList = document.getElementById("dialogs")
    let existDialog = document.getElementById(dialog)
    existDialog.remove()
    dialogList.prepend(existDialog)*/
    //document.getElementById("lastMsg" + dialog).textContent = message.payload.content




}


function sendMessage(dialog) {


    let messageInput = document.getElementById("inputtextarea");
    console.log(messageInput.value)
    if (messageInput.value.length !== 0) {

        let date = new Date()
        let uniqueCode = date.valueOf()
        var messageContent = messageInput.value.trim();
        //console.log(date.valueOf())
        if(messageContent && stompClient) {
            var chatMessage = {
                content: messageContent,
                dialogId: dialog,
                /*type: "MESSAGE",*/
                uniqueCode: uniqueCode
            };

            let rabbitMessage = {
                type: "MESSAGE",
                payload: chatMessage
            }
            stompClient.send("/queue/incoming-messages", {"Authorization": jwt + ""}, JSON.stringify(rabbitMessage));

        }



        let div = document.createElement("div");
        div.setAttribute('class', "outgoing_msg")

        div.innerHTML =
            "                        <div class=\"sent_msg\">\n" +
            "                            <p>" + messageContent + "</p>\n" +
            "                            <span id=\"" + uniqueCode + "\" class=\"time_date\">" +  date.toLocaleDateString() + " " + date.toLocaleTimeString() + "</span> </div>\n"


        let last = document.getElementById("msg");

        last.append(div)

        var div2 = $("#msg");
        div2.scrollTop(div2.prop('scrollHeight'));

        let lastMsg = document.getElementById("lastMsg" + dialog)
        //console.log(lastMsg)
        //let msgInput = messageInput.value

        let dialogList = document.getElementById("dialogs")
        let existDialog = document.getElementById(dialog)
        existDialog.remove()
        dialogList.prepend(existDialog)
        //document.getElementById("lastMsg" + dialog).textContent = message.payload.content


        messageInput.value = ''
        lastMsg.textContent = messageContent

    }

}







