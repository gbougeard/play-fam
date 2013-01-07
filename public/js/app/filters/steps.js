'use strict';

testus.filter('stepStatusRow', function () {
    var STATUS = {
        "OK" : "success",
        "KO" : "error",
        "PENDING" : "warning"
    };

    return function (status) {
        return STATUS[status];
    };
});
testus.filter('stepStatusLabel', function () {
    var STATUS = {
        "OK" : "success",
        "KO" : "important",
        "PENDING" : "warning"
    };

    return function (status) {
        return STATUS[status];
    };
});
testus.filter('notPending', function () {
    var STATUS = {
        "OK" : "disabled",
        "KO" : "disabled",
        "PENDING" : ""
    };

    return function (status) {
        return STATUS[status];
    };
});