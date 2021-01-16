<#import "parts/common.ftl" as c>
<@c.page>
<#--    <div>${stat}%</div>-->
    <table class="table table-hover">
        <thead>
        <tr>
            <th scope="col">Имя</th>
            <th scope="col">Фамилия</th>
            <th scope="col">Username</th>
            <th scope="col">Статус</th>
        </tr>
        </thead>
        <tbody>

        <#list answersOnQuiz as answer >

        <tr>
<#--            <th scope="row">1</th>-->
            <td>${answer.user.firstName}</td>
            <td>${answer.user.lastName}</td>
<#--            <td>${answer.completedAt.time?datetime}</td>-->
            <td>${answer.user.username}</td>
            <td>${answer.status?c}</td>
        </tr>
        </#list>


        </tbody>
    </table>
</@c.page>