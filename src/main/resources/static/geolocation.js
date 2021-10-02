function geo() {
    //var findMeButton = document.getElementById("findme")


    let geoconfig={

        EnableHighAccuracy: true, maximumAge: 60000

    };
// Check if the browser has support for the Geolocation API
    if (!navigator.geolocation) {


        console.log("Браузер не поддерживается")
        //findMeButton.addClass("disabled");
        //$('.no-browser-support').addClass("visible");

    } else {

            /*navigator.geolocation.getCurrentPosition(function (position) {

                // Get the coordinates of the current possition.
                let lat = position.coords.latitude;
                let lng = position.coords.longitude;


                $('.latitude').text(lat);
                $('.longitude').text(lng);
                $('.coordinates').addClass('visible');

                // Create a new map and place a marker at the device location.
                var map = new GMaps({
                    el: '#map',
                    lat: lat,
                    lng: lng
                });

                map.addMarker({
                    lat: lat,
                    lng: lng
                });



                let json = {
                    lat: lat,
                    lng: lng
                }

                let xhr = new XMLHttpRequest();
                xhr.open('POST', '/sendGeolocation',true);
                xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {

                        let array = JSON.parse(xhr.response)
                        console.log(array)
                        for (let i = 0; i < xhr.response.length; i++) {
                            
                            map.addMarker({
                                lat: array[i].lat,
                                lng: array[i].lng
                            });
                        }


                    } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {

                    }
                };
                xhr.send(JSON.stringify(json))

            }, null, geoconfig);*/


        var map
        var m
        navigator.geolocation.getCurrentPosition(function (position) {

            // Get the coordinates of the current possition.
            let lat = position.coords.latitude;
            let lng = position.coords.longitude;

            if (document.getElementById("map") != null) {
                map = new GMaps({
                    el: '#map',
                    lat: lat,
                    lng: lng
                });


                m = map.addMarker({
                    lat: lat,
                    lng: lng
                });


                let json = {
                    lat: lat,
                    lng: lng
                }


                let xhr = new XMLHttpRequest();
                xhr.open('GET', '/getAllGeoWithoutMe',true);
                xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {

                        let array = JSON.parse(xhr.response)
                        console.log(array)
                        for (let i = 0; i < array.length; i++) {

                           // console.log()
                            map.addMarker({
                                lat: array[i].lat,
                                lng: array[i].lng
                            });

                        }


                    } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {

                    }
                };
                xhr.send()
            }







        }, null, geoconfig);



        navigator.geolocation.watchPosition(position => {
            console.log(position)
            let lat = position.coords.latitude;
            let lng = position.coords.longitude;

            $('.latitude').text(lat);
            $('.longitude').text(lng);
            $('.coordinates').addClass('visible');

            // Create a new map and place a marker at the device location.
            /*var map = new GMaps({
                el: '#map',
                lat: lat,
                lng: lng
            });*/

            if (document.getElementById("map")) {
                map.removeMarker(m)

                m = map.addMarker({
                    lat: lat,
                    lng: lng
                });
            }




            let json = {
                lat: lat,
                lng: lng
            }

            let xhr = new XMLHttpRequest();
            xhr.open('POST', '/sendGeolocation',true);
            xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
            xhr.onreadystatechange = function () {
                if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {

                    //let array = JSON.parse(xhr.response)
                    //console.log(array)

                    /*for (let i = 0; i < xhr.response.length; i++) {

                        map.addMarker({
                            lat: array[i].lat,
                            lng: array[i].lng
                        });

                    }
*/

                } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {

                }
            };
            xhr.send(JSON.stringify(json))



        }, null, geoconfig)





    }
}