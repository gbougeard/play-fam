/**
 * User service, exposes user model to the rest of the app.
 */
define(["angular", "common"], function (angular) {
    var mod = angular.module("match.services", ["fam.common"]);
    mod.factory("matchService", ["$http", "$q", "playRoutes", function ($http, $q, playRoutes) {
        var PlaceService = function () {
            var self = this;
            self.loadPlace = function (id) {
//                console.log("matchService.loadPlace", id);
                return playRoutes.controllers.Places.jsonById(id).get();
            };

            self.findByZipcode = function (zipcode) {
                console.log("findByZipcode", zipcode);
                return playRoutes.controllers.Places.jsonLikeZipcode(zipcode).get();

            };

            self.findByCity = function (city) {
                console.log("findByCity", city);
                return playRoutes.controllers.Places.jsonLikeCity(city).get();
            };

            self.loadPlaces = function () {
                return playRoutes.controllers.Places.jsonList().get();
            };

            self.loadMapData = function () {
                return playRoutes.controllers.Places.gmapData().get();
            };

            self.create = function (match) {
                return playRoutes.controllers.Places.post(JSON.stringify(match));
            };

            self.update= function (match) {
                return playRoutes.controllers.Places.update(match.id).post(JSON.stringify(match));
            };

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
        return new PlaceService();
    }]);
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