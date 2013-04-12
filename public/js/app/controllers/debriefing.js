'use strict';


function DebriefingCtrl($scope, $http, $location, $rootScope, $q) {

    $scope.idMatch = $location.absUrl().substring($location.absUrl().lastIndexOf('/') + 1);
    console.log($scope.idMatch);

    $scope.match = {};
    $scope.event = {};
    $scope.mtHome = {};
    $scope.mtAway = {};
    $scope.homeGoals = [];
    $scope.awayGoals = [];
    $scope.homeCards = [];
    $scope.awayCards = [];
    $scope.homeSubs = [];
    $scope.awaySubs = [];
    $scope.homePlayers = [];
    $scope.awayPlayers = [];

    $scope.selectedItem = '10';

    $scope.notes = [
        {id: "10", name: "10"},
        {id: "9.5", name: "9.5"}
    ];

    $scope.loadMatch = function (idMatch) {
        console.log(jsRoutes.controllers.Matchs.jsonById(idMatch));
        jsRoutes.controllers.Matchs.jsonById(idMatch).ajax().then(function (response) {
                console.log(response);
                $scope.match = response;
                $scope.loadEvent(response.eventId);
                $scope.loadHome(idMatch);
                $scope.loadAway(idMatch);


            }
        ).then(function () {
                console.log($scope.homeGoals, $scope.awayPlayers);
                $rootScope.$digest();
            });
    };

    $scope.loadEvent = function (idEvent) {
        jsRoutes.controllers.Events.jsonById(idEvent).ajax().then(function (response) {
                console.log(response);
                $scope.event = response;
                $rootScope.$digest();
            }
        );
    };

    $scope.loadHome = function (idMatch) {
        jsRoutes.controllers.MatchTeams.jsonByMatchAndHome(idMatch).ajax().then(function (response) {
                console.log("Home", idMatch, response);
                $scope.mtHome = response;
                $rootScope.$digest();
                $scope.loadTeamData(idMatch, response.team.id, true);
            }
        );
    };

    $scope.loadAway = function (idMatch) {
        jsRoutes.controllers.MatchTeams.jsonByMatchAndAway(idMatch).ajax().then(function (response) {
                console.log("Away", idMatch, response);
                $scope.mtAway = response;
                $rootScope.$digest();
                $scope.loadTeamData(idMatch, response.team.id, false);

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
                console.log(response);
                if (home) {
                    $scope.homePlayers = response;
                } else {
                    $scope.awayPlayers = response;
                }
                $rootScope.$digest();
            }
        );
    };

    $scope.loadGoals = function (idMatch, idTeam, home) {
        console.log(idMatch, idTeam, home);
        jsRoutes.controllers.Goals.jsonByMatchAndTeam(idMatch, idTeam).ajax().then(function (response) {
                console.log(response);
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
                console.log(response);
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
                console.log(response);
                if (home) {
                    $scope.homeSubs = response;
                } else {
                    $scope.awaySubs = response;
                }
                $rootScope.$digest();
            }
        );
    };


    $scope.loadMatch($scope.idMatch);
}