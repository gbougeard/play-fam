'use strict';


function DebriefingCtrl($scope, eventService, matchService) {

    $scope.idMatch = 0;
    $scope.idTeam = 0;

    $scope.match = {};
    $scope.event = {};
    $scope.mtHome = {};
    $scope.goals = [];
    $scope.subs = [];
    $scope.players = [];

    $scope.selectedItem = {};
    $scope.editing = false;

    $scope.notes = [
        {id: "10", name: "10"},
        {id: "9.5", name: "9.5"}
    ];

    $scope.loadMatch = function (idMatch, idTeam) {
        $scope.idMatch = idMatch;
        $scope.idTeam = idTeam;

        console.log('loadMatch', $scope.idMatch, $scope.idTeam);

        matchService.getMatch($scope.idMatch)
            .then(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                $scope.match = data;
                $scope.loadEvent($scope.match.eventId);
            }
            , function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });

        matchService.getMatchTeams($scope.idMatch, $scope.idTeam)
            .then(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                $scope.matchTeam = data;
            }
            , function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });

        $scope.loadTeamData($scope.idMatch, $scope.idTeam);


    };

    $scope.loadEvent = function (idEvent) {
        eventService.getEvent(idEvent)
            .then(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                $scope.event = data;
            }
            , function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
    };


    $scope.loadTeamData = function (idMatch, idTeam) {
        $scope.loadPlayers(idMatch, idTeam);
        $scope.loadGoals(idMatch, idTeam);
        $scope.loadCards(idMatch, idTeam);
        $scope.loadSubs(idMatch, idTeam);
    };

    $scope.loadPlayers = function (idMatch, idTeam) {
        matchService.getMatchPlayers(idMatch, idTeam)
            .then(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                $scope.players = data;
            }
            , function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
    };

    $scope.loadGoals = function (idMatch, idTeam) {
        matchService.getGoals(idMatch, idTeam)
            .then(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                $scope.goals = data;
            }
            , function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
    };

    $scope.loadCards = function (idMatch, idTeam) {
//        console.log(idMatch, idTeam, home);
        matchService.getCards(idMatch, idTeam)
            .then(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                $scope.cards = data;
            }
            , function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
    };

    $scope.loadSubs = function (idMatch, idTeam) {
//        console.log(idMatch, idTeam, home);
        matchService.getSubs(idMatch, idTeam)
            .then(function (data, status, headers, config) {
                // data contains the response
                // status is the HTTP status
                // headers is the header getter function
                // config is the object that was used to create the HTTP request
                $scope.subs = data;
            }
            , function (data, status, headers, config) {
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