<#import "parts/common.ftl" as e>
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
    <div class="container">
        <div class="messaging">
            <div class="inbox_msg">
                <div class="inbox_people">
                    <div class="headind_srch">
                        <div class="recent_heading">
                            <h4>Recent</h4>
                        </div>
                        <div class="srch_bar">
                            <div class="stylish-input-group">
                                <input type="text" class="search-bar"  placeholder="Search" >
                                <span class="input-group-addon">
                <button type="button"> <i class="fa fa-search" aria-hidden="true"></i> </button>
                </span> </div>
                        </div>
                    </div>
                    <div class="inbox_chat">
                        <#list dialogs as dialog>
                        <div class="chat_list <#--active_chat-->" onsubmit="/chat/${dialog}">
                            <div class="chat_people">
                                <div class="chat_img"> <img src="https://ptetutorials.com/images/user-profile.png" alt="sunil"> </div>
                                <div class="chat_ib">
                                    <h5> ${dialog} <span class="chat_date"><#--${messages[messages?size - 1].timestamp.time?date}--></span></h5>
                                    <p><#--${messages[messages?size - 1].content}--></p>
                                </div>
                            </div>
                        </div>
                        </#list>

                    </div>
                </div>
                <div class="mesgs">
                    <div class="msg_history" id="msg">



                    </div>
                    <#--<div class="type_msg">
                        <div class="input_msg_write">
                            <input type="text" class="write_msg" id="inputtext" placeholder="Type a message" />
                            <button onclick="sendMsg('${user.username}')" class="msg_send_btn" type="button"><i class="fa fa-paper-plane-o" aria-hidden="true"></i></button>
                        </div>
                    </div>-->
                </div>
            </div>



        </div></div>
    </body>
    </html>
</@e.page>