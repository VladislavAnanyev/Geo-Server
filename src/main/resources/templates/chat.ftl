<#import "parts/common.ftl" as e>
<#include "parts/security.ftl">

<@e.page>

    <link href="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
<script src="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<!------ Include the above in your HEAD tag ---------->


<html>
<head>
    <title>Сообщения</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.4.0/sockjs.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css" type="text/css" rel="stylesheet">

    <link rel="stylesheet" href="../static/chatcss.css">





</head>
<body>
<script src="../static/chat.js"></script>
<script src="../static/custom.js"></script>
<script src="../static/activeChat.js"></script>
<script src="../static/createGroup.js"></script>
<script src="../static/newActiveDialog.js"></script>
<#--<script src="/static/getUserList.js"></script>-->
<script>connect()</script>

<#if dialog_id??>
<input id="dialogId" type="hidden" value="${dialog_id?string}">
</#if>
<div class="container">
    <button type="button" <#--onclick="getUserList()"--> class="btn btn-primary mb-2" data-toggle="modal" data-target="#staticBackdrop">Создать группу</button>

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
                        <input class="form-check-input users" type="checkbox" value="" id="flexCheckDefault">
                        <label class="form-check-label usersValue" for="flexCheckDefault">
                            ${user.username}
                        </label>
                    </div>
                        </#if>
                    <#--<div class="form-check">
                        <input class="form-check-input" type="checkbox" value="" id="flexCheckChecked" checked>
                        <label class="form-check-label" for="flexCheckChecked">
                            Checked checkbox
                        </label>
                    </div>-->
                    </#list>
                    <#--<div class="form-group"> Вы действительно хотите изменить пароль?

                        &lt;#&ndash;                        <input type="text" class="form-control" id="oldpassword" placeholder="Придумайте новый пароль"  aria-describedby="emailHelp" name="password">&ndash;&gt;
                        &lt;#&ndash;<input type="text" class="form-control" id="password1" placeholder="Придумайте новый пароль"  aria-describedby="emailHelp" name="password1">
                        <input type="text" class="form-control mt-3" id="password2" placeholder="Введите новый пароль ещё раз" aria-describedby="emailHelp" name="password2">&ndash;&gt;
                    </div>-->
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Назад</button>
                    <button type="button" onclick="createGroup()" data-dismiss="modal" class="btn btn-primary">Создать</button>
                </div>

            </div>
        </div>



    </div>

    <div class="messaging">
        <div class="inbox_msg">
            <div class="inbox_people">
                <div class="headind_srch">
                    <div class="recent_heading">
                        <h4 id="autoUs">${name}</h4>
                    </div>
                    <div class="srch_bar">
                        <div class="stylish-input-group">
                            <input type="text" class="search-bar"  placeholder="Search" >
                            <span class="input-group-addon">
                <button type="button"> <i class="fa fa-search" aria-hidden="true"></i> </button>
                </span> </div>
                    </div>
                </div>
                <div class="inbox_chat" id="dialogs">

                        <#list lastDialogs?if_exists as dialog>


                            <#if dialog.dialog.users?size = 2>


                            <#--                            Переделать-->

                                <#if dialog.sender.username != myUsername.username>

                                    <div id="${dialog.dialog.id?c}" <#--последнее сообщение не от меня--> onclick="location.href='/chat/${dialog.dialog.id?c}'" class="chat_list <#--active_chat-->">
                                        <div class="chat_people">
                                            <div class="chat_img"> <img class="rounded-circle" src="<#--https://ptetutorials.com/images/user-profile.png-->../../../../img/${dialog.sender.avatar}.jpg" alt="sunil"> </div>
                                            <div class="chat_ib последнее сообщение не от меня">
                                                <h5 class="dialogsuser">${dialog.sender.username}<span class="chat_date"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>
                                                <p id="lastMsg${dialog.dialog.id?c}">${dialog.content}</p>
                                            </div>
                                        </div>
                                    </div>
                                <#else>


    <#--                                Второй пользователь-->
                                    <#if dialog.dialog.users?first.username = myUsername.username >
                                        <#assign o = dialog.dialog.users?last>
                                    <#else>
                                        <#assign o = dialog.dialog.users?first>
                                    </#if>

                                    <div id="${dialog.dialog.id?c}" <#--последнее сообщение от меня--> onclick="location.href='/chat/${dialog.dialog.id?c}'" class="chat_list <#--active_chat-->">
                                        <div class="chat_people">
                                            <div class="chat_img"> <img class="rounded-circle" src="<#--https://ptetutorials.com/images/user-profile.png-->../../../../img/${o.avatar}.jpg" alt="sunil"> </div>
                                            <div class="chat_ib последнее сообщение от меня">
                                                <h5 class="dialogsuser">${o.username}<span class="chat_date"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>
                                                <p id="lastMsg${dialog.dialog.id?c}">${dialog.content}</p>
                                            </div>
                                        </div>
                                    </div>

                                </#if>

                            <#else>

                                <div id="${dialog.dialog.id?c}" onclick="location.href='/chat/${dialog.dialog.id?c}'" class="chat_list <#--active_chat-->">
                                    <div class="chat_people">
                                        <div class="chat_img"> <img class="rounded-circle" src="<../../../../img/${dialog.dialog.image}.jpg" alt="sunil"> </div>
                                        <div class="chat_ib группа">
                                            <h5 class="dialogsuser">${dialog.dialog.name?string}<span class="chat_date"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>
                                            <p id="lastMsg${dialog.dialog.id?c}">${dialog.content}</p>
                                        </div>
                                    </div>
                                </div>

                           </#if>



                        </#list>

<#--                </div>-->

                    <#if dialog??>
                    <script type="text/javascript">
                        function funonload() {

                                if (location.href.includes('/chat/${dialog?c}')) {
                                    let id = document.getElementById("${dialog?c}");
                                    id.setAttribute('class', "chat_list active_chat")
                                }

                        }
                        window.onload = funonload;
                    </script>
                    </#if>

                </div>

            </div>
            <div class="mesgs" id="msgcont">
                <div class="msg_history" id="msg">
                    <#list messages?if_exists as msg>
                        <#if msg.sender.username == myUsername.username>

                            <div class="outgoing_msg">
                                <div class="sent_msg">
                                    <p>${msg.content}</p>
                                    <span class="time_date">${msg.timestamp.time?datetime?string ["dd.MM.yyyy HH:mm:ss"]}</span> </div>
                            </div>


                        <#else>


                            <div class="incoming_msg">
                                <div class="incoming_msg_img"> <img class="rounded-circle" src="../../../../img/${msg.sender.avatar}.jpg" alt="sunil"> </div>
                                <div class="received_msg">
                                    <div class="received_withd_msg">
                                        <p>${msg.content}</p>
                                        <span class="time_date">${msg.timestamp.time?datetime?string ["dd.MM.yyyy HH:mm:ss"]} </span>
                                    </div>
                                </div>
                            </div>


                        </#if>

                    </#list>



                </div>
                <#if dialog??>
                <div class="type_msg">
                    <div class="input_msg_write">
                        <input type="text" class="write_msg" id="inputtext" placeholder="Type a message"/>
                        <button onclick="sendMessage('${name}', '${dialog?c}')" class="msg_send_btn" type="button"><i class="fa fa-paper-plane-o" aria-hidden="true"></i></button>

                    </div>
                </div>

                    <#--<#else>

                    <div class="type_msg">
                        <div class="input_msg_write">
                            <input type="text" class="write_msg" id="inputtext" placeholder="Type a message" />
                            &lt;#&ndash;<button onclick="sendMsg('${user.username}')" class="msg_send_btn" type="button"><i class="fa fa-paper-plane-o" aria-hidden="true"></i></button>&ndash;&gt;
                            <button onclick="sendMessage('${name}', 'null')" class="msg_send_btn" type="button"><i class="fa fa-paper-plane-o" aria-hidden="true"></i></button>
                        </div>
                    </div>-->




                </#if>
            </div>
        </div>



    </div>
</div>



<#if dialog?? >


    <#if dialogObj.users?first.username = myUsername.username >
        <#assign c = dialogObj.users?last>
    <#else>
        <#assign c = dialogObj.users?first>
    </#if>

<script>

    <#if dialogObj.users?size = 2>

    let dialog = document.getElementById("dialogs")
    let div2 = document.createElement("div")
    let dialogsName = document.getElementsByClassName("chat_list")

    let dialogsNameArr = []


    div2.setAttribute('id', "${dialog?c}")
    div2.setAttribute('class', "chat_list")



    for (let i = 0; i < dialogsName.length; i++) {
        dialogsNameArr.push(dialogsName[i].id)
        console.log(dialogsName[i].id)
    }
    //div2.setAttribute('class', "inbox_chat")
    div2.innerHTML =
        "                                    <div class=\"chat_people\">\n" +
        "                                        <div class=\"chat_img\"> <img class=\"rounded-circle\" src=\"<#--https://ptetutorials.com/images/user-profile.png-->../../../../img/${c.avatar}.jpg\" alt=\"sunil\"> </div>\n" +
        "                                        <div class=\"chat_ib\">\n" +
        "                                            <h5 class=\"dialogsuser\">${c.username}<span class=\"chat_date\"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>\n" +
        "                                            <p></p>\n" +
        "                                        </div>\n" +
        "                                    </div>\n" +
        "                                </div>"

    console.log(dialogsNameArr.indexOf("${dialog?c}"))
    if (dialogsNameArr.indexOf("${dialog?c}") == -1) {
    dialog.prepend(div2)


    }


    <#else>

    let dialog = document.getElementById("dialogs")
    let div2 = document.createElement("div")
    let dialogsName = document.getElementsByClassName("chat_list")

    let dialogsNameArr = []


    div2.setAttribute('id', "${dialog?c}")
    div2.setAttribute('class', "chat_list")



    for (let i = 0; i < dialogsName.length; i++) {
        dialogsNameArr.push(dialogsName[i].id)
        console.log(dialogsName[i].id)
    }
    //div2.setAttribute('class', "inbox_chat")
    div2.innerHTML =
        "                                    <div class=\"chat_people\">\n" +
        "                                        <div class=\"chat_img\"> <img class=\"rounded-circle\" src=\"<#--https://ptetutorials.com/images/user-profile.png-->../../../../img/${dialogObj.image}.jpg\" alt=\"sunil\"> </div>\n" +
        "                                        <div class=\"chat_ib\">\n" +
        "                                            <h5 class=\"dialogsuser\">${dialogObj.name}<span class=\"chat_date\"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>\n" +
        "                                            <p id=\"lastMsg${dialog?c}\"></p>\n" +
        "                                        </div>\n" +
        "                                    </div>\n" +
        "                                </div>"

    console.log(dialogsNameArr.indexOf("${dialog?c}"))
    if (dialogsNameArr.indexOf("${dialog?c}") == -1) {
        dialog.prepend(div2)


    }


    </#if>


</script>



</#if>

<#if dialog??>



    <script>

        var div3 = $("#msg");
        div3.scrollTop(div3.prop('scrollHeight'));

        document.addEventListener("keypress", function(e) {
            let messageInput = document.getElementById("inputtext");
            if (e.key === "Enter" && messageInput.value !== ' '){
                sendMessage('${name}', '${dialog?c}')
            }
        });

    </script>
</#if>

</body>
</html>
</@e.page>