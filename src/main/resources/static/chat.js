'use strict';

/*var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');*/
//var messageInput = document.querySelector('#message');

/*var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');*/

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(/*event*/) {
    //username = document.querySelector('#name').value.trim();

    //if(username) {
        //usernamePage.classList.add('hidden');
        //chatPage.classList.remove('hidden');

    console.log("Go")
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    //}
    //event.preventDefault();
}


function onConnected() {

    // Subscribe to the Public Topic
    console.log('/topic/' + document.getElementById("dialogId").value);
    stompClient.subscribe('/topic/' + document.getElementById("autoUs").textContent, onMessageReceived);

    // Tell your username to the server
   /*stompClient.send("/app/testchat",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');*/
}


function onError(error) {
    /*connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';*/
    console.log("fail")

    connect()
    console.log("try")
}


function sendMessage(sender, dialog) {

    let messageInput = document.getElementById("inputtext");
    if (messageInput.value.length !== 0) {

        var messageContent = messageInput.value.trim();
        if(messageContent && stompClient) {
            var chatMessage = {
                sender: {username: sender},
                content: messageInput.value,
                dialog: {dialogId: dialog}
            };
            stompClient.send("/app/user/" + dialog, {}, JSON.stringify(chatMessage));

        }

        let date = new Date()

        let div = document.createElement("div");
        div.setAttribute('class', "outgoing_msg")

        div.innerHTML =
            "                        <div class=\"sent_msg\">\n" +
            "                            <p>" + messageInput.value + "</p>\n" +
            "                            <span class=\"time_date\">" +  date.toLocaleDateString() + " " + date.toLocaleTimeString() + "</span> </div>\n"


        let last = document.getElementById("msg");


        last.append(div)
        //window.scrollTo(0,document.querySelector("#msgcont").scroll);

        var div2 = $("#msg");
        div2.scrollTop(div2.prop('scrollHeight'));

        let lastMsg = document.getElementById("lastMsg" + dialog)
        console.log(lastMsg)
        let msgInput = messageInput.value
        messageInput.value = ' '
        lastMsg.textContent = msgInput



    }
}


function onMessageReceived(payload) {
    //f(21623)
   /* var myModal = new bootstrap.Modal(document.getElementById('staticBackdrop'), {
        keyboard: false
    })
    myModal.toggle()*/
    //console.log("receive")
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

    div2.innerHTML =
        "                                    <div class=\"chat_people\">\n" +
        "                                        <div class=\"chat_img\"> <img src=\"../../../../img/" + message.sender.avatar + ".jpg" + "\" alt=\"sunil\"> </div>\n" +
        "                                        <div class=\"chat_ib\">\n" +
        "                                            <h5 class=\"dialogsuser\">"+message.sender.username+"<span class=\"chat_date\"></span></h5>\n" +
        "                                            <p>" + message.content + "</p>\n" +
        "                                        </div>\n" +
        "                                    </div>"


    //console.log(message.sender.username)
    //console.log(dialogsNameArr.indexOf(message.sender.username))

    if (dialogsNameArr.indexOf(message.sender.username) === -1) {
        dialog.before(div2)
    }

    if (dialogsNameArr.indexOf(message.sender.username) === 0) {
        document.getElementById("lastMsg" + message.dialog.dialogId).textContent = message.content
    }

    //var messageElement = document.createElement('li');

    /*if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }*/

    /*var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;*/
    let div = document.createElement("div");
    div.setAttribute('class', "incoming_msg")

    let date = new Date(message.timestamp)
    div.innerHTML =
        "<div class=\"incoming_msg_img\"> <img src=\"../../../../img/" + message.sender.avatar + ".jpg" + "\" alt=\"sunil\"> </div>" +
        "                        <div class=\"received_msg\">\n" +
        "                        <div class=\"received_withd_msg\">\n" +
        "                            <p>" + message.content + "</p>\n" +
        "                            <span class=\"time_date\">" +  date.toLocaleDateString() + " " + date.toLocaleTimeString() + "</span> </div> </div>\n"


    let last = document.getElementById("msg");

    last.append(div)

    var div3 = $("#msg");
    div3.scrollTop(div3.prop('scrollHeight'));
}



//usernameForm.addEventListener('submit', connect, true)

//messageForm.addEventListener('submit', sendMessage, true)

