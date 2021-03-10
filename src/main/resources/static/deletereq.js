
function deleteQuiz(id) {
    let xhr = new XMLHttpRequest();
    console.log('/api/quizzes/' + id);
    xhr.open('DELETE', '/api/quizzes/' + id);
    xhr.send();

    let div = document.getElementById(id);
    div.remove();

}
