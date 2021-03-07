function addOptions() {
    let div = document.createElement("div");
    let count = document.getElementsByClassName("custom-control-label").length + 1;
    div.className = "custom-control custom-checkbox mt-2";
    div.id = count + "Id";
    div.innerHTML = "<input type=\"checkbox\" class=\"custom-control-input\" id=\"customCheck" + count + "\" name=\"check\" value=" + count + ">\n" +
        "        <label class=\"custom-control-label\" for=\"customCheck" + count + "\">\n" +
        "        <input type=\"text\" class=\"form-control\" id=\"options" + count + "\" placeholder=" + count  + ")\ name=\"options\">\n" +
        "        </label> " +
        "        <button onclick=\"removeOptions(" + count + ")\" class=\"btn btn-primary mt-3\">Удалить вариант</button>";
    let opt = document.getElementById("optionstest");
    opt.append(div);
}

function removeOptions(numTest, id) {
    let name = numTest + "Id" + id;
    //console.log(name);
    //let name = id + "Id";
    let div = document.getElementById(name);
    //console.log(div.id)
    //let check = document.getElementsByClassName("custom-control-label");
    let check = document.getElementsByClassName(numTest + "opt");
    div.remove();
    let check2 = document.getElementsByClassName(numTest + "opt");
    let values = document.getElementsByClassName(numTest + "input");
    let check3 = document.getElementsByName(numTest + "options");
    let button = document.getElementsByClassName(numTest + "butt");

    //console.log(button.length);
    let sum;
    for (i = 0; i < check2.length; i++) {
        check3.item(i).id = numTest + "options" + (i + 1);
        sum = i + 1;
        button.item(i).setAttribute('onclick',"removeOptions(" + numTest +"," + sum + ")");
        values.item(i).value = i;

    }

    while (id <= check.length) {
        let next = id + 1;
        let text = document.getElementById(numTest + "options" + id);
        let optionsID = document.getElementById( numTest + "Id" + next)
        let customCheck = document.getElementById(numTest + "customCheck" + next);
        let label = document.getElementById(numTest + "label" + next);
        //console.log(numTest + "Id" + next);

        optionsID.id = numTest + "Id" + id;



        text.placeholder = id + ")";
        customCheck.id = numTest + "customCheck" + id;
        label.id = numTest + "label" + id;
        //label.for = numTest + "customCheck" + id;
        label.setAttribute('for', numTest + "customCheck" + id);
        id++;
    }
}