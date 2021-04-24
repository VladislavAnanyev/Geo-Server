<#import "parts/common.ftl" as e>
<#include "parts/security.ftl">
<@e.page>
<link href="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
<script src="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<!------ Include the above in your HEAD tag ---------->


<html>
<head>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.4.0/sockjs.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css" type="text/css" rel="stylesheet">

    <link rel="stylesheet" href="../static/chatcss.css">



</head>
<body>
<script src="../static/sendMessage.js"></script>
<script src="../static/chat.js"></script>
<script src="../static/custom.js"></script>
<script src="../static/activeChat.js"></script>
<script>connect()</script>


<div class="container">
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

                        <#list lastDialogs as dialog>
                            <#if dialog.recipient.username == myUsername.username>
                                <div id="${dialog.sender.username}" onclick="activeChat(${dialog.sender.username})"  class="chat_list <#--active_chat-->">
                                    <div class="chat_people">
                                        <div class="chat_img"> <img src="<#--https://ptetutorials.com/images/user-profile.png-->../../../../img/${dialog.sender.avatar}.jpg" alt="sunil"> </div>
                                        <div class="chat_ib">
                                            <h5 class="dialogsuser">${dialog.sender.username}<span class="chat_date"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>
                                            <p>${dialog.content}</p>
                                        </div>
                                    </div>
                                </div>
                            <#else>

                                <div id="${dialog.recipient.username}" onclick="activeChat(${dialog.recipient.username})"  class="chat_list <#--active_chat-->">
                                    <div class="chat_people">
                                        <div class="chat_img"> <img src="<#--https://ptetutorials.com/images/user-profile.png-->../../../../img/${dialog.recipient.avatar}.jpg" alt="sunil"> </div>
                                        <div class="chat_ib">
                                            <h5 class="dialogsuser">${dialog.recipient.username}<span class="chat_date"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>
                                            <p>${dialog.content}</p>
                                        </div>
                                    </div>
                                </div>

                            </#if>
                        </#list>

                   <#-- </div>-->

                    <script type="text/javascript">
                        function funonload() {
                            if (location.href.includes('/chat/${user.username}')) {
                                let id = document.getElementById("${user.username}");
                                id.setAttribute('class',"chat_list active_chat")
                            }
                        }
                        window.onload = funonload;
                    </script>

                </div>

            </div>
            <div class="mesgs">
                <div class="msg_history" id="msg">
                    <#list messages as msg>
                        <#if msg.recipient.username == user.username>


                            <div class="outgoing_msg">
                                <div class="sent_msg">
                                    <p>${msg.content}</p>
                                    <span class="time_date"> ${msg.timestamp.time?datetime}</span> </div>
                            </div>

                        <#else>


                            <div class="incoming_msg">
                                <div class="incoming_msg_img"> <img src="../../../../img/${msg.sender.avatar}.jpg" alt="sunil"> </div>
                                <div class="received_msg">
                                    <div class="received_withd_msg">
                                        <p>${msg.content}</p>
                                        <span class="time_date">${msg.timestamp.time?datetime} </span>
                                    </div>
                                </div>
                            </div>


                        </#if>





                    </#list>



                </div>
                <div class="type_msg">
                    <div class="input_msg_write">
                        <input type="text" class="write_msg" id="inputtext" placeholder="Type a message" />
                        <#--<button onclick="sendMsg('${user.username}')" class="msg_send_btn" type="button"><i class="fa fa-paper-plane-o" aria-hidden="true"></i></button>-->
                        <button onclick="sendMessage('${name}', '${user.username}')" class="msg_send_btn" type="button"><i class="fa fa-paper-plane-o" aria-hidden="true"></i></button>
                    </div>
                </div>
            </div>
        </div>



    </div>
</div>

<script>
    let dialog = document.getElementById("dialogs")
    let div2 = document.createElement("div")
    let dialogsName = document.getElementsByClassName("dialogsuser")
    let dialogsNameArr = []


    div2.setAttribute('id', "${user.username}")
    div2.setAttribute('class', "chat_list")



    for (let i = 0; i < dialogsName.length; i++) {
        dialogsNameArr.push(dialogsName[i].textContent)
        console.log(dialogsName[i].textContent)
    }
    //div2.setAttribute('class', "inbox_chat")
    div2.innerHTML =
        "                                    <div class=\"chat_people\">\n" +
        "                                        <div class=\"chat_img\"> <img src=\"<#--https://ptetutorials.com/images/user-profile.png-->../../../../img/${user.avatar}.jpg\" alt=\"sunil\"> </div>\n" +
        "                                        <div class=\"chat_ib\">\n" +
        "                                            <h5 class=\"dialogsuser\">${user.username}<span class=\"chat_date\"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>\n" +
        "                                            <p></p>\n" +
        "                                        </div>\n" +
        "                                    </div>\n" +
        "                                </div>"

    console.log(dialogsNameArr.indexOf("${user.username}"))
    if (dialogsNameArr.indexOf("${user.username}") == -1) {
    dialog.prepend(div2)

    }

</script>
</body>
</html>
</@e.page>