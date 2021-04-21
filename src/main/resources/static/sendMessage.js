
function sendMsg(username) {

    let content = document.getElementById("inputtext");

    let json = {
        recipient: {username: username},
        content: content.value
    }

    let div = document.createElement("div");
    div.setAttribute('class', "outgoing_msg")

    div.innerHTML =
        "                        <div class=\"sent_msg\">\n" +
        "                            <p>" + content.value + "</p>\n" +
        "                            <span class=\"time_date\"> 11:02 AM    |    Today</span> </div>\n"


    let last = document.getElementById("msg");

    last.append(div)

    let xhr = new XMLHttpRequest();
    console.log(JSON.stringify(json));
    xhr.open('POST', '/chat/' + username);
    xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
    xhr.send(JSON.stringify(json));
}

