'use strict';

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 17/11/12
 * Time: 01:22
 * To change this template use File | Settings | File Templates.
 */
fam.filter('eventBkgColor', function () {
    var CSS = {
        1: "grey",
        2: "green",
        3: "orange"
    };

    return function (id) {
        return CSS[id];
    };
});
fam.filter('eventColor', function () {
    var CSS = {
        1: "Gray",
        2: "CadetBlue",
        3: "orange"
    };

    return function (id) {
        return CSS[id];
    };
});
fam.filter('eventTextColor', function () {
    var CSS = {
        1: "black",
        2: "grey",
        3: "white"
    };

    return function (id) {
        return CSS[id];
    };
});
fam.filter('eventBorderColor', function () {
    var CSS = {
        1: "red",
        2: "green",
        3: "orange"
    };

    return function (id) {
        return CSS[id];
    };
});

fam.filter('eventUrl', function () {

    return function (data) {
//        console.log(data);
        var url = '';
        switch (data.typEventId) {
            case  1 : // Workout
                url += '/workouts/';
                break;
            case  2 : // Match
                url += '/matchs/';
                break;
            default:
                url += '/events/';
                break;
        }
        switch (data.eventStatusId) {
            case  27 : // Fini
                url += 'event/';
                break;
            default:
                break;
        }
        return url + data.id
    };
});


function AgendaCtrl($scope, $http, $filter) {

    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

    $scope.eventSource = {
        url: "https://www.google.com/calendar/feeds/os5iqgd70elova2if2f8jdffh8%40group.calendar.google.com/public/basic",
        className: 'gcal-event'           // an option!
//        currentTimezone: 'France/Paris' // an option!

    };

    $scope.events = [
        {title: 'All Day Event', start: new Date(y, m, 1)},
        {title: 'Long Event', start: new Date(y, m, d - 5), end: new Date(y, m, d - 2)},
        {id: 999, title: 'Repeating Event', start: new Date(y, m, d - 3, 16, 0), allDay: false, className: 'typEvent1'},
        {id: 999, title: 'Repeating Event', start: new Date(y, m, d + 4, 16, 0), allDay: false, className: 'typEvent2'},
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
        jsRoutes.controllers.Events.agenda().ajax({
            contentType: "application/json",
            dataType: "json",
            success: function (data, status) {
                // console.log("loadPlaces success", data, status);
                $scope.status = status;

                angular.forEach(data, function (item) {
//                    console.log(item);
                    var dtStart = new Date(item.dtEvent);
                    var dtEnd = moment(item.dtEvent).add("minutes", item.duration).toDate();

                    var event = {
                        id: item.id,
                        title: item.name,
                        start: dtStart,
                        end: dtEnd,
                        url: $filter('eventUrl')(item),
//                        className: 'typEvent'+item.typEventId ,
                        editable: false,
                        color: $filter('eventColor')(item.typEventId)
//                        textColor: $filter('eventTextColor')(item.typEventId)

                    };
//                    console.log(item, event);
                    $scope.events.push(event);
                });

                $scope.$digest();
            },
            error: function (data, status) {
                $scope.data = data || "Request failed";
                $scope.status = status;
                $scope.$digest();
            }
        });


    };

    $scope.loadEvents();
}