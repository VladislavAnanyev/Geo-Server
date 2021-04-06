<#import "parts/common.ftl" as e>
<@e.page>

<#--<form method="post" action="/api/quizzes/${id}/solve" class="form-inline">

    <div><label> Ответ : <input type="text" name="answer"/> </label></div>
    <button type="submit" class="btn btn-primary ml-2">Ответить</button>
</form>-->

    <script src="/static/answer.js"></script>

    <#list test as quiz>
    <div class = "quiz mt-2">
        <div id="test${quiz_index}">
        <div>${quiz.title}</div>
        <div>${quiz.text}</div>
        <#list quiz.options as options>
        <div class="custom-control custom-checkbox mt-2">
            <input type="checkbox" class="custom-control-input answers${options_index}" id="${options_index}_${quiz.id}" name="check${quiz?index}" value="${options_index}">
            <label class="custom-control-label" for="${options_index}_${quiz.id}">
                ${options}
            </label>
        </div>
        </#list>
        </div>

    </div>
    </#list>

    <button type="submit" class="btn btn-primary ml-2 mt-2" onclick="f(${test_id.id?c})">Ответить</button>



</@e.page>