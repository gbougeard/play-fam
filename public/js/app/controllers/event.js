'use strict';


fam.controller('EventCtrl', function ($scope, notificationService, eventService, placeService) {

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
     * @param id
     */
    var saveTeams = function (id, teams) {
        var c = [];

        angular.forEach(teams, function (team) {
            c.push({
                eventId: id,
                teamId: team
            });
        });
        eventService.saveTeams(c)
            .success(function (data, status, headers, config) {
                notificationService.success('Teams saved', 'The Event teams have been successfully created.');
                window.location = jsRoutes.controllers.Events.view(id).url;
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
                notificationService.error('Teams not saved', 'Something terrible happened while saving teams.');
            });
    };

    /**
     *
     */
    $scope.save = function () {

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
        console.log("save", event);

        eventService.create(event)
            .success(function (data, status, headers, config) {
                notificationService.success('Event created', 'The event have been successfully created');
                var id = data;
                saveTeams(id, $scope.selectedTeams);
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
                notificationService.error('Event not created', 'Something terrible happened while creating event.');
            });
    };
    /**
     *
     */
    $scope.update = function () {
        $scope.datepicker.date.setHours($scope.timepicker.time.split(':')[0]);
        $scope.datepicker.date.setMinutes($scope.timepicker.time.split(':')[1]);
        $scope.event.event.dtEvent = $scope.datepicker.date;
        $scope.event.event.placeId = parseInt($scope.event.event.placeId, 10);
        $scope.event.event.eventStatusId = parseInt($scope.event.event.eventStatusId, 10);
        $scope.event.event.typEventId = parseInt($scope.event.event.typEventId, 10);

        eventService.update($scope.event.event)
            .success(function (data, status, headers, config) {
                notificationService.success('Event updated', 'The event have been successfully updated');
                saveTeams($scope.event.event.id, $scope.event.teams);
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
                notificationService.error('Event not updated', 'Something terrible happened while updating event.');
            });
    };

    /**
     *
     */
    $scope.load = function () {
        if (angular.isDefined($scope.event.event)) {
            $scope.datepicker.date = new Date($scope.event.event.dtEvent);
            $scope.timepicker.time = moment($scope.event.event.dtEvent).format('HH:mm');
        }

        eventService.getEventStatuses()
            .success(function (data, status, headers, config) {
                $scope.statuses = data;
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
        placeService.getPage(0)
            .success(function (data, status, headers, config) {
                $scope.places = data.items;
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
        eventService.getTypEvents()
            .success(function (data, status, headers, config) {
                $scope.types = data;
            })
            .error(function (data, status, headers, config) {
                console.error(data, status, headers, config);
            });
    }
});