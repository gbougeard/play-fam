/** Common filters. */
define(["angular"], function(angular) {
    var mod = angular.module("common.filters", []);
    /**
     * Extracts a given property from the value it is applied to.
     * {{{
   * (user | property:'name')
   * }}}
     */
    mod.filter("property", function(value, property) {
        if (angular.isObject(value)) {
            if (value.hasOwnProperty(property)) {
                return value[property];
            }
        }
    });

    mod.filter('fromNow', function () {
        return function (dateString) {
            return moment(new Date(dateString)).fromNow();
        };
    });


    mod.filter('fromNowTimestamp', function () {
        return function (timestamp) {
            var day = moment(timestamp);
//            console.log(day);
            return moment(day).fromNow()
        };
    });

    mod.filter('typAnswer', function () {
        var STATUS = {
            "YES": "success",
            "NO": "important",
            "MAYBE": "info"
        };

        return function (status) {
            return STATUS[status.group];
        };
    });

    return mod;
});