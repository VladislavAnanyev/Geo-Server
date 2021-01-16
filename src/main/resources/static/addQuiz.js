function addQuiz() {

    let arr = [];
    let arr2 = [];
    let options = [];
    

    let title = $('input[name=title]').val();
    let text = $('input[name=text]').val();
    arr = $('input[name=check]:checked');
    let opt = $('input[name=options]');

    for (i = 0; i < arr.length; i++) {
        arr2.push(arr[i].value)
    }

    for (i = 0; i < opt.length; i++) {
        options.push(opt[i].value)
    }


    const json = {
        title: title,
        text: text,
        options: options,
        answer: arr2
    }
    console.log("abcde")

    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/api/quizzes/',true);
    xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
    xhr.send(JSON.stringify(json))

    /*let xhr_2 = new XMLHttpRequest();
    xhr_2.open('GET', '/api/quizzes/',true);
    xhr_2.setRequestHeader('Content-type','application/json; charset=utf-8');
    xhr_2.send();*/

}