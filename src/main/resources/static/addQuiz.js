function addQuiz() {




    let quizzes_mas = [];
    let name = document.getElementById("description");
    let countOfQuiz = document.getElementsByClassName("quiz").length;
    let title = document.getElementsByName("title");
    let text = document.getElementsByName("text");

    /*let time = document.getElementById("time")

    if (!document.getElementById("flexRadioDefault2").checked) {
        time = null
    }*/


    //console.log(time)


    for (let i = 0; i < countOfQuiz; i++) {
        let options = document.getElementsByName(String(Number(i+1) + "options"));
        let opt_values = [];
        let answer_values = [];
        for (let j = 0; j < options.length; j++) {

            opt_values.push(options[j].value)
        }
        let name = Number(i + 1) + "check";

        //arr = $('input[name=' + name + ']:checked');

        let answers = document.getElementsByName(name);
        console.log(answers.length)

        for (let i = 0; i < answers.length; i++) {
            if(answers[i].checked) {
                console.log(answers[i].value)
                answer_values.push(answers[i].value)
            }
        }




        let quiz = {
            title: title[i].value,
            text: text[i].value,
            options:opt_values,
            answer: answer_values
        }
        quizzes_mas.push(quiz)
    }

    //let title = $('input[name=title]').val();
    //let text = $('input[name=text]').val();

    /*let opt = $('input[name=options]');

    for (i = 0; i < arr.length; i++) {
        arr2.push(arr[i].value)
    }*/



    // let quizzes = {
    //         title: title,
    //         text: text,
    //         options: options,
    //         answer: arr2
    //     }

    // quizzes_mas.push(quiz)

    let json;

    /*if (time != null) {


        json = {
            description: name.value,
            quizzes: quizzes_mas,
            //duration: time.value
        }
    } else {*/
    json = {
        description: name.value,
        quizzes: quizzes_mas,
    }
    //}
    //console.log("abcde")

    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/api/quizzes/',true);
    xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
    xhr.send(JSON.stringify(json))

    /*let xhr_2 = new XMLHttpRequest();
    xhr_2.open('GET', '/api/quizzes/',true);
    xhr_2.setRequestHeader('Content-type','application/json; charset=utf-8');
    xhr_2.send();*/

}