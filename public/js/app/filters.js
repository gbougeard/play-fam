'use strict';

//define(['webjars!angular.js'],
//    function() {

/* Filters */

angular.module('fam.filters', []).
   filter('typAnswer', function () {
    var STATUS = {
        "YES": "success",
        "NO": "important",
        "MAYBE": "info"
    };

    return function (status) {
        return STATUS[status.group];
    };
});

//    });