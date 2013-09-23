(function(requirejs) {
    "use strict";

    // -- PROD RequireJS config --
    requirejs.config({
        packages: ["common", "main", "place", "event"],
        shim: {
            "jquery": {
                exports: "$"
            },
            "angular" : {
                exports : "angular",
                deps: ["jquery"]
            },
            "angular-cookies": ["angular"],
            "angular-ui": ["angular"],
            "angular-strap": ["angular"],
//            "restangular": ["restangular"],
            'bootstrap': ["jquery"],
            'bootstrap-datepicker': ["jquery"],
            'bootstrap-timepicker': ["jquery"],
            // Nothing too special here, just remember to depend on jquery
            'pnotify': ["jquery"],
//            'moment': ["moment"],
            'select2': ["jquery"],
            'bootstrap-select': ["select2"],
            "jsRoutes" : {
                deps : [],
                // it's not a RequireJS module, so we have to tell it what var is returned
                exports : "jsRoutes"
            }
        },
        paths: {
            // Map the dependencies to CDNs or WebJars directly
            //"_" : "//cdnjs.cloudflare.com/ajax/libs/underscore.js/1.5.1/underscore-min",
            "jquery": "//code.jquery.com/jquery-1.10.2.min",
            "pnotify":"//cdnjs.cloudflare.com/ajax/libs/jquery-noty/2.0.3/jquery.noty",
            "angular": "//ajax.googleapis.com/ajax/libs/angularjs/1.1.5/angular.min",
            "angular-cookies": "//ajax.googleapis.com/ajax/libs/angularjs/1.1.5/angular-cookies.min",
            "angular-strap": "//cdnjs.cloudflare.com/ajax/libs/angular-strap/0.7.4/angular-strap.min",
            "angular-ui":"//cdnjs.cloudflare.com/ajax/libs/angular-ui/0.4.0/angular-ui.min",
            "moment": "//cdnjs.cloudflare.com/ajax/libs/moment.js/2.2.1/moment.min",
            "bootstrap": "//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min",
            "bootstrap-datepicker":"//cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.2.0/js/bootstrap-datepicker.min",
            "bootstrap-timepicker":"//mgcrea.github.io/angular-strap/vendor/bootstrap-timepicker",
            "bootstrap-select": "//mgcrea.github.io/angular-strap/vendor/bootstrap-select",
            "select2":"//cdnjs.cloudflare.com/ajax/libs/select2/3.4.1/select2.min",
            "fullCalendar" : "//cdnjs.cloudflare.com/ajax/libs/fullcalendar/1.6.4/fullcalendar.min",
            // TODO Replace with your server URL; this isn't perfect yet
            "jsRoutes": "//fam.gbougeard.net/jsroutes"
            // A WebJars URL would look like //server:port/webjars/angularjs/1.0.7/angular.min
        },
        priority: ["angular"]
    });

    requirejs.onError = function(err) {
        console.log(err);
    };

    // Make sure generic external scripts are loaded
    require(["angular"
        , "angular-cookies"
        , "angular-ui"
//        , "angular-strap"
        , "jquery"
//        , "pnotify"
        , "bootstrap"
        , "bootstrap-select"
        , "bootstrap-datepicker"
        , "bootstrap-timepicker"
        , "select2"
//        , "restangular"
        , "moment"
        , "app"
        ], function(angular, app) {
        angular.bootstrap(document, ["app"]);
    });
})(requirejs);