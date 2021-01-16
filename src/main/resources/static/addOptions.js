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

function removeOptions(id) {
    let name = id + "Id";
    let div = document.getElementById(name);
    let check = document.getElementsByClassName("custom-control-label");
    div.remove();
    let check2 = document.getElementsByClassName("custom-control-label");
    let values = document.getElementsByClassName("custom-control-input");
    let check3 = document.getElementsByName("options");
    let button = document.getElementsByClassName("btn btn-primary mt-3");
    console.log(button.length);
    let sum;
    for (i = 0; i < check2.length; i++) {
        check3.item(i).id = "options" + (i + 1);
        sum = i + 1;
        button.item(i).setAttribute('onclick',"removeOptions(" + sum + ")");
        values.item(i).value = i;
    }

    while (id <= check.length) {
        let next = id + 1;
        let text = document.getElementById("options" + id);
        let optionsID = document.getElementById(next + "Id")
        optionsID.id = id + "Id";
        text.placeholder = id + ")";
        id++;
    }
}