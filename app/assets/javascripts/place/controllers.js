define(["angular"], function (angular, b) {
    "use strict";

    var PlaceCtrl = function ($scope, placeService, notificationService) {

        $scope.marker = {};
        $scope.mapOptions = {
            //center: new google.maps.LatLng(35.784, -78.670),
            center: new google.maps.LatLng(43.602521593464054, 1.441223145229742), // Toulouse
            zoom: 12,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        var geocoder = new google.maps.Geocoder();

        google.maps.event.addDomListener(window, 'load', console.log("loaded"));


        $scope.buildAddress = function () {
            $scope.address = $scope.place.address + " , " + $scope.place.zipcode + " " + $scope.place.city;
            console.log("buildAddress", $scope.place, $scope.address);
        };

        $scope.loadPlace = function (id) {
//            console.log("PlacesCtrl.loadPlace", id);
            placeService.loadPlace(id).then(function (response) {
//                console.log('resp', response);
                $scope.place = response.data;

                if ($scope.place.city != "") {
                    $scope.geocode();
                }
            });

        };

        $scope.geocode = function () {
            $scope.buildAddress();
            geocoder.geocode({ 'address': $scope.address}, function (results, status) {
                if (status == google.maps.GeocoderStatus.OK) {
//                map.setCenter(results[0].geometry.location);
                    var marker = new google.maps.Marker({
                        map: $scope.myMap,
                        position: results[0].geometry.location
                    });
                    $scope.marker = results[0].geometry.location;
                    $scope.place.latitude = marker.getPosition().lat();
                    $scope.place.longitude = marker.getPosition().lng();
                    $scope.myMap.panTo($scope.marker);
//                $scope.myMarkers.push(marker);
//                    $scope.$digest();
                } else {
//                    alert('Geocode was not successful for the following reason: ' + status);
                    notificationService.notify({
                        title: 'Place geocoding',
                        text: 'Geocode was not successful for the following reason: ' + status,
                        type: 'error'
                    });
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

        $scope.update = function () {
            placeService.update($scope.place).then(function (response) {
                notificationService.notify({
                    title: 'Place updated',
                    text: 'The place have been successfully updated',
                    type: 'success'
                });
            });
        };

        $scope.create = function () {
            placeService.create($scope.place).then(function (response) {
                notificationService.notify({
                    title: 'Place created',
                    text: 'The place have been successfully created',
                    type: 'success'
                });
            });
        };

        $scope.setZoomMessage = function (zoom) {
            $scope.zoomMessage = 'You just zoomed to ' + zoom + '!';
//        console.log(zoom, 'zoomed')
        };
        $scope.openMarkerInfo = function (marker) {
            console.log("openMarkerInfo", marker);
            $scope.currentMarker = marker;
            $scope.currentMarkerLat = marker.getPosition().lat();
            $scope.currentMarkerLng = marker.getPosition().lng();
            $scope.myInfoWindow.open($scope.myMap, marker);
        };
        $scope.setMarkerPosition = function (marker, lat, lng) {
            marker.setPosition(new google.maps.LatLng(lat, lng));
        };


        $scope.addMarkers = function (places) {
            $scope.myMarkers = [];
            angular.forEach(places, function (item) {
                console.log(item);
                var myLatlng = new google.maps.LatLng(item.latitude, item.longitude);

                var marker = new google.maps.Marker({
                    map: $scope.myMap,
                    position: myLatlng,
                    title: item.name + " - " + item.zipcode + " " + item.city,
                    idPlace: item.id
                });
                console.log(marker);
                $scope.myMarkers.push(marker);
            });
        };


        // The function that will be executed on button click (ng-click="search()")
        $scope.findByCity = function () {
            console.log("findLikeCity", $scope.city);
            placeService.findByCity($scope.city).then(function (response) {
                $scope.addMarkers(response.data);
            });
//            $scope.$digest();
        };

        // The function that will be executed on button click (ng-click="search()")
        $scope.findByZipcode = function () {
            console.log("findByZipcode", $scope.zipcode);
            placeService.findByZipcode($scope.zipcode).then(function (response) {
                $scope.addMarkers(response.data);
            });
//            $scope.$digest();
        };

        $scope.loadPlaces = function () {
            placeService.loadPlaces().then(function (response) {
                $scope.addMarkers(response.data);
            });
//            $scope.$digest();
        };


    };

    PlaceCtrl.$inject = ["$scope", "placeService", "notificationService"];

    return {
        PlaceCtrl: PlaceCtrl
    };

});
