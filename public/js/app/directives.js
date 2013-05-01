'use strict';

//define(['webjars!angular.js'],
//    function() {

        /* Directives */


        angular.module('fam.directives', []).
            directive('appVersion', ['version', function(version) {
                return function(scope, elm, attrs) {
                    elm.text(version);
                };
            }]);

//    });