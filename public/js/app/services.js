'use strict';

//define(['webjars!angular.js'],
//    function() {

        /* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
        angular.module('fam.services', []).
            value('version', '0.1');

//    });