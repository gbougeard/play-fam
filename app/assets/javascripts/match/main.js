/**
 * Place package module.
 * Manages all sub-modules so other RequireJS modules only have to import the package.
 */
define(["angular", "./routes", "./controllers", "./services"], function(angular, routes, controllers, services) {
    var mod = angular.module("fam.match", ["match.routes", "match.services"]);
    mod.controller("PlaceCtrl", controllers.PlaceCtrl);
    return mod;
});