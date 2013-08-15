'use strict';


function DebriefingCtrl($scope, $http, $location, $rootScope, $q) {

    $scope.idMatch = 0;
    $scope.idTeam = 0;

    $scope.match = {};
    $scope.event = {};
    $scope.mtHome = {};
    $scope.homeGoals = [];
    $scope.homeSubs = [];
    $scope.homePlayers = [];

    $scope.selectedItem = {};
    $scope.editing = false;

    $scope.notes = [
        {id: "10", name: "10"},
        {id: "9.5", name: "9.5"}
    ];

    $scope.loadMatch = function () {
        console.log('loadMatch', $scope.idMatch, $scope.idTeam);

        jsRoutes.controllers.Matchs.jsonById($scope.idMatch).ajax().then(function (response) {
//                console.log(response);
                $scope.match = response;
                $scope.loadEvent(response.eventId);
                $scope.loadHome($scope.idMatch, $scope.idTeam);


            }
        ).then(function () {
                console.log($scope.homeGoals, $scope.awayPlayers);
                $rootScope.$digest();
            });
    };

    $scope.loadEvent = function (idEvent) {
        jsRoutes.controllers.Events.jsonById(idEvent).ajax().then(function (response) {
//                console.log(response);
                $scope.event = response;
                $rootScope.$digest();
            }
        );
    };

    $scope.loadHome = function (idMatch, idTeam) {
        jsRoutes.controllers.MatchTeams.jsonByMatchAndTeam(idMatch, idTeam).ajax().then(function (response) {
//                console.log("Home", idMatch, response);
                $scope.mtHome = response;
                $rootScope.$digest();
                $scope.loadTeamData(idMatch, response.team.id, true);
            }
        );
    };

    $scope.loadTeamData = function (idMatch, idTeam, home) {
        $scope.loadPlayers(idMatch, idTeam, home);
        $scope.loadGoals(idMatch, idTeam, home);
        $scope.loadCards(idMatch, idTeam, home);
        $scope.loadSubs(idMatch, idTeam, home);
    };

    $scope.loadPlayers = function (idMatch, idTeam, home) {
        console.log(idMatch, idTeam, home);
        jsRoutes.controllers.MatchPlayers.jsonByMatchAndTeam(idMatch, idTeam).ajax().then(function (response) {
//                console.log(response);
                    $scope.homePlayers = response;
                angular.forEach($scope.homePlayers, function (player) {
                   player.editing = false;
                });
                $rootScope.$digest();
            }
        );
    };

    $scope.loadGoals = function (idMatch, idTeam, home) {
        console.log(idMatch, idTeam, home);
        jsRoutes.controllers.Goals.jsonByMatchAndTeam(idMatch, idTeam).ajax().then(function (response) {
//                console.log(response);
                if (home) {
                    $scope.homeGoals = response;
                } else {
                    $scope.awayGoals = response;
                }
                $rootScope.$digest();
            }
        );
    };

    $scope.loadCards = function (idMatch, idTeam, home) {
        console.log(idMatch, idTeam, home);
        jsRoutes.controllers.Cards.jsonByMatchAndTeam(idMatch, idTeam).ajax().then(function (response) {
//                console.log(response);
                if (home) {
                    $scope.homeCards = response;
                } else {
                    $scope.awayCards = response;
                }
                $rootScope.$digest();
            }
        );
    };

    $scope.loadSubs = function (idMatch, idTeam, home) {
        console.log(idMatch, idTeam, home);
        jsRoutes.controllers.Substitutions.jsonByMatchAndTeam(idMatch, idTeam).ajax().then(function (response) {
//                console.log(response);
                if (home) {
                    $scope.homeSubs = response;
                } else {
                    $scope.awaySubs = response;
                }
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