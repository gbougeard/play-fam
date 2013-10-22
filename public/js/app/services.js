'use strict';

var services = angular.module('fam.services', []);

services.factory('famHelper', function () {
    var buildIndex = function (source, property) {
        var tempArray = [];
        for (var i = 0, len = source.length; i < len; ++i) {
            tempArray[source[i][property]] = source[i];
        }
        return tempArray;
    };

    return {
        buildIndex: buildIndex
    };
});

services.factory('eventService', function ($http) {

    var ajax = {
        "headers": [
            {'Content-Type': 'application/json'}
        ]
    };

    var getEvent = function (id) {
        return $http.get(jsRoutes.controllers.Events.view(id).url, ajax);
    };

    return {
        getEvent: getEvent
    };
});

services.factory('matchService', function ($http) {

    var getMatch = function (id) {
        return $http.get(jsRoutes.controllers.Matchs.jsonById(id).url);
    };

    var getMatchTeams = function (idMatch, idTeam) {
        return $http.get(jsRoutes.controllers.MatchTeams.jsonByMatchAndTeam(idMatch, idTeam).url);
    };

    var getMatchPlayers = function (idMatch, idTeam) {
        return $http.get(jsRoutes.controllers.MatchPlayers.jsonByMatchAndTeam(idMatch, idTeam).url);
    };

    var getGoals = function (idMatch, idTeam) {
        return $http.get(jsRoutes.controllers.Goals.jsonByMatchAndTeam(idMatch, idTeam).url);
    };

    var getCards = function (idMatch, idTeam) {
        return $http.get(jsRoutes.controllers.Cards.jsonByMatchAndTeam(idMatch, idTeam).url);
    };

    var getSubs = function (idMatch, idTeam) {
        return $http.get(jsRoutes.controllers.Substitutions.jsonByMatchAndTeam(idMatch, idTeam).url);
    };

    return {
        getMatch: getMatch,
        getMatchTeams: getMatchTeams,
        getMatchPlayers: getMatchPlayers,
        getGoals: getGoals,
        getCards: getCards,
        getSubs: getSubs
    };
});