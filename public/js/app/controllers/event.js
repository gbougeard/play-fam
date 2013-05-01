'use strict';


function EventCtrl($scope, $http) {

    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

    $scope.teams = [
//        {
//            "id": "1",
//            "name": "Team1"
//        },
//        {
//            "id": "2",
//            "name": "Team2"
//        },
//        {
//            "id": "3",
//            "name": "Team3"
//        }
    ];

    $scope.selectedTeams = [
        "1"
    ];

    $scope.places = [
//        {
//            "id": "1",
//            "name": "Place1"
//        },
//        {
//            "id": "2",
//            "name": "Place2"
//        },
//        {
//            "id": "3",
//            "name": "Place3"
//        }
    ];

    $scope.selectedPlace = "";

    $scope.types = [];
    $scope.selectedType = "";

    $scope.name = "";
    $scope.duration = 0;
    $scope.datepicker = { "date": date};
    $scope.timepicker = { "time": ""};

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

        //TODO add alert on success & errors
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
                angular.forEach(teams, function (team) {
                    c.push({
                        eventId: id,
                        teamId: team
                    });
                });
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
}