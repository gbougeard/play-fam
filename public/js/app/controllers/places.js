'use strict';


fam.controller('PlacesCtrl', function ($scope, $http, $location, Restangular) {

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

        console.log("geocode",$scope.place, $scope.address);
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
                $scope.place.latitude = $scope.marker.pb;
                $scope.place.longitude = $scope.marker.qb;
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
        var placeFuture = Restangular.one('places', id);
        placeFuture.get().then(function(result){
           $scope.place = result;
           $scope.address = $scope.place.address + " , " + $scope.place.zipcode + " " + $scope.place.city;
        });

    };

    $scope.update = function(){
        jsRoutes.controllers.Places.update($scope.place.id).ajax({
            data: JSON.stringify($scope.place),
            contentType: "application/json",
            dataType: "json",
            success: function (data, status) {
                console.log("success!", data, status);

                $.pnotify({
                    title: 'Place updated',
                    text: 'The place have been successfully updated',
                    type: 'success',
                    icon: 'picon picon-flag-green'
                });
            },
            error: function (data, status) {
                console.log("Failed!", data, status);
                //$scope.data = data || "Request failed";
                $.pnotify({
                    title: 'Oh No!',
                    text: 'Something terrible happened while updating the place.',
                    type: 'error'
                });
            }
        });


    }

//    google.maps.event.addDomListener(window, 'load', initialize);
});