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

services.factory("notificationService", function () {
    var notify = function (hash) {
        $.pnotify(hash);
    };
    var success = function (title, msg) {
        $.pnotify({
            'title': title,
            'text': msg,
            'type': 'success'
        });
    };
    var error = function (title, msg) {
        $.pnotify({
            'title': title,
            'text': msg,
            'type': 'error'
        });
    };
    return {
        notify:     notify,
        success:    success,
        error:      error
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

    var getAgenda = function () {
        return $http.get(jsRoutes.controllers.Events.agenda().url, ajax);
    };

    var update = function (event) {
        return $http.post(jsRoutes.controllers.Events.update(event.id).url, event);
    };

    var create = function (event) {
        return $http.post(jsRoutes.controllers.Events.create().url, event);
    };

    var saveTeams = function (eventTeams) {
        return $http.post(jsRoutes.controllers.Events.saveTeams().url, eventTeams);
    };

    var getEventStatuses = function () {
        return $http.get(jsRoutes.controllers.EventStatuses.jsonList().url, ajax);
    };

    var getTypEvents = function () {
        return $http.get(jsRoutes.controllers.TypEvents.jsonList().url, ajax);
    };

    return {
        getEvent:           getEvent,
        getAgenda:          getAgenda,
        getEventStatuses:   getEventStatuses,
        getTypEvents:       getTypEvents,
        update:             update,
        create:             create,
        saveTeams:          saveTeams
    };
});

services.factory('answerService', function ($http) {

    var ajax = {
        "headers": [
            {'Content-Type': 'application/json'}
        ]
    };

    var getAnswers = function (id) {
        return $http.get(jsRoutes.controllers.Answers.jsonByEvent(idEvent).url, ajax);
    };

    return {
        getAnswers: getAnswers
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
        getMatch:           getMatch,
        getMatchTeams:      getMatchTeams,
        getMatchPlayers:    getMatchPlayers,
        getGoals:           getGoals,
        getCards:           getCards,
        getSubs:            getSubs
    };
});

services.factory('placeService', function ($http) {

    var ajax = {
        "headers": [
            {'Content-Type': 'application/json'}
        ]
    };

    var getPlace = function (id) {
        return $http.get(jsRoutes.controllers.Places.view(id).url, ajax);
    };

    var getPage = function (page) {
        return $http.get(jsRoutes.controllers.Places.list(page, 2).url, ajax);
    };

    var updatePlace = function (place) {
        return $http.post(jsRoutes.controllers.Places.update(place.id).url, place);
    };

    var createPlace = function (place) {
        return $http.post(jsRoutes.controllers.Places.save(place).url);
    };

    var findByCity = function (city) {
        return $http.get(jsRoutes.controllers.Places.mapByCity(city).url, ajax);
    };

    var findByZipcode = function (zipcode) {
        return $http.get(jsRoutes.controllers.Places.mapByZipcode(zipcode).url, ajax);
    };

    var findByClub = function (id) {
        return $http.get(jsRoutes.controllers.Clubs.findPlaces(id).url, ajax);
    };

    var getMapData = function () {
        return $http.get(jsRoutes.controllers.Places.gmapData().url, ajax);
    };


    return {
        getPlace:       getPlace,
        getPage:        getPage,
        updatePlace:    updatePlace,
        createPlace:    createPlace,
        findByCity:     findByCity,
        findByZipcode:  findByZipcode,
        findByClub:     findByClub,
        getMapData:     getMapData

    };
});