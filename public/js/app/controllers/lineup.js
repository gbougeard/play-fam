'use strict';


function LineupCtrl($scope, $http, $location, $rootScope, $q) {

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

        jsRoutes.controllers.Matchs.jsonById($scope.idMatch).ajax().then(function (response) {
                console.log("match", response);
                $scope.match = response;
                $scope.loadEvent(response.eventId);
                $scope.loadAnswers(response.eventId);
                $scope.loadMatchTeam($scope.idMatch, $scope.idTeam);
                $scope.loadPlayers($scope.idMatch, $scope.idTeam);


            }
        ).then(function () {
                $rootScope.$digest();
            });
    };

    $scope.loadEvent = function (idEvent) {
        jsRoutes.controllers.Events.jsonById(idEvent).ajax().then(function (response) {
                console.log("event",response);
                $scope.event = response;
                $rootScope.$digest();
            }
        );
    };

    $scope.loadAnswers = function (idEvent) {
        jsRoutes.controllers.Answers.jsonByEvent(idEvent).ajax().then(function (response) {
                console.log("answers", response);
                $scope.answers = response;
                $rootScope.$digest();
            }
        );
    };

    $scope.loadMatchTeam = function (idMatch, idTeam) {
        jsRoutes.controllers.MatchTeams.jsonByMatchAndTeam(idMatch, idTeam).ajax().then(function (response) {
                console.log("MatchTeam", response);
                $scope.matchTeam = response;
                $rootScope.$digest();
            }
        );
    };

    $scope.loadPlayers = function (idMatch, idTeam) {
        console.log(idMatch, idTeam);
        jsRoutes.controllers.MatchPlayers.jsonByMatchAndTeam(idMatch, idTeam).ajax().then(function (response) {
                console.log("players", response);
                $scope.players = response;
//                angular.forEach($scope.homePlayers, function (player) {
//                   player.editing = false;
//                });
                $rootScope.$digest();
            }
        );
    };



    $scope.edit = function(matchPlayer){
        matchPlayer.editing = true;
        $scope.selectedItem = matchPlayer;
        console.log("edit", $scope.selectedItem);
    }

    $scope.save = function(){
        console.log("save", $scope.selectedItem);
    }


//    $scope.loadMatch($scope.idMatch, $scope.idTeam);
}