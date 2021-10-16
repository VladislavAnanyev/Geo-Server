/*
'use strict';


var stompClient3 = null;
var username3 = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function meetingConnect(/!*event*!/) {


    //if(username) {
    //usernamePage.classList.add('hidden');
    //chatPage.classList.remove('hidden');

    console.log("Go")
    var socket = new SockJS('/ws');
    stompClient3 = Stomp.over(socket);



    stompClient3.connect({}, onConnectedMeeting, onErrorMeeting);




    //}
    //event.preventDefault();
}


function onConnectedMeeting() {
    console.log(location.pathname.replace("/chat", ""))
    if (location.pathname.includes("chat") && location.pathname.replace("/chat", "") !== "") {

        connect()
    }

    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/authuser', false);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            console.log(xhr.responseText)
            username3 = xhr.responseText
        }
    }
    xhr.send();

    // Subscribe to the Public Topic

    //let id = document.getElementById("useranswerid").value

    //console.log('/topic/' + username3);
    stompClient3.subscribe('/topic/' + username3, onMessageReceivedMeeting);


    // Tell your username to the server
    /!*stompClient.send("/app/testchat",
         {},
         JSON.stringify({sender: username, type: 'JOIN'})
     )

     connectingElement.classList.add('hidden');*!/
}


function onErrorMeeting(error) {
    /!*connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';*!/
    console.log("fail")

    //connect()
    console.log("try")
}




function onMessageReceivedMeeting(payload) {

    var meeting = JSON.parse(payload.body);

    if (meeting.type === "MEETING") {
        console.log(meeting)
        console.log(111)



        var toastLiveExample = document.getElementById('liveToastMeet')

        let body = document.getElementById("toast-text-meet")
        let head = document.getElementById("toast-head-meet")

        body.textContent = "Скорее посмотрите их"
        head.textContent = "У вас новые встречи"

        var toast = new bootstrap.Toast(toastLiveExample, null)

        toast.show()
    }




}
*/
