function activeChat(username) {
    console.log(username.id)
    location.href='/chat/' + username.id;
    //document.location.replace('catalog.html')

}