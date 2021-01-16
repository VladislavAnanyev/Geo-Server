function f(id) {
    let arr2 = [];
    let answer = $('input[name=check]:checked')

    for (i = 0; i < answer.length; i++) {
        arr2.push(answer[i].value)
    }

    const json = {
        answer: arr2
    }

    let style = document.getElementById("test").style;
    style.padding = '20px';
    style.borderRadius = '10px';
    style.opacity = '0.9';

    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/api/quizzes/' + id + '/solve/',true);
    xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
    xhr.onreadystatechange = function () {
        if(xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            if (xhr.responseText === "true") {
                style.background = 'MediumSpringGreen';
            } else {
                style.background = 'Salmon';
            }
        }
    };
    xhr.send(JSON.stringify(json));
}


