function changePass() {
    let username = $('input[name=username]').val();
    let oldPass = $('input[name=password]').val();
    //let xhrPost = new XMLHttpRequest();
    /*let jsonPost = {
        username: username,
        password: oldPass
    }*/

    console.log(username)

    let pass1 = $('input[name=password1]').val();
    let pass2 = $('input[name=password2]').val();
    /*let status = false

    xhrPost.open('POST', '/login');
    xhrPost.setRequestHeader('Content-type','application/x-www-form-urlencoded');
    xhrPost.onreadystatechange = function () {
        if(xhrPost.readyState === XMLHttpRequest.DONE && xhrPost.status === 200) {
            console.log("dsfasdf")
            status = true
            //console.log(xhrPost.)
        }
        if (status === true) {
            console.log("123")

        }
    };
    xhrPost.send(JSON.stringify(jsonPost));*/



    f(pass1,pass2)



}

function f(pass1, pass2) {
    let username = $('input[name=username]').val();
    if (pass1 === pass2) {
        let xhrPut = new XMLHttpRequest();
        let jsonPut = {
            username: username,
            password: pass1
        }



        xhrPut.open('PUT', '/pass');
        xhrPut.setRequestHeader('Content-type','application/json; charset=utf-8');
        xhrPut.onreadystatechange = function () {
            if(xhrPut.readyState === XMLHttpRequest.DONE && xhrPut.status === 200) {
                console.log("asdfg");
            }
        };
        xhrPut.send(JSON.stringify(jsonPut));
        console.log("Успех");
        document.getElementById("password1").style.background = 'MediumSpringGreen';
        document.getElementById("password2").style.background = 'MediumSpringGreen';
    } else {
        document.getElementById("password1").style.background = 'Salmon';
        document.getElementById("password2").style.background = 'Salmon';
        console.log("Ошибка");
    }
}