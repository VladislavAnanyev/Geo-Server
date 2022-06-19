<#import "parts/common.ftlh" as e>
<#--<#include "parts/security.ftlh">-->

<@e.page>

    <div class="row">

        <#if friends??>
            <#list friends as friend>
                <div class="col-sm-6" id="${friend.username}">

                    <div class="card mt-2" style="width: 18rem;">
                        <img class="rounded-circle" src="${friend.avatar}" alt="...">
                        <div class="card-body">
                            <h5 class="card-title">${friend.firstName} ${friend.lastName}</h5>
                            <#--                    <p class="card-text">${meeting.time?datetime?string["dd.MM.yyyy HH:mm:ss"]}</p>-->
                            <a onclick="writeMsg('${friend.userId?c}')" href="#" class="btn btn-primary mb-2">Написать сообщение</a>
                            <a onclick="deleteFriend('${friend.userId?c}')" href="#" class="btn btn-primary">Удалить из друзей</a>

                        </div>
                    </div>
                </div>
            </#list>
        </#if>
    </div>


    <script src="/static/newActiveDialog.js"></script>
    <script src="/static/deletereq.js"></script>
</@e.page>