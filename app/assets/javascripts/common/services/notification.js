/** Helpers */
define(["angular", 'jquery', 'pnotify'], function(angular, $) {
    var mod = angular.module("common.notification", []);
    mod.service("notificationService", function() {
        return {
            notify: function(hash) {
                $.pnotify(hash);
            }
        };
    });
    return mod;
});