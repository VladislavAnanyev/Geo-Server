<#import "parts/common.ftl" as e>
<#include "parts/security.ftl">

<@e.page>

    <#macro logout>
    <#if nowUser??>
        <form action="/logout" method="post">
            <button class="btn btn-primary" type = "submit">Выйти</button>
        </form>
    <#else>
        <form action="/signin" method="get">
            <button class="btn btn-primary" type = "submit">Войти</button>
        </form>
    </#if>

    </#macro>

    <div>
        <a href = "/oauth2/authorization/google">Click here to Google Login</a>
    </div>

    <div>
        <a href = "/oauth2/authorization/github">Click here to GitHub Login</a>
    </div>

    <form action="/signin" method="post">
        <div class="form-group">
            <label for="exampleInputEmail1">Логин</label>
            <input type="text" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp" name="username">
            <small id="emailHelp" class="form-text text-muted">Мы никогда никому не передадим ваши данные</small>
        </div>
        <div class="form-group">
            <label for="exampleInputPassword1">Пароль</label>
            <input type="password" class="form-control" id="exampleInputPassword1" name="password">
        </div>
        <div class="form-group form-check">
            <input type="checkbox" class="form-check-input" id="exampleCheck1">
            <label class="form-check-label" for="exampleCheck1">Запомнить меня</label>
        </div>
        <button type="submit" class="btn btn-primary">Войти</button>
        <button disabled type="button" class="btn btn-primary ml-2 my-1" data-toggle="modal" data-target="#staticBackdrop">
            Забыли пароль?
        </button>
    </form>



    <div class="modal fade" id="staticBackdrop" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="staticBackdropLabel">Изменение пароля</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="form-group"> Вы действительно хотите изменить пароль?

                    <#--                        <input type="text" class="form-control" id="oldpassword" placeholder="Придумайте новый пароль"  aria-describedby="emailHelp" name="password">-->
                    <#--<input type="text" class="form-control" id="password1" placeholder="Придумайте новый пароль"  aria-describedby="emailHelp" name="password1">
                    <input type="text" class="form-control mt-3" id="password2" placeholder="Введите новый пароль ещё раз" aria-describedby="emailHelp" name="password2">-->
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Назад</button>
                <button type="button" onclick="sendEmailMessage()" data-dismiss="modal" class="btn btn-primary">Да</button>
            </div>

        </div>
    </div>

</@e.page>