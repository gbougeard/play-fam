'use strict';

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 17/11/12
 * Time: 01:22
 * To change this template use File | Settings | File Templates.
 */
function MapPlaceCtrl($scope, $http) {

    $scope.myMarkers = [];
    $scope.mapOptions = {
        //center: new google.maps.LatLng(35.784, -78.670),
        center: new google.maps.LatLng(43.602521593464054, 1.441223145229742), // Toulouse
        zoom: 6,
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
        jsRoutes.controllers.Places.gmapData().ajax({
            success: function (data, status) {
                // console.log("loadPlaces success", data, status);
                $scope.status = status;

                angular.forEach(data, function (item) {
//                    console.log(item);
                    var myLatlng = new google.maps.LatLng(item.latitude, item.longitude);

                    $scope.myMarkers.push(new google.maps.Marker({
                        map: $scope.myMap,
                        position: myLatlng,
                        title: item.name + " - " + item.zipcode + " "+ item.city
                    }));
                });

                $scope.$digest();
            },
            error: function (data, status) {
                $scope.data = data || "Request failed";
                $scope.status = status;
                $scope.$digest();
            }
        });
    };


    // The function that will be executed on button click (ng-click="search()")
    $scope.findByCity = function () {
        console.log("findLikeCity", $scope.city);
        $scope.myMarkers = [];
        // Create the http post request
        // the data holds the keywords
        // The request is a JSON request.
        jsRoutes.controllers.Places.jsonLikeCity($scope.city).ajax({
            success: function (data, status) {
                // console.log("loadPlaces success", data, status);
                $scope.status = status;

                angular.forEach(data, function (item) {
//                    console.log(item);
                    var myLatlng = new google.maps.LatLng(item.latitude, item.longitude);

                    $scope.myMarkers.push(new google.maps.Marker({
                        map: $scope.myMap,
                        position: myLatlng,
                        title: item.name + " - " + item.zipcode + " "+ item.city
                    }));
                });

                $scope.$digest();
            },
            error: function (data, status) {
                $scope.data = data || "Request failed";
                $scope.status = status;
                $scope.$digest();
            }
        });
    };

    // The function that will be executed on button click (ng-click="search()")
    $scope.findByZipcode = function () {
        console.log("findLikeZipcode", $scope.zipcode);
        // Create the http post request
        // the data holds the keywords
        // The request is a JSON request.
        $scope.myMarkers = [];
        jsRoutes.controllers.Places.jsonLikeZipcode($scope.zipcode).ajax({
            success: function (data, status) {
                // console.log("loadPlaces success", data, status);
                $scope.status = status;

                angular.forEach(data, function (item) {
//                    console.log(item);
                    var myLatlng = new google.maps.LatLng(item.latitude, item.longitude);

                    $scope.myMarkers.push(new google.maps.Marker({
                        map: $scope.myMap,
                        position: myLatlng,
                        title: item.name + " - " + item.zipcode + " "+ item.city
                    }));
                });

                $scope.$digest();
            },
            error: function (data, status) {
                $scope.data = data || "Request failed";
                $scope.status = status;
                $scope.$digest();
            }
        });
    };

   // $scope.loadPlaces();
}