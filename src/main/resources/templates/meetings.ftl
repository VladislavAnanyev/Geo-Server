<#import "parts/common.ftl" as e>

<@e.page>
    <script src="/static/newActiveDialog.js"></script>
<#--    <script src="/static/meetgeo.js"></script>-->
    <script src='//cdn.jsdelivr.net/gmaps4rails/2.1.2/gmaps4rails.js'> </script>
    <script src='//cdnjs.cloudflare.com/ajax/libs/underscore.js/1.8.3/underscore.js'> </script>
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAWyCb1Xq7gDRWSWRnOAVF3VsBz9TQW-og"></script>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/gmaps.js/0.4.24/gmaps.min.js"></script>
    <link rel="stylesheet" href="/static/geoloc.css">
    <h5 class="mb-3" id="example">Возможно, вы сегодня видели:<a class="anchorjs-link" aria-label="Anchor" data-anchorjs-icon="#" href="#example" style="padding-left: 0.375em;"></a></h5>


    <div class="row">
    <#list meetings as meeting>
        <div class="col-sm-6">
        <#if meeting.secondUser.username != myUsername>
    <div class="card mt-2" style="width: 18rem;">
        <img class="rounded-circle" src="${meeting.secondUser.avatar}" alt="...">
        <div class="card-body">
            <h5 class="card-title">${meeting.secondUser.firstName} ${meeting.secondUser.lastName}</h5>
            <p class="card-text">${meeting.time?datetime?string["dd.MM.yyyy HH:mm:ss"]}</p>
            <a onclick="writeMsg('${meeting.secondUser.username}')" href="../chat/${meeting.secondUser.username}" class="btn btn-primary">Написать сообщение</a>

        </div>




            <#else>

                <div class="card" style="width: 18rem;">
                    <#if meeting.firstUser.avatar?contains("http")><img class="rounded-circle" src="${meeting.firstUser.avatar}" <#else> <img class="rounded-circle" src="../../../../img/${meeting.firstUser.avatar}.jpg" </#if> alt="...">
                    <div class="card-body">
                        <h5 class="card-title">${meeting.firstUser.firstName} ${meeting.firstUser.lastName}</h5>
                        <p class="card-text">${meeting.time?datetime?string["dd.MM.yyyy HH:mm:ss"]}</p>
                        <a onclick="writeMsg('${meeting.firstUser.username}')" <#--href="../chat/${meeting.firstUser.username}"--> class="btn btn-primary">Написать сообщение</a>
                    </div>
<#--                </div>-->

        </#if>
        <ul class="list-group list-group-flush">
            <li class="list-group-item">
                <div class="map-overlay">
                    <div class="map" id="map${meeting_index}"></div>
                </div>
            </li>


        </ul>



    </div>



    </div>
    </#list>
    </div>

        <script>

            // Check if the browser has support for the Geolocation API



            <#list meetings as mt>
            new GMaps({
                el: '#map${mt_index}',
                lat: ${mt.lat?replace(",",".")},
                lng: ${mt.lng?replace(",",".")}
            }).addMarker({
                lat: ${mt.lat?replace(",",".")},
                lng: ${mt.lng?replace(",",".")}
            });
            </#list>




            /*var map,map2;

            var myOptions = {
                zoom: 6,
                center: new google.maps.LatLng(51, -1),
                mapTypeId: google.maps.MapTypeId.ROADMAP
            }

            var myOptions2 = {
                zoom: 4,
                center: new google.maps.LatLng(53, -2),
                mapTypeId: google.maps.MapTypeId.SATELLITE
            }

            map = new google.maps.Map(document.getElementById("map_canvas"),
                myOptions);

            map2 = new google.maps.Map(document.getElementById("map_canvas2"),
                myOptions2);*/



        </script>


</@e.page>