<#import "parts/common.ftl" as e>
<@e.page>

<#--<form method="post" action="/api/quizzes/${id}/solve" class="form-inline">

    <div><label> Ответ : <input type="text" name="answer"/> </label></div>
    <button type="submit" class="btn btn-primary ml-2">Ответить</button>
</form>-->

    <script src="/static/answer.js"></script>
    <script src="/static/answer.js"></script>
    <link href='https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css'>
    <script src='https://stackpath.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.bundle.min.js'></script>
    <script src='https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js'></script>
<#--    <script src='https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.4/Chart.bundle.min.js'></script>-->
<#--    <link rel="stylesheet" href="../static/chart.css">-->

    <div>
    <#list test as quiz>
    <div class = "card quiz mt-2 card mb-3 shadow p-3 mb-5 bg-white rounded">
        <b class="mr-4">${quiz_index + 1}.</b>
        <div class="card-body">
        <div id="test${quiz_index}">
        <div><b>${quiz.title}</b></div>
        <div>${quiz.text}</div>
        <#list quiz.options as options>
        <div class="custom-control custom-checkbox mt-2">
            <input type="checkbox" class="custom-control-input answers${options_index}" id="${options_index}_${quiz.id}" name="check${quiz?index}" value="${options_index}">
            <label class="custom-control-label" for="${options_index}_${quiz.id}">
                ${options}
            </label>
        </div>
        </#list>
        </div>
        </div>


    </div>
    </#list>

    <button type="submit" data-toggle="modal" data-target="#staticBackdrop"
            class="btn btn-primary ml-2 mb-2" id="btnAns" onclick="f(${test_id.id?c})">Ответить</button>



    <div onload="chartpie(${test_id.id?c})" class="modal fade bd-example-modal-lg" id="staticBackdrop" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="staticBackdropLabel">Ваши результаты</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <link rel="stylesheet" href="../static/chart.css">

                    <script src='https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.4/Chart.bundle.min.js'></script>
                    <script>




                    </script>
                    <div class="page-content page-container" id="page-content">
                        <div class="padding">
                            <div class="row">
                                <div class="container-fluid d-flex justify-content-center">
                                    <div class="col-sm-8 col-md-6">
                                        <div class="card justify-content-center" >
<#--                                            <div class="card-header">Диаграмма</div>-->
                                            <div class="card-body" style="height: 420px">
                                                <div class="chartjs-size-monitor" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px; overflow: hidden; pointer-events: none; visibility: hidden; z-index: -1;">
                                                    <div class="chartjs-size-monitor-expand" style="position:absolute;left:0;top:0;right:0;bottom:0;overflow:hidden;pointer-events:none;visibility:hidden;z-index:-1;">
                                                        <div style="position:absolute;width:1000000px;height:1000000px;left:0;top:0"></div>
                                                    </div>
                                                    <div class="chartjs-size-monitor-shrink" style="position:absolute;left:0;top:0;right:0;bottom:0;overflow:hidden;pointer-events:none;visibility:hidden;z-index:-1;">
                                                        <div style="position:absolute;width:200%;height:200%;left:0; top:0"></div>
                                                    </div>
                                                </div> <canvas id="chart-line" width="299" height="200" class="chartjs-render-monitor" style="display: block; width: 299px; height: 200px;"></canvas>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
<#--                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Назад</button>-->
                    <button type="button" onclick="sendEmailMessage()" data-dismiss="modal" class="btn btn-primary">Понятно</button>
                </div>

            </div>
        </div>


    </div>
    </div>



</@e.page>