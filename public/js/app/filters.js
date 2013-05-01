'use strict';

//define(['webjars!angular.js'],
//    function() {

        /* Filters */

        angular.module('fam.filters', []).
            filter('interpolate', ['version', function(version) {
                return function(text) {
                    return String(text).replace(/\%VERSION\%/mg, version);
                }
            }]);

//    });