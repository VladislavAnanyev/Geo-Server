
<#import "parts/common.ftl" as e>

        <#include "parts/security.ftl">


<@e.page>





<#--    <link href="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">-->

<!------ Include the above in your HEAD tag ---------->


    <title>Сообщения</title>

<#--    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css" type="text/css" rel="stylesheet">-->

    <link rel="stylesheet" href="../static/chatcss.css">











<#--<#if dialog??>-->



<#--<input id="dialogId" type="hidden" value="${dialog?c}">-->
<#--</#if>-->
<div class="container">







    <div data-barba="wrapper" data-barba-namespace="home" class="messaging">


        <div class="modal fade" id="staticBackdrop" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="staticBackdropLabel">Создание группы</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">

                        <form id="GroupName">
                            <input type="text" class="form-control mb-2" required placeholder="Напишите здесь название группы" id="chatName">
                        </form>
                        <#list userList as user>
                            <#if user.username != name>
                                <div class="form-check ml-1">
                                    <input onclick="document.getElementById('crGroup').disabled=false" class="form-check-input users" type="checkbox" value="" id="flexCheckDefault${user.username}">
                                    <label onclick="document.getElementById('crGroup').disabled=false" class="form-check-label usersValue" for="flexCheckDefault${user.username}">
                                        ${user.username}
                                    </label>
                                </div>
                            </#if>

                        </#list>

                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Назад</button>
                        <button type="button" onclick="createGroup()" id="crGroup" data-dismiss="modal" disabled class="btn btn-primary">Создать</button>
                    </div>

                </div>
            </div>



        </div>

        <div class="inbox_msg">
            <div class="inbox_people">
                <div class="headind_srch">
                    <div class="recent_heading">
                        <h4 class="mt-1" id="autoUs">${name}</h4>

                    </div>
                    <div class="srch_bar">
                        <button type="button"  class="btn btn-primary btn-sm" data-toggle="modal" data-target="#staticBackdrop"><#if text??>${text} <#else > Создать группу</#if></button>
                    </div>
                </div>
                <div class="inbox_chat" id="dialogs">

                        <#list lastDialogs?if_exists as dialog>


                            <#if dialog.dialog.users?size = 2 <#--|| (dialogObj.users?size = 1 && !dialogObj.name??)-->>


                            <#--                            Переделать-->

                                <#if dialog.sender.username != myUsername.username>


                                    <div id="${dialog.dialog.dialogId?c}" <#--последнее сообщение не от меня--> class="chat_list <#--active_chat-->">
                                        <a id="${dialog.dialog.dialogId?c}href" href="/chat/${dialog.dialog.dialogId?c}">
                                        <div class="chat_people">
                                            <div class="chat_img"> <img class="rounded-circle" width="43.26px" height="43.26px" src="${dialog.sender.avatar}"  alt="sunil"> </div>
                                            <div class="chat_ib последнее сообщение не от меня">
                                                <h5 class="dialogsuser">${dialog.sender.username}<span class="chat_date"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>
                                                <p id="lastMsg${dialog.dialog.dialogId?c}">${dialog.content}</p>
                                            </div>
                                        </div>
                                        </a>
                                    </div>
                                <#else>


    <#--                                Второй пользователь-->
                                    <#if dialog.dialog.users?first.username = myUsername.username >
                                        <#assign o = dialog.dialog.users?last>
                                    <#else>
                                        <#assign o = dialog.dialog.users?first>
                                    </#if>

                                    <div id="${dialog.dialog.dialogId?c}" <#--последнее сообщение от меня-->  class="chat_list <#--active_chat-->">
                                        <a id="${dialog.dialog.dialogId?c}href" href="/chat/${dialog.dialog.dialogId?c}">
                                        <div class="chat_people">
                                            <div class="chat_img"> <img class="rounded-circle" width="43.26px" height="43.26px" src="${o.avatar}" alt="sunil"> </div>
                                            <div class="chat_ib последнее сообщение от меня">
                                                <h5 class="dialogsuser">${o.username}<span class="chat_date"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>
                                                <p id="lastMsg${dialog.dialog.dialogId?c}">${dialog.content}</p>
                                            </div>
                                        </div>
                                        </a>
                                    </div>

                                </#if>

                            <#else>

                                <div id="${dialog.dialog.dialogId?c}"  class="chat_list <#--active_chat-->">
                                    <a id="${dialog.dialog.dialogId?c}href" href="/chat/${dialog.dialog.dialogId?c}">
                                    <div class="chat_people">
                                        <div class="chat_img"> <#if dialog.dialog.image??><img class="rounded-circle" width="43.26px" height="43.26px" src="${dialog.dialog.image}"  alt="sunil"> </#if></div>
                                        <div class="chat_ib группа">
                                            <h5 class="dialogsuser"><#if dialog.dialog.name??>${dialog.dialog.name?string}</#if><span class="chat_date"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>
                                            <p id="lastMsg${dialog.dialog.dialogId?c}">${dialog.content}</p>
                                        </div>
                                    </div>
                                    </a>
                                </div>

                           </#if>



                        </#list>

<#--                </div>-->



                </div>

            </div>

            <main data-barba="container" data-barba-namespace="msgs">
            <#--<#if dialog??>-->

            <div class="mesgs" id="msgcont">


                <div class="msg_history" id="msg">

                    <#--<main data-barba="container">-->
                    <#--<#list messages?if_exists as msg>
                        <#if msg.sender.username == myUsername.username>



                            <div class="outgoing_msg">
                                <div class="sent_msg">
                                    <p>${msg.content}</p>
                                    <span id="${msg.id?c}" class="time_date">&lt;#&ndash;?string["dd.MM.yyyy HH:mm:ss"]}&ndash;&gt;</span> </div>
                            </div>


                        <#else>


                            <div class="incoming_msg">
                                <div class="incoming_msg_img"> <img class="rounded-circle" width="43.26px" height="43.26px" src="${msg.sender.avatar}"  alt="sunil"> </div>
                                <div class="received_msg">
                                    <div class="received_withd_msg">
                                        <p>${msg.content}</p>
                                        <span id="${msg.id?c}" class="time_date"></span>
                                    </div>
                                </div>
                            </div>


                        </#if>



                    </#list>-->

<#--                    <#if dialog??>-->
                    <#--<script>

                        /*function setTime(time, id) {
                            let userTime = new Date(time)
                            document.getElementById(id).textContent = userTime.toLocaleDateString() + " " + userTime.toLocaleTimeString()
                        }*/

                        &lt;#&ndash;<#list messages as mes>

                        <#if mes.timestamp??>
                        setTime(${mes.timestamp?long?c}, ${mes.id?c})


                        <#else >
                            console.log("nulllllllllll")
                        </#if>



                        /*setTime(&lt;#&ndash;${mes.timestamp.toInstant().epochSecond?c}, ${mes.id?c}&ndash;&gt;);*/
                        </#list>&ndash;&gt;

                    </script>-->
<#--                    </#if>-->


<#--                    </main>-->
                </div>


                <#--<#if dialog??>
                <div id="msgArea" class="type_msg">
                    <div class="input_msg_write">
                        <input type="text" class="write_msg" id="inputtextarea" placeholder="Type a message"/>
                        <button onclick="sendMessage(location.pathname.replace('/chat/', ''))" class="msg_send_btn" type="button"><i class="fa fa-paper-plane-o" aria-hidden="true"></i></button>
                    </div>
                </div>
                </#if>-->


                <script>

                    //console.log(document.getElementsByClassName("input_msg_write").length)



                        if (location.pathname.replace("/chat/", "") === "" ||
                            location.pathname.replace("/chat/", "") === "/chat") {
                            let textArea = document.createElement('div')
                            textArea.setAttribute("id", "msgArea")
                            textArea.classList.add("type_msg")
                            textArea.innerHTML = "<div class=\"input_msg_write\">\n" +
                                "                        <input type=\"text\" class=\"write_msg\" id=\"inputtextarea\" placeholder=\"Type a message\"/>\n" +
                                "                        <button onclick=\"sendMessage(location.pathname.replace('/chat/', ''))\" class=\"msg_send_btn\" type=\"button\"><i class=\"fa fa-paper-plane-o\" aria-hidden=\"true\"></i></button>\n" +
                                "                    </div>"

                            let msgsById = document.getElementById("msg");

                            textArea.after(msgsById)


                        }


                    if (location.pathname.replace("/chat/", "") !== "" && location.pathname !== "/chat") {

                        let textArea = document.createElement('div')
                        textArea.setAttribute("id", "msgArea")
                        textArea.classList.add("type_msg")
                        textArea.innerHTML = "<div class=\"input_msg_write\">\n" +
                            "                        <input type=\"text\" class=\"write_msg\" id=\"inputtextarea\" placeholder=\"Напишите сообщение\"/>\n" +
                            "                        <button onclick=\"sendMessage(location.pathname.replace('/chat/', ''))\" class=\"msg_send_btn\" type=\"button\"><i class=\"fa fa-paper-plane-o\" aria-hidden=\"true\"></i></button>\n" +
                            "                    </div>"



                        let msgsById = document.getElementById("msg");

                        console.log(location.pathname.replace("/chat/", ""))

                        msgsById.after(textArea)
                    }

                </script>


            </div>
            </main>

<#--            </#if>-->

        </div>





    </div>


</div>




<#--<script src="../static/notification.js"></script>-->

<#--<script src="../static/custom.js"></script>-->
<script src="../static/activeChat.js"></script>
<script src="../static/createGroup.js"></script>
<script src="../static/newActiveDialog.js"></script>
<#--<script src="/static/getUserList.js"></script>-->
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.4.0/sockjs.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>

<#--        <script src="../static/chat.js"></script>-->




<#--<script src="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>-->
<script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>










        <script>



            count = 0
            page = 0;
            currentCount = 0;
        function populate() {


            //console.log("Скр")
            // нижняя граница документа
            let windowRelativeBottom = document.getElementById("msg").offsetTop

            let elem = document.getElementById("msg")
            let scrollBottom = elem.scrollTop;
            //console.log(scrollBottom)
            //console.log(pageYOffset)
            // если пользователь прокрутил достаточно далеко (< 100px до конца)
            if (scrollBottom < 300 && count===0) {
                count++
                page++

                dia = location.pathname.replace("/chat/", "")
                xhr = new XMLHttpRequest();
                xhr.open('GET', '/chat/nextPages?dialog_id=' + dia +'&page=' + page);
                xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
                        let json = JSON.parse(xhr.response)

                        if (json.messages.length !==0) {


                            //console.log("123")



                            for (let i = json.messages.length - 1; i !== 0; i--) {
                                //console.log(11)
                                //console.log(json.messages[i].sender.username)
                                //console.log('${name}')
                                if (json.messages[i].sender.username === '${name}') {

                                    let divChat = document.createElement("div")
                                    divChat.setAttribute('class', "outgoing_msg")
                                    divChat.innerHTML =
                                        "                        <div class=\"sent_msg\">\n" +
                                        "                            <p>" + json.messages[i].content + "</p>\n" +
                                        "                            <span id=" + json.messages[i].id + " class=\"time_date\">" + new Date(json.messages[i].timestamp).toLocaleDateString() + " " + new Date(json.messages[i].timestamp).toLocaleTimeString() + "</span> </div>\n"


                                    let last = document.getElementById("msg");

                                    last.prepend(divChat)
                                } else {
                                    let divChat = document.createElement("div")
                                    divChat.setAttribute('class', "incoming_msg")
                                    divChat.innerHTML =
                                        "<div class=\"incoming_msg_img\"> <img src=" + json.messages[i].sender.avatar + " alt=\"sunil\"> </div>" +
                                        "                        <div class=\"received_msg\">\n" +
                                        "                        <div class=\"received_withd_msg\">\n" +
                                        "                            <p>" + json.messages[i].content + "</p>\n" +
                                        "                            <span class=\"time_date\">" + new Date(json.messages[i].timestamp).toLocaleDateString() + " " + new Date(json.messages[i].timestamp).toLocaleTimeString() + "</span> </div> </div>\n"


                                    let last = document.getElementById("msg");

                                    last.prepend(divChat)
                                }
                            }
                        }





                        count--

                    }
                };
                xhr.send();



            }

        }

        document.getElementById("msg").addEventListener('scroll', populate);


        </script>







    <#if dialog?? >



        <#if dialogObj.users?first.username = myUsername.username >
            <#assign c = dialogObj.users?last>
        <#else>
            <#assign c = dialogObj.users?first>
        </#if>

        <script>

            dialog = document.getElementById("dialogs")
            div2 = document.createElement("div")
            dialogsName = document.getElementsByClassName("chat_list")

            dialogsNameArr = []


            dia = location.pathname.replace("/chat/", "")
            //onclicks = "location.href='/chat/" + dia + "'"
            div2.setAttribute('id', dia)
            div2.setAttribute('class', "chat_list")
            //div2.setAttribute('onclick', onclicks)



            for (let i = 0; i < dialogsName.length; i++) {
                dialogsNameArr.push(dialogsName[i].id)
                //console.log(dialogsName[i].id)
            }

            <#if dialogObj.users?size = 2 <#--|| (dialogObj.users?size = 1 && !dialogObj.name??)-->>


            //div2.setAttribute('class', "inbox_chat")

            //let dia = location.pathname.replace("/chat/", "")


            if (dialogsNameArr.indexOf(dia) === -1) {

                div2.innerHTML = "<a id=\"${dialogObj.dialogId?c}href\" href=\"/chat/50955\">" +
                    "                                    <div class=\"chat_people\">\n" +
                    "                                        <div class=\"chat_img\"> <img class=\"rounded-circle\" width=\"43.26px\" height=\"43.26px\" src=\"${c.avatar}\" alt=\"sunil\"> </div>\n" +
                    "                                        <div class=\"chat_ib\">\n" +
                    "                                            <h5 class=\"dialogsuser\">${c.username}<span class=\"chat_date\"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>\n" +
                    "                                            <p></p>\n" +
                    "                                        </div>\n" +
                    "                                    </div>\n" +
                    "                                </div></a>"




                dialog.prepend(div2)


            }


            <#else>


            //div2.setAttribute('class', "inbox_chat")


            if (dialogsNameArr.indexOf(dia) === -1) {



                div2.innerHTML = "<a id=\"${dialogObj.dialogId?c}href\" href=\"/chat/${dialogObj.dialogId?c}\">" +
                    "                                    <div class=\"chat_people\">\n" +
                    "                                        <div class=\"chat_img\"> <img class=\"rounded-circle\" width=\"43.26px\" height=\"43.26px\" src=\"<#--https://ptetutorials.com/images/user-profile.png-->${dialogObj.image}\" alt=\"sunil\"> </div>\n" +
                    "                                        <div class=\"chat_ib\">\n" +
                    "                                            <h5 class=\"dialogsuser\">${dialogObj.name}<span class=\"chat_date\"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>\n" +
                    "                                            <p id=\"lastMsg${dialogObj.dialogId?c}\"></p>\n" +
                    "                                        </div>\n" +
                    "                                    </div>\n" +
                    "                                </div> </a>"




                dialog.prepend(div2)

            }


            </#if>


        </script>



    </#if>






        <script>


        document.addEventListener("keypress", function(e) {
        let messageInput = document.getElementById("inputtextarea");
        if (e.key === "Enter" && messageInput.value !== ' ') {
            dia = location.pathname.replace("/chat/", "")
        sendMessage(dia)
        }
        });
        </script>


    <#if dialog??>
    <script>

        function writeMsgs() {

            page = 0;
            currentCount = 0;

            //console.log("fffffffffffffffffffffffffff")


            if (document.getElementsByClassName("outgoing_msg").length === 0
                && document.getElementsByClassName("incoming_msg").length === 0 ||
                location.pathname.replace("/chat/", "") === "/chat") {


                let dia = location.pathname.replace("/chat/", "")
                let xhr = new XMLHttpRequest();
                xhr.open('GET', '/chat/nextPages?dialog_id=' + dia + '&page=' + page);
                xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
                        let json = JSON.parse(xhr.response)
                        let elems = document.getElementsByClassName("outgoing_msg")
                        let elems2 = document.getElementsByClassName("incoming_msg")

                        for (let i = 0; i < elems.length; i++) {
                            elems[i].remove()
                        }

                        for (let i = 0; i < elems2.length; i++) {
                            elems2[i].remove()
                        }

                        if (json.messages.length !== 0) {


                            //console.log("123")


                            for (let i = json.messages.length - 1; i >= 0; i--) {
                                //console.log(11)
                                //console.log(json.messages[i].sender.username)
                                //console.log('${name}')
                                if (json.messages[i].sender.username === '${name}') {

                                    let divChat = document.createElement("div")
                                    divChat.setAttribute('class', "outgoing_msg")
                                    divChat.innerHTML =
                                        "                        <div class=\"sent_msg\">\n" +
                                        "                            <p>" + json.messages[i].content + "</p>\n" +
                                        "                            <span id=" + json.messages[i].id + " class=\"time_date\">" + new Date(json.messages[i].timestamp).toLocaleDateString() + " " + new Date(json.messages[i].timestamp).toLocaleTimeString() + "</span> </div>\n"


                                    let last = document.getElementById("msg");

                                    last.prepend(divChat)
                                } else {
                                    let divChat = document.createElement("div")
                                    divChat.setAttribute('class', "incoming_msg")
                                    divChat.innerHTML =
                                        "<div class=\"incoming_msg_img\"> <img class='rounded-circle' src=" + json.messages[i].sender.avatar + " alt=\"sunil\"> </div>" +
                                        "                        <div class=\"received_msg\">\n" +
                                        "                        <div class=\"received_withd_msg\">\n" +
                                        "                            <p>" + json.messages[i].content + "</p>\n" +
                                        "                            <span class=\"time_date\">" + new Date(json.messages[i].timestamp).toLocaleDateString() + " " + new Date(json.messages[i].timestamp).toLocaleTimeString() + "</span> </div> </div>\n"


                                    let last = document.getElementById("msg");

                                    last.prepend(divChat)


                                }
                            }
                        }


                        count--
                        var div10 = $("#msg");
                        div10.scrollTop(div10.prop('scrollHeight'));
                    }
                };
                xhr.send();

            }
        }
        writeMsgs()
    </script>
    </#if>


        <#--<script>
            $('a').click(function(event){
                $.ajax({url:$(this).attr('href'),
                        success: function(data){
                            console.log("9999999")
                            $('div').html(data);

                    }});
//Запрещаем стандартную реакцию на ссылку
                event.preventDefault();
            });
        </script>-->




    <#--<script>
        barba.init({
            transitions: [{
                name: 'opacity-transition',
                leave(data) {
                    return gsap.to(data.current.container, {
                        opacity: 0
                    });
                },
                enter(data) {
                    return gsap.from(data.next.container, {
                        opacity: 0
                    });
                }
            }]
        });
    </script>-->

    <script src="https://cdn.jsdelivr.net/npm/@barba/core"></script>
<#--    <script src="https://unpkg.com/@barba/core"></script>-->
    <script src="https://unpkg.com/gsap@latest/dist/gsap.min.js"></script>

    <script>
        barba.init({
            transitions: [{
                name: 'default-transition',

                /*beforeOnce(data) {


                    console.log(location.pathname.replace("/chat/", ""))
                },*/
                /*path: "/chat/!**",*/

                //from: {namespace: ['home']},
                //both: {namespace: ['home']},
                //to: {namespace: ['msgs']},

                before(data) {

                    /*if (document.getElementsByClassName('active_chat').length !==0) {
                        let active = document.getElementsByClassName('active_chat')[0]
                        active.classList.remove('active_chat')
                    }*/



                    /*count = 0
                    page = 0;
                    currentCount = 0;*/
                    delete count
                    delete page
                    delete currentCount
                    delete xhr
                    delete dialog
                    delete div2
                    delete dialogsName
                    delete onclicks
                    delete dialogsNameArr

                    //let diaActual = location.pathname.replace("/chat/", "")

                    let prevDialogPath = data.current.url.path

                    if (prevDialogPath.toString().replace("/chat", "") === "") {

                    } else if (prevDialogPath.toString().replace("/chat/", "") !== "") {
                        let prevDialog = prevDialogPath.toString().replace("/chat/", "")


                        let idHref2 = document.getElementById(prevDialog + "href")
                        //console.log(idHref)
                        // console.log("ДДДДДДДДДДДДДДДДДДДДДДДд")
                        //idHref2.setAttribute('href', prevDialogPath)


                        //stompClient.unsubscribe("sub-0")

                    }


                    let previousDialog = document.getElementById(data.next.url.path.replace("/chat/", "") + "href");
                    if (previousDialog !== null) {
                        previousDialog.removeAttribute("href")
                    }

                    /*$('script').stop()*/
                },
                leave() {

                    // create your stunning leave animation here
                },
                /*enter() {

                    // create your amazing enter animation here
                },*/
                after(data) {

                    //let diaActual = location.pathname.replace("/chat/", "")
                    //let idHref= document.getElementById(diaActual + "href")
                    //console.log(idHref)
                    //idHref.removeAttribute('href')



                    if (data.current.url.path.includes("/chat/")) {

                        let currentDialog = document.getElementById(data.current.url.path.replace("/chat/", "") + "href");
                        if (currentDialog !== null) {
                            currentDialog.setAttribute("href", "/chat/" + data.current.url.path.replace("/chat/", ""))
                        }
                        /*let currentDialog = document.getElementById(data.next.url.path.replace("/chat/", "") + "href");
                        console.log(currentDialog)
                        currentDialog.removeAttribute("href")*/
                    }



                    let active = document.getElementsByClassName("active_chat")

                    console.log(active)
                    if (active.length !== 0) {
                        for (let i = 0; i < active.length; i++) {
                            active[i].classList.remove("active_chat")
                        }
                    }


                    //$('script').remove().appendTo('body');

                    /*$('script').run()*/

                    let dia = location.pathname.replace("/chat/", "")
                    let id2 = document.getElementById(dia);
                    id2.setAttribute('class', "chat_list active_chat")
                    /*if (location.href.includes('/chat/<#--${dialog?c}-->')) {*/
                        /*let active = document.getElementsByClassName('active_chat')[0]
                        active.classList.remove('active_chat')*/
                        let id = document.getElementById(location.pathname.replace("/chat/", ""));
                        if (id.value !== "") {
                            id.setAttribute('class', "chat_list active_chat")
                        }
                  // }





                    let count = 0
                    let page = 0;
                    let currentCount = 0;

                    //console.log("fffffffffffffffffffffffffff")


                    if (document.getElementsByClassName("outgoing_msg").length === 0
                        && document.getElementsByClassName("incoming_msg").length ===0) {
                        //writeMsgs()
                        if (count===0 && (document.getElementsByClassName("outgoing_msg").length === 0
                            && document.getElementsByClassName("incoming_msg").length === 0)) {
                            count++


                            let dia = location.pathname.replace("/chat/", "")
                            let xhr = new XMLHttpRequest();
                            xhr.open('GET', '/chat/nextPages?dialog_id=' + dia +'&page=' + page);
                            xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
                            xhr.onreadystatechange = function () {
                                if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
                                    let json = JSON.parse(xhr.response)


                                    if (json.messages.length !==0) {




                                        //console.log("123")



                                        for (let i = json.messages.length - 1; i >= 0; i--) {
                                            //console.log(11)
                                            //console.log(json.messages[i].sender.username)
                                            //console.log('')
                                            if (json.messages[i].sender.username === '${name}') {

                                                let divChat = document.createElement("div")
                                                divChat.setAttribute('class', "outgoing_msg")
                                                divChat.innerHTML =
                                                    "                        <div class=\"sent_msg\">\n" +
                                                    "                            <p>" + json.messages[i].content + "</p>\n" +
                                                    "                            <span id=" + json.messages[i].id + " class=\"time_date\">" + new Date(json.messages[i].timestamp).toLocaleDateString() + " " + new Date(json.messages[i].timestamp).toLocaleTimeString() + "</span> </div>\n"


                                                let last = document.getElementById("msg");

                                                last.prepend(divChat)
                                            } else {
                                                let divChat = document.createElement("div")
                                                divChat.setAttribute('class', "incoming_msg")
                                                divChat.innerHTML =
                                                    "<div class=\"incoming_msg_img\"> <img class='rounded-circle' src=" + json.messages[i].sender.avatar + " alt=\"sunil\"> </div>" +
                                                    "                        <div class=\"received_msg\">\n" +
                                                    "                        <div class=\"received_withd_msg\">\n" +
                                                    "                            <p>" + json.messages[i].content + "</p>\n" +
                                                    "                            <span class=\"time_date\">" + new Date(json.messages[i].timestamp).toLocaleDateString() + " " + new Date(json.messages[i].timestamp).toLocaleTimeString() + "</span> </div> </div>\n"


                                                let last = document.getElementById("msg");

                                                last.prepend(divChat)


                                            }
                                        }
                                    }






                                    count--
                                    var div10 = $("#msg");
                                    div10.scrollTop(div10.prop('scrollHeight'));
                                }
                            };
                            xhr.send();


                            if (data.next.url.path.replace("/chat/", "") !== "" ||
                                data.next.url.path.replace("/chat/", "") !== "/chat") {
                                console.log("00909099090")
                                let textArea = document.createElement('div')
                                textArea.setAttribute("id", "msgArea")
                                textArea.classList.add("type_msg")
                                textArea.innerHTML = "<div class=\"input_msg_write\">\n" +
                                    "                        <input type=\"text\" class=\"write_msg\" id=\"inputtextarea\" placeholder=\"Напишите сообщение\"/>\n" +
                                    "                        <button onclick=\"sendMessage(location.pathname.replace('/chat/', ''))\" class=\"msg_send_btn\" type=\"button\"><i class=\"fa fa-paper-plane-o\" aria-hidden=\"true\"></i></button>\n" +
                                    "                    </div>"

                                let last2 = document.getElementById("msg");

                                last2.after(textArea)


                            }

                        }
                    }


                    /**/






                }

            }]

            /*prevent: (el, event, href) => {

                if (href.includes("/quizzes")) {
            return true
        } else return false},*/
            //debug: true,
        });


        /*barba.hooks.after((data) => {



        });*/



    </script>

    <script type="text/javascript">
        function funonload() {

            let dia = location.pathname.replace("/chat/", "")

            if (location.href.includes('/chat/' + dia)) {
                let id = document.getElementById(dia);
                id.setAttribute('class', "chat_list active_chat")

                let idHref= document.getElementById(dia + "href")
                //console.log(idHref)
                idHref.removeAttribute('href')
            }
        }

        window.onload = funonload;
    </script>



</@e.page>