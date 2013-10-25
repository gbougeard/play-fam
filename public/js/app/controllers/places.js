'use strict';


fam.controller('PlacesCtrl', function ($scope, notificationService, placeService ) {
    $scope.marker = {};
    $scope.mapOptions = {
        //center: new google.maps.LatLng(35.784, -78.670),
        center: new google.maps.LatLng(43.602521593464054, 1.441223145229742), // Toulouse
        zoom: 12,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    var geocoder = new google.maps.Geocoder();
//    var map;

//    $scope.initialize = function () {
//        geocoder = new google.maps.Geocoder();
//        var latlng = new google.maps.LatLng(-34.397, 150.644);
//        var mapOptions = {
//            zoom: 8,
//            center: latlng,
//            mapTypeId: google.maps.MapTypeId.ROADMAP
//        }
//        map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
//    }

    $scope.geocode = function () {
        $scope.address = $scope.place.address + " , " + $scope.place.zipcode + " " + $scope.place.city;
        console.log("geocode", $scope.place, $scope.address);
//        var address = document.getElementById('address').value;
        geocoder.geocode({ 'address': $scope.address}, function (results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
//                map.setCenter(results[0].geometry.location);
                var marker = new google.maps.Marker({
                    map: $scope.myMap,
                    position: results[0].geometry.location
                });
                console.log("result", results[0].geometry.location, $scope.marker);
                $scope.marker = results[0].geometry.location;
                console.log("before", $scope.place);
                $scope.place.latitude = marker.getPosition().lat();
                $scope.place.longitude = marker.getPosition().lng();
                console.log("after", $scope.place);
                $scope.myMap.panTo($scope.marker);
//            .panTo($scope.marker);
//                $scope.myMarkers.push(marker);
                $scope.$digest();
            } else {
                alert('Geocode was not successful for the following reason: ' + status);
            }
        });
    }

    $scope.addMarker = function ($event) {
        var marker = new google.maps.Marker({
            map: $scope.myMap,
            position: $event.latLng
        });
        $scope.marker = $event.latLng;
        console.log("addMarker", $scope.marker, $event);
//        $scope.$digest();
    };

    $scope.loadPlace = function (id) {
        placeService.getPlace(id)
            .success(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                $scope.place = data;
                $scope.address = $scope.place.address + " , " + $scope.place.zipcode + " " + $scope.place.city;

                if ($scope.place.city != "") {
                    $scope.geocode();
                }
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
    };

    $scope.update = function (id, place) {
        placeService.updatePlace(id, place)
            .success(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                notificationService.success( 'Place updated', 'The place have been successfully updated');
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
                notificationService.error( 'Place not updated', 'Something terrible happened while updating the place');
            });
    };

    $scope.create = function (place) {
        placeService.createPlace(place)
            .success(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                notificationService.success( 'Place created', 'The place have been successfully created');
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
                notificationService.error( 'Place not created', 'Something terrible happened while creating the place');
            });
    };

//    google.maps.event.addDomListener(window, 'load', initialize);
});