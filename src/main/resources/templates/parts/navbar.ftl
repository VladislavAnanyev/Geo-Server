<#include "security.ftl">
<#import "../login.ftl" as e>

<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="/">Quizzes</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item">
                <a class="nav-link" href="/reg">Регистрация</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/api/quizzes">Все викторины</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="">Завершённые</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/add">Добавить викторину</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/myquiz">Мои викторины</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/chat">Сообщения</a>
            </li>
        </ul>

        <div class="dropdown">
            <a class="btn btn-secondary dropdown-toggle mr-3" href="#" role="button" id="dropdownMenuLink autoUser" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                ${name}
            </a>

            <div class="dropdown-menu" aria-labelledby="dropdownMenuLink">
                <a class="dropdown-item" href="/profile">Профиль</a>
                <a class="dropdown-item" href="#">Another action</a>
                <a class="dropdown-item" href="#">Something else here</a>
            </div>
        </div>
<#--        <div class = "navbar-text mr-3">${name}</div>-->
        <@e.logout />

    </div>
</nav>