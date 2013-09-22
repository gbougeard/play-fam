define(["angular", "moment"], function (angular) {
    "use strict";

    var EventCtrl = function ($scope, eventService, placeService, notificationService) {

        $scope.event = {};
        $scope.comments = {};

        $scope.teams = [];

        $scope.selectedTeams = [];

        $scope.statuses = [];
        $scope.places = [];
        $scope.selectedPlace = '';

        $scope.types = [];
        $scope.selectedType = '';


        $scope.name = '';
        $scope.duration = 0;
        $scope.datepicker = {'date': ''};
        $scope.timepicker = { 'time': ''};

        $scope.recurrence = [
            {'id': 'D', 'name': 'Every day'},
            {'id': 'W', 'name': 'Every week'},
            {'id': 'M', 'name': 'Every month'},
            {'id': 'Y', 'name': 'Every year'}
        ];
        $scope.selectedEvery = ''; // 1 to 30
        $scope.days = ['L', 'Ma', 'Me', 'J', 'V', 'S', 'D'];
        $scope.dtBegin = new Date();
        $scope.dtEnd = '';
        $scope.endAfter = ''; // after ? occurrences
        $scope.recendopts = 'neverEnd';

        /**
         *
         */
        $scope.create = function () {

            $scope.datepicker.date.setHours($scope.timepicker.time.split(':')[0]);
            $scope.datepicker.date.setMinutes($scope.timepicker.time.split(':')[1]);
            console.log($scope.datepicker.date, $scope.timepicker);

            var event = {
                "name": $scope.name,
                "dtEvent": $scope.datepicker.date,
                "duration": $scope.duration,
                "placeId": parseInt($scope.selectedPlace, 10),
                "typEventId": parseInt($scope.selectedType, 10),
                "eventStatusId": 25,
                "comments": $scope.comments
            };
            var e = {
                "event": event,
                "teams": $scope.selectedTeams
            }
            console.log("create", e);
            eventService.create(e)
                .then(function (response) {
                    notificationService.notify({
                        title: 'Event created',
                        text: 'The event have been successfully created',
                        type: 'success'
                    });
                    eventService.saveTeams(reponse.data, e)
                        .then(function (response) {
                            notificationService.notify({
                                title: 'Teams saved',
                                text: 'The Event teams have been successfully saved',
                                type: 'success'
                            });
                        });
                });
        };
        /**
         *
         */
        $scope.update = function () {
            console.log("update");
            $scope.datepicker.date.setHours($scope.timepicker.time.split(':')[0]);
            $scope.datepicker.date.setMinutes($scope.timepicker.time.split(':')[1]);
            $scope.event.event.dtEvent = $scope.datepicker.date;
            $scope.event.event.placeId = parseInt($scope.event.event.placeId, 10);
            $scope.event.event.eventStatusId = parseInt($scope.event.event.eventStatusId, 10);
            $scope.event.event.typEventId = parseInt($scope.event.event.typEventId, 10);

            eventService.update($scope.event)
                .then(function (response) {
                    notificationService.notify({
                        title: 'Event updated',
                        text: 'The event have been successfully updated',
                        type: 'success'
                    });
                    eventService.saveTeams($scope.event.event.id, $scope.event)
                        .then(function (response) {
                            notificationService.notify({
                                title: 'Teams saved',
                                text: 'The Event teams have been successfully saved',
                                type: 'success'
                            });
                        });
                });
        };
        /**
         *
         */
        $scope.load = function () {
            console.log("load");

//            $scope.statuses = Restangular.all('eventStatuses').getList();
            eventService.loadEventStatuses().then(function (response) {
                $scope.statuses = response.data;
            });
//            $scope.places = Restangular.all('places').getList();
            placeService.loadPlaces().then(function (response) {
//                $scope.places = response.data;
            });
//            $scope.types = Restangular.all('typEvents').getList();
            eventService.loadTypEvents().then(function (response) {
                $scope.types = response.data;
            });

            if (angular.isDefined($scope.event.event)) {
                $scope.datepicker.date = new Date($scope.event.event.dtEvent);
                $scope.timepicker.time = moment($scope.event.event.dtEvent).format('HH:mm');
            }
        };

    };

    EventCtrl.$inject = ["$scope", "eventService", "placeService", "notificationService"];

    return {
        EventCtrl: EventCtrl
    };

});
