<#import "parts/common.ftl" as c>
<@c.page>

    <script src="/static/putreq.js"></script>
    <script src="/static/addOptions.js"></script>

        <div class="form-group">
            <label for="exampleFormControlInput1">Название</label>
            <input type="text" class="form-control" id="exampleFormControlInput1" value="${oldQuiz.title}" name="title">
        </div>

        <div class="form-group">
            <label for="exampleFormControlInput2">Вопрос</label>
            <input type="text" class="form-control" id="exampleFormControlInput2" value="${oldQuiz.text}"  name="text">
        </div>




        <div class="options" id="optionstest">
            <label for="exampleFormControlInput3">Варианты ответа</label>
        <#list oldQuiz.options as opt>

        <div class="custom-control custom-checkbox mt-2" id="${opt_index+1}Id">
            <input type="checkbox" class="custom-control-input" id="${opt_index}" name="check"  value="${opt_index}" <#list oldQuiz.answer as ans> <#if ans == opt_index>checked</#if> </#list> >
            <label class="custom-control-label" for="${opt_index}">
                <input type="text" class="form-control" value="${opt}" id="options${opt_index+1}" name="options">
            </label>
            <button onclick="removeOptions(${opt_index + 1})" class="btn btn-primary mt-3">Удалить вариант</button>
        </div>

        </#list>

        </div>

        <div>
            <button onclick="addOptions()" class="btn btn-primary mt-2">Добавить вариант</button>
        </div>

<#--        <form method="get" action="/myquiz" >-->
        <button  onclick="updateQuiz(${oldQuiz.id?c})" type="submit" class="btn btn-primary mt-2">Изменить</button>
<#--        </form>-->



</@c.page>