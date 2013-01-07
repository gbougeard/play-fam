'use strict';

testus.controller('WizStep3Controller',
    function($scope, $location, releaseWizardSvc) {
        $scope.selection = releaseWizardSvc.getSelection();

        $scope.selBrowsers = releaseWizardSvc.getBrowsers();
        $scope.selLangs = releaseWizardSvc.getLangs();

        $scope.browsers = ['IE', 'Chrome', 'Firefox'];
        $scope.langs = ['FR', 'EN', 'DE', 'ES'];

        $scope.applyParameters = function () {
            console.log("applyParameters");

            angular.forEach($scope.selection, function (story) {
                story.browsers = angular.copy($scope.selBrowsers);
                story.languages = angular.copy($scope.selLangs);
            });

//        $scope.$digest();
        };

        $scope.next = function () {
            console.log("step3 - next");
            releaseWizardSvc.setSelection($scope.selection);
            $location.path('/wiz4');
        };

    });