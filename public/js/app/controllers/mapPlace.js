'use strict';

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 17/11/12
 * Time: 01:22
 * To change this template use File | Settings | File Templates.
 */
function MapPlaceCtrl($scope, placeService) {

    $scope.myMarkers = [];
    $scope.mapOptions = {
        //center: new google.maps.LatLng(35.784, -78.670),
        center: new google.maps.LatLng(43.602521593464054, 1.441223145229742), // Toulouse
        zoom: 12,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    $scope.addMarker = function ($event) {
        $scope.myMarkers.push(new google.maps.Marker({
            map: $scope.myMap,
            position: $event.latLng
        }));
    };
    $scope.setZoomMessage = function (zoom) {
        $scope.zoomMessage = 'You just zoomed to ' + zoom + '!';
//        console.log(zoom, 'zoomed')
    };
    $scope.openMarkerInfo = function (marker) {
        console.log("openMarkerInfo",marker);
        $scope.currentMarker = marker;
        $scope.currentMarkerLat = marker.getPosition().lat();
        $scope.currentMarkerLng = marker.getPosition().lng();
        $scope.myInfoWindow.open($scope.myMap, marker);
    };
    $scope.setMarkerPosition = function (marker, lat, lng) {
        marker.setPosition(new google.maps.LatLng(lat, lng));
    };

    // The function that will be executed on button click (ng-click="search()")
    $scope.loadPlaces = function () {
        // Create the http post request
        // the data holds the keywords
        // The request is a JSON request.
        placeService.getMapData()
            .success(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                angular.forEach(data, function (item) {
                    $scope.myMarkers.push(createMarker(item));
                });
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
    };


    // The function that will be executed on button click (ng-click="search()")
    $scope.findByCity = function () {
        console.log("findByCity", $scope.city);
        $scope.myMarkers = [];
        placeService.findByCity($scope.city)
            .success(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                angular.forEach(data, function (item) {
                    $scope.myMarkers.push(createMarker(item));
                });
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
    };

    // The function that will be executed on button click (ng-click="search()")
    $scope.findByZipcode = function () {
        console.log("findByZipcode", $scope.zipcode);
        // Create the http post request
        // the data holds the keywords
        // The request is a JSON request.
        $scope.myMarkers = [];
        placeService.findByZipcode($scope.zipcode)
            .success(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                angular.forEach(data, function (item) {
                    $scope.myMarkers.push(createMarker(item));
                });
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
    };

    var createMarker = function(item){
        var myLatlng = new google.maps.LatLng(item.latitude, item.longitude);

        return new google.maps.Marker({
            map: $scope.myMap,
            position: myLatlng,
            title: item.name + " - " + item.zipcode + " " + item.city ,
            idPlace:item.id
        });
    };

    // $scope.loadPlaces();
}