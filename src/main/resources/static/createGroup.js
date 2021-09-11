function createGroup() {



    let dialog = document.getElementById("dialogs")
    let name = document.getElementById("chatName")
    let answer_values = [];

    //document.forms['GroupName'].repo

    //document.forms['GroupName'].reportValidity()
    //document.forms['GroupName'].checkValidity()

    //name.checkValidacity()

    let size = document.getElementsByClassName("users");
    let usersNames = document.getElementsByClassName("usersValue")

    let users = []
    let user;

    for (let i = 0; i < size.length; i++) {
       // answer = document.getElementsByName("" + name + i);

        //for (j = 0; j < answer.length; j++) {
            if(size[i].checked) {
                //console.log(answer[j].value)
                //console.log(usersNames[i].textContent.trim())
                user = {
                    username: usersNames[i].textContent.trim()
                }
                users.push(user)
                user = ""
            }
            //answer[j].disabled = true;
       // }
       // let test = {answer: answer_values}
       // answers.push(test)
        //answer_values = []

    }



    let json = {
        name: name.value,
        users: users
    }

    console.log(json)

    let xhr = new XMLHttpRequest()
    xhr.open('POST', '/createGroup', true)
    xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            let div2 = document.createElement("div")
            div2.setAttribute('class', "chat_list")
            div2.setAttribute('id', xhr.response)
            div2.setAttribute('onclick', "activeChat(" + xhr.response + ")")

            div2.innerHTML =
                "                                    <div class=\"chat_people\">\n" +
                "                                        <div class=\"chat_img\"> <img class=\"rounded-circle\" src=\"../../../../img/default.jpg" + "\" alt=\"sunil\"> </div>\n" +
                "                                        <div class=\"chat_ib\">\n" +
                "                                            <h5 class=\"dialogsuser\">"+name.value+"<span class=\"chat_date\"></span></h5>\n" +
                "                                            <p></p>\n" +
                "                                        </div>\n" +
                "                                    </div>"


            //console.log(message.sender.username)
            //console.log(dialogsNameArr.indexOf(message.sender.username))

            //if (dialogsNameArr.indexOf(message.sender.username) === -1) {
            //dialog.before(div2)
            //}
            document.location.href = '/chat/' + xhr.response
        }
    };
    xhr.send(JSON.stringify(json));
}