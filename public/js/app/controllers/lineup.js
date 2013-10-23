'use strict';


function LineupCtrl($scope, eventService, matchService) {

    $scope.idMatch = 0;
    $scope.idTeam = 0;

    $scope.match = {};
    $scope.event = {};
    $scope.matchTeam = {};
    $scope.answers = [];
    $scope.players = [];

    $scope.selectedItem = {};


    $scope.loadMatch = function () {
        console.log('loadMatch', $scope.idMatch, $scope.idTeam);
        matchService.getMatch($scope.idMatch)
            .success(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                $scope.match = data;
                $scope.loadEvent(response.eventId);
                $scope.loadAnswers(response.eventId);
                $scope.loadMatchTeam($scope.idMatch, $scope.idTeam);
                $scope.loadPlayers($scope.idMatch, $scope.idTeam);
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
    };

    $scope.loadEvent = function (idEvent) {
        eventService.getEvent(idEvent)
            .success(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                $scope.event = data;
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
    };

    $scope.loadAnswers = function (idEvent) {
        answerService.getAnswers(idEvent)
            .success(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                $scope.answers = data;
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
    };

    $scope.loadMatchTeam = function (idMatch, idTeam) {
        matchService.getMatchTeams(idMatch, idTeam)
            .success(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                $scope.matchTeam = data;
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
    };

    $scope.loadPlayers = function (idMatch, idTeam) {
        matchService.getMatchPlayers(idMatch, idTeam)
            .success(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                $scope.players = data;
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
    };


    $scope.edit = function (matchPlayer) {
        matchPlayer.editing = true;
        $scope.selectedItem = matchPlayer;
        console.log("edit", $scope.selectedItem);
    };

    $scope.save = function () {
        console.log("save", $scope.selectedItem);
    };


}