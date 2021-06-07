<#import "parts/common.ftl" as c>
<@c.page>
    <title>Добавить викторину</title>
    <script src="/static/addQuiz.js"></script>
    <script src="/static/addOptions.js"></script>
    <script src="/static/addTest.js"></script>

    <div>
        <div class="form-group">
            <label for="exampleFormControlInput1">Название</label>
            <input type="text" class="form-control" id="description" placeholder="Напишите здесь название викторины" name="description">
        </div>
    </div>

    <div id="1Id" class="quiz">
        <div class="form-group">
            <label for="exampleFormControlInput1">Тема</label>
            <input type="text" class="form-control" id="titleID" placeholder="Напишите здесь тему вопроса из викторины" name="title">
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
<#--            <button onclick="removeOptions(1,1)" class="btn btn-primary 1butt">Удалить вариант</button>-->
            <a href="#" style="color: black"  data-bs-toggle="tooltip" data-bs-placement="right" title="Удалить вариант">
            <svg  onclick="removeOptions(1,1)" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-archive-fill ml-2 1butt" viewBox="0 0 16 16">
                <path d="M12.643 15C13.979 15 15 13.845 15 12.5V5H1v7.5C1 13.845 2.021 15 3.357 15h9.286zM5.5 7h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1 0-1zM.8 1a.8.8 0 0 0-.8.8V3a.8.8 0 0 0 .8.8h14.4A.8.8 0 0 0 16 3V1.8a.8.8 0 0 0-.8-.8H.8z"/>
            </svg>
            </a>

        </div>

        <div class="custom-control custom-checkbox mt-2" id="1Id2" >
            <input type="checkbox" class="custom-control-input 1input" id="1customCheck2" name="1check" value="1">
            <label class="custom-control-label 1opt"  id="1label2"  for="1customCheck2">
                <input type="text" class="form-control" id="1options2" placeholder="2)" name="1options">
            </label>
<#--            <button onclick="removeOptions(1,2)" class="btn btn-primary 1butt">Удалить вариант</button>-->
            <a href="#" style="color: black"  data-bs-toggle="tooltip" data-bs-placement="right" title="Удалить вариант">
            <svg  onclick="removeOptions(1,2)" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-archive-fill ml-2 1butt" viewBox="0 0 16 16">
                <path d="M12.643 15C13.979 15 15 13.845 15 12.5V5H1v7.5C1 13.845 2.021 15 3.357 15h9.286zM5.5 7h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1 0-1zM.8 1a.8.8 0 0 0-.8.8V3a.8.8 0 0 0 .8.8h14.4A.8.8 0 0 0 16 3V1.8a.8.8 0 0 0-.8-.8H.8z"/>
            </svg>
            </a>
        </div>

        <div class="custom-control custom-checkbox mt-2" id="1Id3">
            <input type="checkbox" class="custom-control-input 1input" id="1customCheck3" name="1check" value="2">
            <label class="custom-control-label 1opt" id="1label3"  for="1customCheck3">
            <input type="text" class="form-control" id="1options3" placeholder="3)" name="1options">
            </label>
<#--            <button onclick="removeOptions(1,3)" class="btn btn-primary 1butt">Удалить вариант</button>-->
            <a href="#" style="color: black"  data-bs-toggle="tooltip" data-bs-placement="right" title="Удалить вариант">
            <svg  onclick="removeOptions(1,3)" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-archive-fill ml-2 1butt" viewBox="0 0 16 16">
                <path d="M12.643 15C13.979 15 15 13.845 15 12.5V5H1v7.5C1 13.845 2.021 15 3.357 15h9.286zM5.5 7h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1 0-1zM.8 1a.8.8 0 0 0-.8.8V3a.8.8 0 0 0 .8.8h14.4A.8.8 0 0 0 16 3V1.8a.8.8 0 0 0-.8-.8H.8z"/>
            </svg>
            </a>
        </div>

        <div id="1Id4" class="custom-control custom-checkbox mt-2" >
            <input type="checkbox" class="custom-control-input 1input" id="1customCheck4" name="1check" value="3">
            <label class="custom-control-label 1opt" id="1label4"  for="1customCheck4">
            <input type="text" class="form-control" id="1options4" placeholder="4)" name="1options">
            </label>


                <a href="#" style="color: black"  data-bs-toggle="tooltip" data-bs-placement="right" title="Удалить вариант">
                    <svg onclick="removeOptions(1,4)"  xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-archive-fill ml-2 1butt" viewBox="0 0 16 16">
                        <path d="M12.643 15C13.979 15 15 13.845 15 12.5V5H1v7.5C1 13.845 2.021 15 3.357 15h9.286zM5.5 7h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1 0-1zM.8 1a.8.8 0 0 0-.8.8V3a.8.8 0 0 0 .8.8h14.4A.8.8 0 0 0 16 3V1.8a.8.8 0 0 0-.8-.8H.8z"/>
                    </svg>
                </a>





<#--            <button onclick="removeOptions(1,4)" class="btn btn-primary 1butt">Удалить вариант</button>-->

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

    <#--<div>Время на выполнение
    <div class="form-check ml-4">
        <input class="form-check-input" type="radio" name="flexRadioDefault" id="flexRadioDefault1" checked>
        <label class="form-check-label" for="flexRadioDefault1">
            Неограничено
        </label>
    </div>
    <div class="form-check ml-4">
        <input class="form-check-input" type="radio" name="flexRadioDefault" id="flexRadioDefault2">
        <label class="form-check-label" for="flexRadioDefault2">
            Ограничено
        </label>
        <label for="time"> (чч:мм)</label>
        <input type="time" value="01:00" id="time">
    </div>
        <div>

        </div>
    </div>-->



    <div id="addTest">
<#--    <form method="get" action="/" >-->
        <button onclick="addQuiz()" type="submit" class="btn btn-primary mb-2">Создать</button>
<#--    </form>-->
    </div>

</@c.page>