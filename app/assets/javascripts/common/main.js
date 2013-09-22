/**
 * Common functionality.
 */
define(["angular", "./services/helper", "./services/playRoutes","./services/notification", "./filters", "./directives/example"],
    function(angular) {
        return angular.module("fam.common", ["common.helper", "common.playRoutes", "common.notification", "common.filters",
            "common.directives.example"]);
    });