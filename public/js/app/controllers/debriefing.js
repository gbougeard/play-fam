'use strict';


function DebriefingCtrl($scope, $http, $location) {

    $scope.idMatch = $location.absUrl().substring($location.absUrl().lastIndexOf('/') + 1);
    console.log($scope.idMatch);


}