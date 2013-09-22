(function (requirejs) {
    "use strict";

    // -- DEV RequireJS config --
    requirejs.config({
        // Packages = top-level folders; loads a contained file named "main.js"
        packages: ["common", "main", "place", "event"],
        shim: {
            "jquery": {
                exports: "$"
            },
            "angular": {
                exports: "angular",
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
            "app": ["angular"]
        },
        paths: {
            // You can also define a module here, e.g. a local module that doesn't support RequireJS
            // or map a longer path to a shorter name
            "moment": "//cdnjs.cloudflare.com/ajax/libs/moment.js/2.2.1/moment.min"
            ,"bootstrap-select": "https://mgcrea.github.io/angular-strap/vendor/bootstrap-select"
//            "restangular":"//cdnjs.cloudflare.com/ajax/libs/restangular/0.5.1/restangular.min"
        },
        priority: ["angular"]
    });

    requirejs.onError = function (err) {
        console.log(err);
    };

    /*
     * Custom naming for WebJars.
     * Define all WebJars deps with a generic name, so they can be referenced transparently
     * This is important so the mainProd file stays as simple as possible.
     */

    define("_", ["webjars!underscore.js"], function () {
        return _;
    });
    define("jquery", ["webjars!jquery.js"], function () {
        return $;
    });
    define("pnotify", ["webjars!jquery.pnotify.js"], function () {
//        return $;
    });
    define("bootstrap", ["webjars!bootstrap.js"], function () {/*return $;*/
    });
    define("bootstrap-datepicker", ["webjars!bootstrap-datepicker.js"], function () {/*return $;*/
    });
    define("bootstrap-timepicker", ["webjars!bootstrap-timepicker.js"], function () {/*return $;*/
    });
    define("angular", ["webjars!angular.js"], function () {
        return angular; // return the global var
    });
    define("angular-cookies", ["webjars!angular-cookies.js"], function () {
    });
    define("angular-ui", ["webjars!angular-ui.js"], function () {
    });
    define("angular-strap", ["webjars!angular-strap.js"], function () {
    });
//    define("restangular", ["webjars!restangular.js"], function () {
//    });
//    define("moment", ["webjars!moment.js"], function (moment) {
//        moment().format();
//    });
    define("select2", ["webjars!select2.js"], function () {
    });

    // External Dependency that is not a RequireJS module
    define("jsRoutes", ["/jsroutes.js"], function () {
        return jsRoutes;
    });

    // Load the app. This is kept minimal so it doesn't need much updating.
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
        , "app"],
        function (angular, cookies, app) {
            angular.bootstrap(document, ["app"]);
//            angular.bootstrap(document.getElementById("map"), ['app.ui-map']);

        }
    );
})(requirejs);