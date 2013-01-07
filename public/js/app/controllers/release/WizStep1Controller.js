'use strict';

testus.controller('WizStep1Controller',
    function ($scope, $location, releaseWizardSvc) {
        $scope.release = releaseWizardSvc.getRelease();

        $scope.next = function () {
            console.log("step1 - next");
            releaseWizardSvc.setRelease($scope.release);
            $location.path('/wiz2');
        };

        $scope.resetData = function(){
            releaseWizardSvc.resetStorage();

        }
    });