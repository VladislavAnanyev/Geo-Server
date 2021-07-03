<#import "parts/common.ftl" as c>
<@c.page>
    <title>Статистика</title>
<#--    <div>${stat}%</div>-->
    <table class="table table-hover">
        <thead>
        <tr>
            <th scope="col">№</th>
            <th scope="col">Имя</th>
            <th scope="col">Фамилия</th>
            <th scope="col">Логин</th>
            <th scope="col">Время</th>
            <th scope="col">Процент</th>
        </tr>
        </thead>
        <tbody>


        <#list answersOnQuiz as answer>

            <tr>
                <th scope="row">${answer_index + 1}</th>
                <td>${answer.user.firstName}</td>
                <td>${answer.user.lastName}</td>
                <#--            <td>${answer.completedAt.time?datetime}</td>-->
                <td><a href="/about/${answer.user.username}">${answer.user.username}</a></td>
                <td>
                    <#if answer.completedAt??>
                        ${answer.completedAt.time?datetime?string ["dd.MM.yyyy HH:mm:ss"]}
                        <#else>
                        -
                    </#if>

                </td>
                <#--            <td>${answer.status?c}</td>&ndash;&gt;-->



                <td>
                    <#if answer.percent??>
                        ${answer.percent}
                    <#else>
                        -
                    </#if>
                </td>


            </tr>


        </#list>

        </tbody>
    </table>
</@c.page>