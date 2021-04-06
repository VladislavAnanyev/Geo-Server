function addTest() {
    let div = document.createElement("div");
    let count = document.getElementsByClassName("custom-control-label").length + 1;
    count = 1;

    let allTest = document.getElementsByName("title").length+1
    div.id = allTest + "Id";
    div.setAttribute('class', "quiz");
    //console.log(allTest.length);
    div.innerHTML = "<div class=\"form-group pt-5\">\n" +
        "        <label for=\"exampleFormControlInput" + allTest + "\">Название</label>\n" +
        "        <input type=\"text\" class=\"form-control\" id=\"titleID\" placeholder=\"Напишите здесь название викторины\" name=\"title\">\n" +
        "    </div>\n" +
        "\n" +
        "    <div class=\"form-group\">\n" +
        "        <label for=\"exampleFormControlInput1\">Вопрос</label>\n" +
        "        <input type=\"text\" class=\"form-control\" id=\"questionID\" placeholder=\"Напишите здесь ваш вопрос\"  name=\"text\">\n" +
        "    </div>\n" +
        "\n" +
        "    <div class=\"options\" id=\"" + allTest + "optionstest\">\n" +
        "        <label for=\"exampleFormControlInput1\">Варианты ответа</label>\n" +
        "    <div class=\"custom-control custom-checkbox\" id=" + allTest +"Id" + count + "\>\n" +
        "<input type=\"checkbox\" class=\"custom-control-input " + allTest + "input\" id=" + Number(allTest) + "customCheck" + Number(count) + "\ name=" + allTest + "check\ value=" + Number(count-1) + ">\n" +
        "        <label class=\"custom-control-label " + allTest + "opt\" id=" + allTest + "label" + count + "\ for=" + Number(allTest) + "customCheck" + Number(count) + "\>\n" +
        "        <input type=\"text\" class=\"form-control\" id=" + allTest + "options" + Number(count) + "\ placeholder=" + Number(count)  + ")\ name=" + allTest + "options\>\n" +
        "        </label> " +
        "        <button onclick=\"removeOptions(" + Number(allTest) + "," + Number(count) + ")\" class=\"btn btn-primary mt-3 " + allTest + "butt\">Удалить вариант</button>\n" +
        "\n" +
        "    </div>\n" +
        "\n" +
        "    <div class=\"custom-control custom-checkbox mt-2\" id=" + allTest +"Id" + (++count) + "\>\n" +
        "<input type=\"checkbox\" class=\"custom-control-input " + allTest + "input\" id=" + Number(allTest) + "customCheck" + Number(count) + "\ name=" + allTest + "check\ value=" + Number(count-1) + ">\n" +
        "        <label class=\"custom-control-label " + allTest + "opt\" id=" + allTest + "label" + count + "\  for=" + Number(allTest) + "customCheck" + Number(count) + "\>\n" +
        "        <input type=\"text\" class=\"form-control\" id=" + allTest + "options" + Number(count) + "\  placeholder=" + Number(count)  + ")\ name=" + allTest + "options\>\n" +
        "        </label> " +
        "        <button onclick=\"removeOptions(" + Number(allTest) + "," + Number(count) + ")\" class=\"btn btn-primary mt-3 " + allTest + "butt\">Удалить вариант</button>\n" +
        "    </div>\n" +
        "\n" +
        "    <div class=\"custom-control custom-checkbox\" id=" + allTest +"Id" + (++count) + "\>\n" +
        "<input type=\"checkbox\" class=\"custom-control-input " + allTest + "input\" id=" + Number(allTest) + "customCheck" + Number(count) + "\ name=" + allTest + "check\ value=" + Number(count-1) + ">\n" +
        "        <label class=\"custom-control-label " + allTest + "opt\" id=" + allTest + "label" + count + "\ for=" + Number(allTest) + "customCheck" + Number(count) + "\>\n" +
        "        <input type=\"text\" class=\"form-control\" id=" + allTest + "options" + Number(count) + "\ placeholder=" + Number(count)  + ")\ name=" + allTest + "options\>\n" +
        "        </label> " +
        "        <button onclick=\"removeOptions(" + Number(allTest) + "," + Number(count) + ")\" class=\"btn btn-primary mt-3 " + allTest + "butt\">Удалить вариант</button>\n" +
        "    </div>\n" +
        "\n" +
        "    <div class=\"custom-control custom-checkbox\" id=" + Number(allTest) + "Id" + Number(++count) + "\>\n" +
        "<input type=\"checkbox\" class=\"custom-control-input " + allTest + "input\" id=" + Number(allTest) + "customCheck" + Number(count) + "\ name=" + allTest + "check\ value=" + Number(count-1) + ">\n" +
        "        <label class=\"custom-control-label " + allTest + "opt\" id=" + allTest + "label" + count + "\ for=" + Number(allTest) + "customCheck" + Number(count) + "\>\n" +
        "        <input type=\"text\" class=\"form-control\" id=" + allTest + "options" + Number(count) + "\ placeholder=" + Number(count)  + ")\ name=" + allTest + "options\>\n" +
        "        </label> " +
        "        <button onclick=\"removeOptions(" + Number(allTest) + "," + Number(count) + ")\" class=\"btn btn-primary mt-3 " + allTest + "butt\">Удалить вариант</button>\n" +
        "    </div>\n" +
        "\n" +
        "    </div>\n" +
        "\n" +
        "    <div>\n" +
        "        <button onclick=\"addOptions(" + allTest + ")\" class=\"btn btn-primary mt-2\">Добавить вариант</button>\n" +
        "    </div>" +
        "<div id=\"addQuiz\">\n" +
        "        <button onclick=\"addTest()\" class=\"btn btn-primary mt-2\">Добавить викторину</button>\n" +
        "    </div>";
    let opt = document.getElementById("addTest");
    opt.before(div);
}


/*
<input type=\"checkbox\" class=\"custom-control-input\" id=\"customCheck" + count + "\" name=\"check\" value=" + count + ">\n" +
"        <label class=\"custom-control-label\" for=\"customCheck" + count + "\">\n" +
        "        <input type=\"text\" class=\"form-control\" id=\"options" + count + "\" placeholder=" + count  + ")\ name=\"options\">\n" +
        "        </label> " +
"        <button onclick=\"removeOptions(" + count + ")\" class=\"btn btn-primary mt-3\*/
