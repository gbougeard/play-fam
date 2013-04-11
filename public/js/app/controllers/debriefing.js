'use strict';


function DebriefingCtrl($scope, $http, $location) {

    $scope.idMatch = $location.absUrl().substring($location.absUrl().lastIndexOf('/') + 1);
    console.log($scope.idMatch);

    $scope.loadMatch = function(idMatch){
        jsRoutes.controllers.Matchs.jsonById(idMatch).ajax(
            {
                success: function (data, status) {
                    console.log(data, status);
                    $scope.loadEvent(data.eventId);
                },
                error: function (data, status) {
                    console.error(data, status);
                }
            }
        );
    };

    $scope.loadEvent = function(idEvent){
        jsRoutes.controllers.Events.jsonById(idEvent).ajax(
            {
                success: function (data, status) {
                    console.log(data, status);
                },
                error: function (data, status) {
                    console.error(data, status);
                }
            }
        );
    };

    $scope.loadMatch($scope.idMatch);
}