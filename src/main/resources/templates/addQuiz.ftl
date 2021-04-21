<#import "parts/common.ftl" as c>
<@c.page>

    <script src="/static/addQuiz.js"></script>
    <script src="/static/addOptions.js"></script>
    <script src="/static/addTest.js"></script>

    <div id="1Id" class="quiz">
        <div class="form-group">
            <label for="exampleFormControlInput1">Название</label>
            <input type="text" class="form-control" id="titleID" placeholder="Напишите здесь название викторины" name="title">
        </div>

        <div class="form-group">
            <label for="exampleFormControlInput1">Вопрос</label>
            <input type="text" class="form-control" id="questionID" placeholder="Напишите здесь ваш вопрос"  name="text">
        </div>

        <div class="options" id="1optionstest">
            <label for="exampleFormControlInput1">Варианты ответа</label>
        <div class="custom-control custom-checkbox" id="1Id1">
            <input type="checkbox" class="custom-control-input 1input" id="1customCheck1" name="1check" value="0">
            <label class="custom-control-label 1opt" id="1label1"  for="1customCheck1">
                <input type="text" class="form-control" id="1options1" placeholder="1)" name="1options">
            </label>
            <button onclick="removeOptions(1,1)" class="btn btn-primary 1butt">Удалить вариант</button>

        </div>

        <div class="custom-control custom-checkbox mt-2" id="1Id2" >
            <input type="checkbox" class="custom-control-input 1input" id="1customCheck2" name="1check" value="1">
            <label class="custom-control-label 1opt"  id="1label2"  for="1customCheck2">
                <input type="text" class="form-control" id="1options2" placeholder="2)" name="1options">
            </label>
            <button onclick="removeOptions(1,2)" class="btn btn-primary 1butt">Удалить вариант</button>
        </div>

        <div class="custom-control custom-checkbox mt-2" id="1Id3">
            <input type="checkbox" class="custom-control-input 1input" id="1customCheck3" name="1check" value="2">
            <label class="custom-control-label 1opt" id="1label3"  for="1customCheck3">
            <input type="text" class="form-control" id="1options3" placeholder="3)" name="1options">
            </label>
            <button onclick="removeOptions(1,3)" class="btn btn-primary 1butt">Удалить вариант</button>
        </div>

        <div id="1Id4" class="custom-control custom-checkbox mt-2" >
            <input type="checkbox" class="custom-control-input 1input" id="1customCheck4" name="1check" value="3">
            <label class="custom-control-label 1opt" id="1label4"  for="1customCheck4">
            <input type="text" class="form-control" id="1options4" placeholder="4)" name="1options">
            </label>
            <button onclick="removeOptions(1,4)" class="btn btn-primary 1butt">Удалить вариант</button>
        </div>

        </div>

        <div>
            <button onclick="addOptions(1)" class="btn btn-primary mt-2">Добавить вариант</button>
        </div>

<#--    <form method="get" action="/myquiz" >-->
    </div>
    <div id="addQuiz">
        <button onclick="addTest()" class="btn btn-primary mt-2">Добавить викторину</button>
    </div>


    <div id="addTest">
<#--    <form method="get" action="/" >-->
        <button onclick="addQuiz()" type="submit" class="btn btn-primary mt-2">Создать</button>
<#--    </form>-->
    </div>

</@c.page>