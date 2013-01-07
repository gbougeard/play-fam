'use strict';

testus.controller('WizStep4Controller',
    function ($scope, $location, releaseWizardSvc) {
        $scope.campaigns = releaseWizardSvc.getCampaigns();

        $scope.submit = function () {
            console.log("step4 - submit");
            releaseWizardSvc.submit();
        };

    });