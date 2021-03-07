
<#import "parts/common.ftl" as e>

<@e.page>
<div xmlns="">
    <#list test as testList>
        <div class="card my-3">
            <div class="m-2">
                <span>${testList.user.username}</span>
            </div>
            <div class="card-footer text-muted">
                <i>${testList.id?c}</i>
            </div>
<#--            <div class="card-footer text-muted">-->
<#--                <#list quiz.options as options>-->
<#--                ${options}-->
<#--                </#list>-->
<#--            </div>-->
            <#--<div class="card-footer text-muted">
                <a href="/about/${testList.user.username}">${testList.user.username}</a>
            </div>-->
            <form method="get" action="" class="form-inline">

                <button type="submit" class="btn btn-primary ml-2">Ответить</button>
            </form>
        </div>
    <#else>
        No message
    </#list>


    <script src="/static/page.js"></script>
    <script src="/static/pageSize.js"></script>

    <form>
    <nav aria-label="Page navigation example">
        <ul class="pagination">
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
                <div class="form-row align-items-center ml-3">
                    <div class="col-auto my-1">
                        <label class="mr-sm-2 sr-only" for="inlineFormCustomSelect">Preference</label>
                        <select class="custom-select " onclick="paging()" name = "size" id="inlineFormCustomSelect">
                            <option value="no" selected>no</option>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="10">10</option>
                        </select>
                    </div>
                </div>
            </li>
        </ul>

    </nav>
    </form>

</div>
</@e.page>