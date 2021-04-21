function activeChat(username) {
    console.log(username.id)
    location.href='/chat/' + username.id;

}