<#import "parts/common.ftl" as e>
<#include "parts/security.ftl">

<@e.page>

    <#macro logout>
    <#if user??>
        <form action="/logout" method="post">
            <button class="btn btn-primary" type = "submit">Выйти</button>
        </form>
    <#else>
        <form action="/login" method="get">
            <button class="btn btn-primary" type = "submit">Войти</button>
        </form>
    </#if>

    </#macro>

    <form action="/login" method="post">
        <div class="form-group">
            <label for="exampleInputEmail1">Логин</label>
            <input type="text" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp" name="username">
            <small id="emailHelp" class="form-text text-muted">We'll never share your email with anyone else.</small>
        </div>
        <div class="form-group">
            <label for="exampleInputPassword1">Пароль</label>
            <input type="text" class="form-control" id="exampleInputPassword1" name="password">
        </div>
        <div class="form-group form-check">
            <input type="checkbox" class="form-check-input" id="exampleCheck1">
            <label class="form-check-label" for="exampleCheck1">Запомнить меня</label>
        </div>
        <button type="submit" class="btn btn-primary"> Войти</button>
    </form>

</@e.page>