
function deleteFriend(id) {

    console.log(id)
    let xhr = new XMLHttpRequest();
    let div = document.getElementById(id);
    xhr.open('DELETE', '/friend/' + id);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            setTimeout(() => $(div).slideUp('slow', function () {
                    //button.disabled = false;
                    $(this).remove();
                    //button.disabled = false;

                }
            ), 0);

       }
    }
    xhr.send();




    //div.remove();

}

function deleteTempQuiz(id) {
    let div = document.getElementById(id + "Id");
    setTimeout(() => $(div).slideUp('slow', function () {
            //button.disabled = false;
            $(this).remove();
            //button.disabled = false;

        }
    ), 0);
}
