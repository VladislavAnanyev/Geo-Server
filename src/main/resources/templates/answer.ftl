<#import "parts/common.ftl" as e>
<@e.page>

<#--<form method="post" action="/api/quizzes/${id}/solve" class="form-inline">

    <div><label> Ответ : <input type="text" name="answer"/> </label></div>
    <button type="submit" class="btn btn-primary ml-2">Ответить</button>
</form>-->

    <script src="/static/answer.js"></script>

    <div id="test">
    <div>${quiz.title}</div>
    <div>${quiz.text}</div>
    <#list quiz.options as options>
    <div class="custom-control custom-checkbox mt-2">
<#--    <div class="form-check" id="checkRadio">-->
        <input type="checkbox" class="custom-control-input" id="${options_index}" name="check" value="${options_index}">
<#--        <input class="form-check-input" type="radio" name="exampleRadios" id="exampleRadios1" value="${options_index}" >-->
        <label class="custom-control-label" for="${options_index}">
            ${options}
        </label>
<#--        <label class="form-check-label" for="exampleRadios1">-->
<#--            ${options}-->
<#--        </label>-->
    </div>
    </#list>
    </div>
    <button type="submit" class="btn btn-primary ml-2 mt-2" onclick="f(${quiz.id?c})">Ответить</button>



</@e.page>