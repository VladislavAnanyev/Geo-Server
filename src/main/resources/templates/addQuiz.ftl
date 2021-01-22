<#import "parts/common.ftl" as c>
<@c.page>

    <script src="/static/addQuiz.js"></script>
    <script src="/static/addOptions.js"></script>

    <div class="form-group">
        <label for="exampleFormControlInput1">Название</label>
        <input type="text" class="form-control" id="titleID" placeholder="Напишите здесь название викторины" name="title">
    </div>

    <div class="form-group">
        <label for="exampleFormControlInput1">Вопрос</label>
        <input type="text" class="form-control" id="questionID" placeholder="Напишите здесь ваш вопрос"  name="text">
    </div>

    <div class="options" id="optionstest">
        <label for="exampleFormControlInput1">Варианты ответа</label>
    <div class="custom-control custom-checkbox" id="1Id">
        <input type="checkbox" class="custom-control-input" id="customCheck1" name="check" value="0">
        <label class="custom-control-label" for="customCheck1">
            <input type="text" class="form-control" id="options1" placeholder="1)" name="options">
        </label>
        <button onclick="removeOptions(1)" class="btn btn-primary mt-3">Удалить вариант</button>

    </div>

    <div class="custom-control custom-checkbox mt-2" id="2Id" >
        <input type="checkbox" class="custom-control-input" id="customCheck2" name="check" value="1">
        <label class="custom-control-label" for="customCheck2">
            <input type="text" class="form-control" id="options2" placeholder="2)" name="options">
        </label>
        <button onclick="removeOptions(2)" class="btn btn-primary mt-3">Удалить вариант</button>
    </div>

    <div class="custom-control custom-checkbox mt-2" id="3Id">
        <input type="checkbox" class="custom-control-input" id="customCheck3" name="check" value="2">
        <label class="custom-control-label" for="customCheck3">
        <input type="text" class="form-control" id="options3" placeholder="3)" name="options">
        </label>
        <button onclick="removeOptions(3)" class="btn btn-primary mt-3">Удалить вариант</button>
    </div>

    <div id="4Id" class="custom-control custom-checkbox mt-2" >
        <input type="checkbox" class="custom-control-input" id="customCheck4" name="check" value="3">
        <label class="custom-control-label" for="customCheck4">
        <input type="text" class="form-control" id="options4" placeholder="4)" name="options">
        </label>
        <button onclick="removeOptions(4)" class="btn btn-primary mt-3">Удалить вариант</button>
    </div>

    </div>

    <div>
        <button onclick="addOptions()" class="btn btn-primary mt-2">Добавить вариант</button>
    </div>

<#--    <form method="get" action="/myquiz" >-->
        <form method="get" action="/" >
        <button onclick="addQuiz()" type="submit" class="btn btn-primary mt-2">Создать</button>
    </form>

</@c.page>