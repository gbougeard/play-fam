/**
 * Place package module.
 * Manages all sub-modules so other RequireJS modules only have to import the package.
 */
define(["angular", "./routes", "./controllers", "./services"], function(angular, routes, controllers, services) {
    var mod = angular.module("fam.place", ["place.routes", "place.services"]);
    mod.controller("PlaceCtrl", controllers.PlaceCtrl);
    return mod;
});