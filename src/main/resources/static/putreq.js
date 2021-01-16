function updateQuiz(id) {

    let arr2 = [];
    let options2 = [];

    let xhr = new XMLHttpRequest();
    let title_1 = document.getElementById("exampleFormControlInput1")
    let answer = $('input[name=check]:checked');
    let title = $('input[name=title]').val();
    let text = $('input[name=text]').val();
    let opt = $('input[name=options]');

    for (i = 0; i < answer.length; i++) {
        arr2.push(answer[i].value)
    }

    for (i = 0; i < opt.length; i++) {
        options2.push(opt[i].value)
    }

    const json = {
        title: title_1.value,
        text: text,
        options: options2,
       // options: options2,
        answer: arr2
    }

    console.log(JSON.stringify(json));
    xhr.open('PUT', '/update/' + id);
    xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
    xhr.send(JSON.stringify(json));

}