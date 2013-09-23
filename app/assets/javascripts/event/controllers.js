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

    //====================================================================================
    var AgendaCtrl = function ($scope, $filter, eventService, placeService, notificationService) {
        var date = new Date();
        var d = date.getDate();
        var m = date.getMonth();
        var y = date.getFullYear();

        $scope.eventSource = {
            url: "//www.google.com/calendar/feeds/os5iqgd70elova2if2f8jdffh8%40group.calendar.google.com/public/basic",
            className: 'gcal-event'           // an option!
//        currentTimezone: 'France/Paris' // an option!

        };

        $scope.events = [
            {title: 'All Day Event', start: new Date(y, m, 1)},
            {title: 'Long Event', start: new Date(y, m, d - 5), end: new Date(y, m, d - 2)},
            {id: 999, title: 'Repeating Event', start: new Date(y, m, d - 3, 16, 0), allDay: false, className:'typEvent1'},
            {id: 999, title: 'Repeating Event', start: new Date(y, m, d + 4, 16, 0), allDay: false, className:'typEvent2'},
            {title: 'Birthday Party', start: new Date(y, m, d + 1, 19, 0), end: new Date(y, m, d + 1, 22, 30), allDay: false},
            {title: 'Click for Google', start: new Date(y, m, 28), end: new Date(y, m, 29), url: 'http://google.com/'}
        ];

        $scope.eventSources = [$scope.events, $scope.eventSource];

        $scope.addEvent = function () {
            $scope.events.push({
                title: 'Open Sesame',
                start: new Date(y, m, 28),
                end: new Date(y, m, 29)
            });
        };

        $scope.remove = function (index) {
            $scope.events.splice(index, 1);
        };

        // The function that will be executed on button click (ng-click="search()")
        $scope.loadEvents = function () {
            // Create the http post request
            // the data holds the keywords
            // The request is a JSON request.
            eventService.loadEvents().then(function (response) {
               var events = response.data;

                angular.forEach(events, function (item) {
//                    console.log(item, $filter('eventColor')(item.typEventId));
                    var dtStart = new Date(item.dtEvent);
                    var dtEnd = moment(item.dtEvent).add("minutes", item.duration).toDate();

                    var event = {
                        id: item.id,
                        title: item.name,
                        start: dtStart,
                        end: dtEnd,
                        url:   $filter('eventUrl')(item),
//                        className: 'typEvent'+item.typEventId ,
                        editable:false,
                        color: $filter('eventColor')(item.typEventId)
//                        ,textColor: $filter('eventTextColor')(item.typEventId)

                    };
//                    console.log(item, event);
                    $scope.events.push(event);
                });
            });
        };
    };
    AgendaCtrl.$inject = ["$scope", "$filter", "eventService", "placeService", "notificationService"];

    //====================================================================================
    return {
        EventCtrl: EventCtrl,
        AgendaCtrl: AgendaCtrl
    };

});
