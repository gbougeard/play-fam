'use strict';

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 17/11/12
 * Time: 01:22
 * To change this template use File | Settings | File Templates.
 */
function AgendaCtrl($scope, $http) {

    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

    $scope.eventSource = {
        url: "http://www.google.com/calendar/feeds/usa__en%40holiday.calendar.google.com/public/basic",
        className: 'gcal-event',           // an option!
//        currentTimezone: 'France/Paris' // an option!
    };

    $scope.events = [
        {title: 'All Day Event', start: new Date(y, m, 1)},
        {title: 'Long Event', start: new Date(y, m, d - 5), end: new Date(y, m, d - 2)},
        {id: 999, title: 'Repeating Event', start: new Date(y, m, d - 3, 16, 0), allDay: false},
        {id: 999, title: 'Repeating Event', start: new Date(y, m, d + 4, 16, 0), allDay: false},
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
    }

    $scope.remove = function (index) {
        $scope.events.splice(index, 1);
    }

    // The function that will be executed on button click (ng-click="search()")
    $scope.loadEvents = function () {
        // Create the http post request
        // the data holds the keywords
        // The request is a JSON request.
        jsRoutes.controllers.Events.eventsData().ajax({
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
                        url:  '/events/'+item.id
                    };
//                    console.log(event);
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