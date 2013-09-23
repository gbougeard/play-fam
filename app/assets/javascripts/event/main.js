/**
 * Event package module.
 * Manages all sub-modules so other RequireJS modules only have to import the package.
 */
define(["angular", "./routes", "./controllers", "./services", "./filters"], function(angular, routes, controllers, services, filters) {
    var mod = angular.module("fam.event", ["event.routes", "event.services", "event.filters"]);
    mod.controller("EventCtrl", controllers.EventCtrl);
    mod.controller("AgendaCtrl", controllers.AgendaCtrl);
    return mod;
});