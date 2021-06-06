
<#import "parts/common.ftl" as e>

<@e.page>
<div xmlns="">
    <title>Все викторины</title>
    <link href='https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css'>
    <script src='https://stackpath.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.bundle.min.js'></script>
    <script src='https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js'></script>




    <#list test as testList>

        <#--<div class="card w-75">
            <div class="card-body">
                <h5 class="card-title">${testList.description}</h5>
                <p class="card-text">Количество вопросов: </p>
                <a href="#" class="btn btn-primary">Кнопка</a>
            </div>
        </div>-->

<#--        <div class="card my-3">-->
            <#--<div class="m-2">
                <span><a href="/about/${testList.user.username}">${testList.user.username}</a></span>
            </div>
            <div class="card-footer text-muted">
                <i>${testList.description}</i>
            </div>-->

            <div class="card mb-3 shadow p-3 mb-5 bg-white rounded">
<#--                <img src="/../../../../img/look.com.ua_2016.02-111-1920x1080/${img[testList?index].name}" height="100px" class="card-img-top" alt="...">-->
                <div class="card-body">
                    <h5 class="card-title">${testList.description}</h5>
                    <p class="card-text">Количество вопросов: ${testList.quizzes?size}</p>
                    <p class="card-text" ><small  class="text-muted">Автор: <a href="/about/${testList.user.username}">${testList.user.username}</a></small></p>
                </div>
                <form method="get" action="/api/quizzes/${testList.id?c}/solve/" class="form-inline">

                    <button type="submit" class="btn btn-primary ml-3 mb-3">Приступить к выполнению</button>
                </form>
            </div>

<#--            <div class="card-footer text-muted">-->
<#--                <#list quiz.options as options>-->
<#--                ${options}-->
<#--                </#list>-->
<#--            </div>-->
            <#--<div class="card-footer text-muted">
                <a href="/about/${testList.user.username}">${testList.user.username}</a>
            </div>-->

<#--        </div>-->
    <#else>
        Здесь пока ничего нет

    </#list>


    <script src="/static/page.js"></script>
    <script src="/static/pageSize.js"></script>

    <div class="row">

        <#--<div class="col">
            <div class="form-row align-items-center ml-3">Размер:
                <div class="col-auto my-1 ">
                    <label class="mr-sm-2 sr-only" for="inlineFormCustomSelect">Preference</label>
                    <select class="custom-select"  name = "size" id="inlineFormCustomSelect">
                        <option onclick="paging()" value="no" selected>Все</option>
                        <option onclick="paging()" value="1">1</option>
                        <option onclick="paging()" value="5">5</option>
                        <option onclick="paging()" value="10">10</option>
                    </select>
                </div>
            </div>
        </div>-->

        <div class="col">
    <nav aria-label="Page navigation example" >
        <ul class="pagination justify-content-end">
            <li class="page-item">
                <a class="page-link"  aria-label="Previous">
                    <span aria-hidden="true" onclick="pagePrev()">&laquo;</span>
                </a>
            </li>
            <li id="first" class="page-item"><a class="page-link" onclick="pageOne(0)">1</a></li>
            <li id="second" class="page-item"><a class="page-link" onclick="pageTwo(1)">2</a></li>
            <li id="third" class="page-item"><a class="page-link" onclick="pageThree(2)">3</a></li>
            <li class="page-item">
                <a class="page-link" aria-label="Next">
                    <span onclick="pageNext()" aria-hidden="true">&raquo;</span>
                </a>
            </li>
            <li>

            </li>
        </ul>


    </nav>
        </div>

    </div>


</div>



</@e.page>