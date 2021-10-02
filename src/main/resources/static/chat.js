'use strict';

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect() {

    console.log("Go")
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    console.log('/topic/' + document.getElementById("dialogId").value);

    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/authuser', false);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            username = xhr.responseText
        }
    }
    xhr.send();
    console.log(username)

    stompClient.subscribe('/topic/' + username, onMessageReceived);

}


function onError(error) {
    console.log("fail")
    connect()
    console.log("try")
}


function sendMessage(dialog) {


    let messageInput = document.getElementById("inputtextarea");
    console.log(messageInput.value)
    if (messageInput.value.length !== 0) {

        var messageContent = messageInput.value.trim();
        if(messageContent && stompClient) {
            var chatMessage = {
                sender: {username: username},
                content: messageContent,
                dialog: {dialogId: dialog}
            };
            stompClient.send("/app/user/" + dialog, {}, JSON.stringify(chatMessage));

        }

        let date = new Date()
        let div = document.createElement("div");
        div.setAttribute('class', "outgoing_msg")

        div.innerHTML =
            "                        <div class=\"sent_msg\">\n" +
            "                            <p>" + messageContent + "</p>\n" +
            "                            <span class=\"time_date\">" +  date.toLocaleDateString() + " " + date.toLocaleTimeString() + "</span> </div>\n"


        let last = document.getElementById("msg");

        last.append(div)

        var div2 = $("#msg");
        div2.scrollTop(div2.prop('scrollHeight'));

        let lastMsg = document.getElementById("lastMsg" + dialog)
        //console.log(lastMsg)
        //let msgInput = messageInput.value
        messageInput.value = ''
        lastMsg.textContent = messageContent

    }
}


function onMessageReceived(payload) {

    var message = JSON.parse(payload.body);

    console.log(message)

    let dialog = document.getElementById("dialogs")

    let dialogsName = document.getElementsByClassName("dialogsuser")
    let dialogsNameArr = []

    for (let i = 0; i < dialogsName.length; i++) {
        dialogsNameArr.push(dialogsName[i].textContent)
        //  console.log(dialogsName[i].textContent)
    }


    let div2 = document.createElement("div")
    div2.setAttribute('class', "chat_list")
    div2.setAttribute('id', message.sender.username)
    div2.setAttribute('onclick', "activeChat(" + message.sender.username + ")")

    if (username !== message.sender.username) {

        div2.innerHTML =
            "                                    <div class=\"chat_people\">\n" +
            "                                        <div class=\"chat_img\"> <img src=\"../../../../img/" + message.sender.avatar + ".jpg" + "\" alt=\"sunil\"> </div>\n" +
            "                                        <div class=\"chat_ib\">\n" +
            "                                            <h5 class=\"dialogsuser\">" + message.sender.username + "<span class=\"chat_date\"></span></h5>\n" +
            "                                            <p>" + message.content + "</p>\n" +
            "                                        </div>\n" +
            "                                    </div>"


        if (dialogsNameArr.indexOf(message.sender.username) === -1) {
            dialog.before(div2)
        }

        if (dialogsNameArr.indexOf(message.sender.username) === 0) {
            document.getElementById("lastMsg" + message.dialog.dialogId).textContent = message.content
        }

    }



    let div = document.createElement("div");

    if (username !== message.sender.username) {
        div.setAttribute('class', "incoming_msg")
        let date = new Date(message.timestamp)
        div.innerHTML =
            "<div class=\"incoming_msg_img\"> <img src=\"../../../../img/" + message.sender.avatar + ".jpg" + "\" alt=\"sunil\"> </div>" +
            "                        <div class=\"received_msg\">\n" +
            "                        <div class=\"received_withd_msg\">\n" +
            "                            <p>" + message.content + "</p>\n" +
            "                            <span class=\"time_date\">" +  date.toLocaleDateString() + " " + date.toLocaleTimeString() + "</span> </div> </div>\n"

    } else {
        div.setAttribute('class', "outgoing_msg")
        let date = new Date(message.timestamp)
        div.innerHTML = "<div class=\"sent_msg\">\n" +
            "                                    <p>" + message.content + "</p>\n" +
            "                                    <span class=\"time_date\">" +  date.toLocaleDateString() + " " + date.toLocaleTimeString() + "</span> </div>\n" +
            "                            "


        //if (dialogsNameArr.indexOf(message.sender.username) === 0) {
            document.getElementById("lastMsg" + message.dialog.dialogId).textContent = message.content
        //}
    }


    let last = document.getElementById("msg");

    last.append(div)

    var div3 = $("#msg");
    div3.scrollTop(div3.prop('scrollHeight'));


}



