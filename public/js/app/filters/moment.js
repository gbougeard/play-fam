'use strict';

testus.filter('fromNow', function () {
    return function (dateString) {
        return moment(new Date(dateString)).fromNow();
    };
});


testus.filter('fromNowTimestamp', function () {
    return function (timestamp) {
        var day = moment(timestamp);
//            console.log(day);
        return moment(day).fromNow()
    };
});