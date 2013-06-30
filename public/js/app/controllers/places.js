'use strict';


fam.controller('PlacesCtrl', function($scope, $http, $location) {

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

    $scope.codeAddress=function (address) {
        console.log("geocode", address);
//        var address = document.getElementById('address').value;
        geocoder.geocode( { 'address': address}, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
//                map.setCenter(results[0].geometry.location);
                var marker = new google.maps.Marker({
                    map:  $scope.myMap,
                    position: results[0].geometry.location
                });
                console.log("result", results[0].geometry.location, $scope.marker);
                $scope.marker= results[0].geometry.location;
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
        $scope.marker= $event.latLng;
        console.log("addMarker", $scope.marker, $event);
//        $scope.$digest();
    };

//    google.maps.event.addDomListener(window, 'load', initialize);
});