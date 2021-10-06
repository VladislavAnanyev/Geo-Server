<#import "parts/common.ftl" as e>
<#include "parts/security.ftl">

<@e.page>

<#--    <link href="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">-->

<!------ Include the above in your HEAD tag ---------->


    <title>Сообщения</title>

<#--    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css" type="text/css" rel="stylesheet">-->

    <link rel="stylesheet" href="../static/chatcss.css">





<#if dialog??>
<input id="dialogId" type="hidden" value="${dialog?c}">
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

                    </#list>

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

                    </div>
                </div>
                <div class="inbox_chat" id="dialogs">

                        <#list lastDialogs?if_exists as dialog>


                            <#if dialog.dialog.users?size = 2 <#--|| (dialogObj.users?size = 1 && !dialogObj.name??)-->>


                            <#--                            Переделать-->

                                <#if dialog.sender.username != myUsername.username>

                                    <div id="${dialog.dialog.dialogId?c}" <#--последнее сообщение не от меня--> onclick="location.href='/chat/${dialog.dialog.dialogId?c}'" class="chat_list <#--active_chat-->">
                                        <div class="chat_people">
                                            <div class="chat_img"> <img class="rounded-circle" width="43.26px" height="43.26px" src="${dialog.sender.avatar}"  alt="sunil"> </div>
                                            <div class="chat_ib последнее сообщение не от меня">
                                                <h5 class="dialogsuser">${dialog.sender.username}<span class="chat_date"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>
                                                <p id="lastMsg${dialog.dialog.dialogId?c}">${dialog.content}</p>
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

                                    <div id="${dialog.dialog.dialogId?c}" <#--последнее сообщение от меня--> onclick="location.href='/chat/${dialog.dialog.dialogId?c}'" class="chat_list <#--active_chat-->">
                                        <div class="chat_people">
                                            <div class="chat_img"> <img class="rounded-circle" width="43.26px" height="43.26px" src="${o.avatar}" alt="sunil"> </div>
                                            <div class="chat_ib последнее сообщение от меня">
                                                <h5 class="dialogsuser">${o.username}<span class="chat_date"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>
                                                <p id="lastMsg${dialog.dialog.dialogId?c}">${dialog.content}</p>
                                            </div>
                                        </div>
                                    </div>

                                </#if>

                            <#else>

                                <div id="${dialog.dialog.dialogId?c}" onclick="location.href='/chat/${dialog.dialog.dialogId?c}'" class="chat_list <#--active_chat-->">
                                    <div class="chat_people">
                                        <div class="chat_img"> <#if dialog.dialog.image??><img class="rounded-circle" width="43.26px" height="43.26px" src="<../../../../img/${dialog.dialog.image}.jpg"  alt="sunil"> </#if></div>
                                        <div class="chat_ib группа">
                                            <h5 class="dialogsuser"><#if dialog.dialog.name??>${dialog.dialog.name?string}</#if><span class="chat_date"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>
                                            <p id="lastMsg${dialog.dialog.dialogId?c}">${dialog.content}</p>
                                        </div>
                                    </div>
                                </div>

                           </#if>



                        </#list>

<#--                </div>-->



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
                                <div class="incoming_msg_img"> <img class="rounded-circle" width="43.26px" height="43.26px" src="${msg.sender.avatar}"  alt="sunil"> </div>
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
                        <input type="text" class="write_msg" id="inputtextarea" placeholder="Type a message"/>
                        <button onclick="sendMessage('${dialog?c}')" class="msg_send_btn" type="button"><i class="fa fa-paper-plane-o" aria-hidden="true"></i></button>
                    </div>
                </div>


                </#if>
            </div>
        </div>



    </div>
</div>






<script src="../static/chat.js"></script>
<#--<script src="../static/custom.js"></script>-->
<script src="../static/activeChat.js"></script>
<script src="../static/createGroup.js"></script>
<script src="../static/newActiveDialog.js"></script>
<#--<script src="/static/getUserList.js"></script>-->
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.4.0/sockjs.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
<script>connect()</script>


<script src="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

    <#if dialog??>



        <script>

            var div3 = $("#msg");
            div3.scrollTop(div3.prop('scrollHeight'));

            document.addEventListener("keypress", function(e) {
                let messageInput = document.getElementById("inputtextarea");
                if (e.key === "Enter" && messageInput.value !== ' '){
                    sendMessage('${dialog?c}')
                }
            });

        </script>
    </#if>


    <#if dialog?? >


        <#if dialogObj.users?first.username = myUsername.username >
            <#assign c = dialogObj.users?last>
        <#else>
            <#assign c = dialogObj.users?first>
        </#if>

        <script>

            let dialog = document.getElementById("dialogs")
            let div2 = document.createElement("div")
            let dialogsName = document.getElementsByClassName("chat_list")

            let dialogsNameArr = []


            div2.setAttribute('id', "${dialog?c}")
            div2.setAttribute('class', "chat_list")
            div2.setAttribute('onclick', "location.href='/chat/${dialog?c}'")



            for (let i = 0; i < dialogsName.length; i++) {
                dialogsNameArr.push(dialogsName[i].id)
                console.log(dialogsName[i].id)
            }

            <#if dialogObj.users?size = 2 <#--|| (dialogObj.users?size = 1 && !dialogObj.name??)-->>


            //div2.setAttribute('class', "inbox_chat")

            if (dialogsNameArr.indexOf("${dialog?c}") == -1) {

                div2.innerHTML =
                    "                                    <div class=\"chat_people\">\n" +
                    "                                        <div class=\"chat_img\"> <img class=\"rounded-circle\" width=\"43.26px\" height=\"43.26px\" src=\"${c.avatar}\" alt=\"sunil\"> </div>\n" +
                    "                                        <div class=\"chat_ib\">\n" +
                    "                                            <h5 class=\"dialogsuser\">${c.username}<span class=\"chat_date\"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>\n" +
                    "                                            <p></p>\n" +
                    "                                        </div>\n" +
                    "                                    </div>\n" +
                    "                                </div>"

                console.log(dialogsNameArr.indexOf("${dialog?c}"))


                dialog.prepend(div2)


            }


            <#else>


            //div2.setAttribute('class', "inbox_chat")

            if (dialogsNameArr.indexOf("${dialog?c}") == -1) {

                div2.innerHTML =
                    "                                    <div class=\"chat_people\">\n" +
                    "                                        <div class=\"chat_img\"> <img class=\"rounded-circle\" width=\"43.26px\" height=\"43.26px\" src=\"<#--https://ptetutorials.com/images/user-profile.png-->../../../../img/${dialogObj.image}.jpg\" alt=\"sunil\"> </div>\n" +
                    "                                        <div class=\"chat_ib\">\n" +
                    "                                            <h5 class=\"dialogsuser\">${dialogObj.name}<span class=\"chat_date\"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>\n" +
                    "                                            <p id=\"lastMsg${dialog?c}\"></p>\n" +
                    "                                        </div>\n" +
                    "                                    </div>\n" +
                    "                                </div>"

                console.log(dialogsNameArr.indexOf("${dialog?c}"))

                dialog.prepend(div2)

            }


            </#if>


        </script>



    </#if>


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

</@e.page>