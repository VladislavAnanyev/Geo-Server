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
    //console.log('/topic/' + document.getElementById("dialogId").value);

    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/authuser', false);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            username = xhr.responseText
        }
    }
    xhr.send();

    stompClient.subscribe('/topic/' + username, onMessageReceived);

}


function onError(error) {
    console.log("fail")
    connect()
    console.log("try")
}





function onMessageReceived(payload) {

    var message = JSON.parse(payload.body);

    console.log(message)

    /*let dialog = document.getElementById("dialogs")

    let dialogsName = document.getElementsByClassName("dialogsuser")
    let dialogsNameArr = []

    for (let i = 0; i < dialogsName.length; i++) {
        dialogsNameArr.push(dialogsName[i].textContent)
        //  console.log(dialogsName[i].textContent)
    }*/


    /*let div2 = document.createElement("div")
    div2.setAttribute('class', "chat_list")
    div2.setAttribute('id', message.sender.username)
    div2.setAttribute('onclick', "activeChat(" + message.sender.username + ")")*/

    /*if (username !== message.sender.username) {

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

    }*/

    let div = document.createElement("div");

    if (username !== message.sender.username) {
        /*div.setAttribute('class', "incoming_msg")
        let date = new Date(message.timestamp)
        div.innerHTML =
            "<div class=\"toast\" role=\"alert\" aria-live=\"assertive\" aria-atomic=\"true\">\n" +
            "  <div class=\"toast-header\">\n" +
            "    <img src=\"...\" class=\"rounded me-2\" alt=\"...\">\n" +
            "    <strong class=\"me-auto\">Bootstrap</strong>\n" +
            "    <small>11 мин. назад</small>\n" +
            "    <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"toast\" aria-label=\"Закрыть\"></button>\n" +
            "  </div>\n" +
            "  <div class=\"toast-body\">\n" +
            "    Привет мир! Это тост-сообщение.\n" +
            "  </div>\n" +
            "</div>"*/


        //var toastTrigger = document.getElementById('liveToastBtn')
        var toastLiveExample = document.getElementById('liveToast')

        let body = document.getElementById("toast-text")
        let head = document.getElementById("toast-head")

        body.textContent = message.content
        head.textContent = message.sender.username
        //if (toastTrigger) {
            //toastTrigger.addEventListener('click', function () {
                var toast = new bootstrap.Toast(toastLiveExample, null)

                toast.show()


        /*var toastElList = [].slice.call(document.querySelectorAll('.toast'))
        var toastList = toastElList.map(function (toastEl) {
            return new bootstrap.Toast(toastEl, null)
        })

        toastList[0].show()*/
            //})
        //}

    }


}



