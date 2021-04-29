<#import "parts/common.ftl" as e>
<@e.page>
    <script src="/static/changePassword.js"></script>
    <!-- Button trigger modal -->
    <button type="button" class="btn btn-primary ml-2 my-1" data-toggle="modal" data-target="#staticBackdrop">
        Изменить пароль
    </button>

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
                    <div class="form-group">

                        <#--                        <input type="text" class="form-control" id="oldpassword" placeholder="Придумайте новый пароль"  aria-describedby="emailHelp" name="password">-->
                        <input type="text" class="form-control" id="password1" placeholder="Придумайте новый пароль"  aria-describedby="emailHelp" name="password1">
                        <input type="text" class="form-control mt-3" id="password2" placeholder="Введите новый пароль ещё раз" aria-describedby="emailHelp" name="password2">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Назад</button>
                    <button type="button" onclick="changePass()" class="btn btn-primary">Да</button>
                </div>

            </div>
        </div>
    </div>
</@e.page>