/**
 * User service, exposes user model to the rest of the app.
 */
define(["angular", "common"], function (angular) {
    var mod = angular.module("event.services", ["fam.common"]);
    mod.factory("eventService", ["$http", "$q", "playRoutes", function ($http, $q, playRoutes) {
        var EventService = function () {
            var self = this;

            self.loadTypEvents = function () {
                return playRoutes.controllers.TypEvents.jsonList().get();
            };

            self.loadEventStatuses = function () {
                return playRoutes.controllers.EventStatuses.jsonList().get();
            };

            self.create = function (event) {
                return playRoutes.controllers.Events.post(JSON.stringify(event));
            };

            self.update = function (event) {
                console.log("update", event);
                return playRoutes.controllers.Events.update(event.event.id).post(JSON.stringify(event.event));
            };

            self.saveTeams = function (id, event) {
                var c = [];
                angular.forEach(event.teams, function (team) {
                    c.push({
                        eventId: event.event.id,
                        teamId: team
                    });
                });

                console.log("teams", c);
                return playRoutes.controllers.Events.saveTeams().post(JSON.stringify(c));
            }

//            var user;
//            var token;
//            self.loginUser = function(credentials) {
//                return playRoutes.controllers.Application.login().post(credentials).then(function(response) {
//                    // return promise so we can chain easily
//                    this.token = response.data.token;
//                    // in a real app we could use the token to fetch the user data
//                    return playRoutes.controllers.Users.user(3).get();
//                }).then(function(response) {
//                        var user = response.data; // Extract user data from user() request
//                        user.email = credentials.email;
//                        self.user = user;
//                        return user;
//                    });
//            };
//            self.logout = function() {
//                // Logout on server in a real app
//                self.user = undefined;
//            };
//            self.getUser = function() {
//                return self.user;
//            };
        };
        return new EventService();
    }
    ])
    ;
    /**
     * Add this object to a route definition to only allow resolving the route if the user is
     * logged in.
     */
//    mod.constant("userResolve", {
//        checkAuth: ["$q", "userService", function($q, userService) {
//            var deferred = $q.defer();
//            if (userService.getUser()) {
//                deferred.resolve();
//            } else {
//                deferred.reject();
//            }
//            return deferred.promise;
//        }]
//    });
    /**
     * If the current route does not resolve, go back to the start page.
     */
    var handleRouteError = function ($rootScope, $location) {
        $rootScope.$on("$routeChangeError", function (e, next, current) {
            $location.path("/");
        });
    };
    handleRouteError.$inject = ["$rootScope", "$location"];
    mod.run(handleRouteError);
    return mod;
});