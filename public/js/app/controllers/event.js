'use strict';


fam.controller('EventCtrl', function($scope, $http, $location) {

//    var date = new Date();
//    var d = date.getDate();
//    var m = date.getMonth();
//    var y = date.getFullYear();

    $scope.event = {};

    $scope.teams = [];

    $scope.selectedTeams = [];

    $scope.statuses = [];
    $scope.places = [];

    $scope.selectedPlace = "";

    $scope.types = [];
    $scope.selectedType = "";

    $scope.dateObject = "";

    $scope.name = "";
    $scope.duration = 0;
    $scope.datepicker = {date: ""};
//    $scope.timepicker = { "time": $scope.dateObject || moment(date.getTime())};

    $scope.save = function () {
        var event = {
            "name": $scope.name,
            "dtEvent": moment($scope.datepicker.date).format("YYYY-MM-DD"),
            "duration": $scope.duration,
            "placeId": $scope.selectedPlace,
            "typEventId": $scope.selectedType,
            "eventStatusId": 25

        }
        console.log("save" , event);

        jsRoutes.controllers.Events.save().ajax({
            data: JSON.stringify(event),
            contentType: "application/json",
            dataType: "json",
            success: function (data, status) {
                console.log("success!", data, status);

                $.pnotify({
                    title: 'Event created',
                    text: 'The event have been successfully created',
                    type: 'success',
                    icon: 'picon picon-flag-green'
                });

                var id = data;
                var c = [];
                angular.forEach($scope.selectedTeams, function (team) {
                    c.push({
                        eventId: id,
                        teamId: team
                    });
                });
                console.log(c);
                jsRoutes.controllers.Events.saveTeams().ajax({
                    data: JSON.stringify(c),
                    contentType: "application/json",
                    dataType: "json",
                    success: function (data, status) {
                        console.log("success!", data, status);
                        //resetStorage();
                        $.pnotify({
                            title: 'Teams saved',
                            text: 'The Event teams have been successfully created',
                            type: 'success'
                        });
                        console.log("goto", jsRoutes.controllers.Events.view(id).url);
//                        $location.path(jsRoutes.controllers.Events.view(id).url).replace();
                        window.location = jsRoutes.controllers.Events.view(id).url;
                    },
                    error: function (data, status) {
                        console.log("Failed!", data, status);
                        //$scope.data = data || "Request failed";
                        $.pnotify({
                            title: 'Oh No!',
                            text: 'Something terrible happened while creating event.',
                            type: 'error'
                        });
                    }
                });
            },
            error: function (data, status) {
                console.log("Failed!", data, status);
                //$scope.data = data || "Request failed";
                $.pnotify({
                    title: 'Oh No!',
                    text: 'Something terrible happened while creating the event.',
                    type: 'error'
                });
            }
        });
    };

    $scope.update = function () {
//        var event = {
//            "name": $scope.name,
//            "dtEvent": moment($scope.datepicker.date).format("YYYY-MM-DD"),
//            "duration": $scope.duration,
//            "placeId": $scope.selectedPlace,
//            "typEventId": $scope.selectedType,
//            "eventStatusId": 25
//
//        }
        console.log("update" , $scope.event);

        jsRoutes.controllers.Events.update($scope.event.event.id).ajax({
            data: JSON.stringify($scope.event.event),
            contentType: "application/json",
            dataType: "json",
            success: function (data, status) {
                console.log("success!", data, status);

                $.pnotify({
                    title: 'Event created',
                    text: 'The event have been successfully created',
                    type: 'success',
                    icon: 'picon picon-flag-green'
                });

                var id = data;
                var c = [];
                angular.forEach($scope.event.teams, function (team) {
                    c.push({
                        eventId: $scope.event.event.id,
                        teamId: team
                    });
                });
                console.log(c);
                jsRoutes.controllers.Events.saveTeams().ajax({
                    data: JSON.stringify(c),
                    contentType: "application/json",
                    dataType: "json",
                    success: function (data, status) {
                        console.log("success!", data, status);
                        //resetStorage();
                        $.pnotify({
                            title: 'Teams saved',
                            text: 'The Event teams have been successfully created',
                            type: 'success'
                        });
                        console.log("goto", jsRoutes.controllers.Events.view(id).url);
//                        $location.path(jsRoutes.controllers.Events.view(id).url).replace();
                        window.location = jsRoutes.controllers.Events.view(id).url;
                    },
                    error: function (data, status) {
                        console.log("Failed!", data, status);
                        //$scope.data = data || "Request failed";
                        $.pnotify({
                            title: 'Oh No!',
                            text: 'Something terrible happened while creating event.',
                            type: 'error'
                        });
                    }
                });
            },
            error: function (data, status) {
                console.log("Failed!", data, status);
                //$scope.data = data || "Request failed";
                $.pnotify({
                    title: 'Oh No!',
                    text: 'Something terrible happened while creating the event.',
                    type: 'error'
                });
            }
        });
    };
});