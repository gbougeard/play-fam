/** Common filters. */
define(["angular"], function(angular) {
    var mod = angular.module("event.filters", []);
    /**
     * Extracts a given property from the value it is applied to.
     * {{{
   * (user | property:'name')
   * }}}
     */
    mod.filter('eventBkgColor', function () {
        var CSS = {
            1: "grey",
            2: "green",
            3: "orange"
        };

        return function (id) {
            return CSS[id];
        };
    });
    mod.filter('eventColor', function () {
        var CSS = {
            1: "Gray",
            2: "CadetBlue",
            3: "orange"
        };

        return function (id) {
            return CSS[id];
        };
    });
    mod.filter('eventTextColor', function () {
        var CSS = {
            1: "black",
            2: "grey",
            3: "white"
        };

        return function (id) {
            return CSS[id];
        };
    });
    mod.filter('eventBorderColor', function () {
        var CSS = {
            1: "red",
            2: "green",
            3: "orange"
        };

        return function (id) {
            return CSS[id];
        };
    });

    mod.filter('eventUrl', function () {

        return function (data) {
//        console.log(data);
            var url = '';
            switch(data.typEventId){
                case  1 : // Workout
                    url += '/workouts/';
                    break;
                case  2 : // Match
                    url += '/matchs/' ;
                    break;
                default:
                    url += '/events/';
                    break;
            }
            switch(data.eventStatusId){
                case  27 : // Fini
                    url += 'event/';
                    break;
                default:
                    break;
            }
            return url + data.id
        };
    });

    return mod;
});