function sendEmailMessage() {
    let xhr = new XMLHttpRequest();
    let url = new URL(document.location.href);
    xhr.open('POST', '/update/userinfo/password');
    xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
    xhr.onreadystatechange = function () {
        if(xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            console.log("asdfg");
        }
    };
    xhr.send(url.host);
}