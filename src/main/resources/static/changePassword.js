function changePass() {
    let pass1 = $('input[name=password1]').val();
    let pass2 = $('input[name=password2]').val();
    if (pass1 === pass2) {
        let xhr = new XMLHttpRequest();
        let json = {
            password: pass1
        }

        xhr.open('PUT', '/update/userinfo/password');
        xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
        xhr.onreadystatechange = function () {
            if(xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
                console.log("asdfg");
            }
        };
        xhr.send(JSON.stringify(json));
        console.log("Успех");
        document.getElementById("password1").style.background = 'MediumSpringGreen';
        document.getElementById("password2").style.background = 'MediumSpringGreen';
    } else {
        document.getElementById("password1").style.background = 'Salmon';
        document.getElementById("password2").style.background = 'Salmon';
        console.log("Ошибка");
    }


}