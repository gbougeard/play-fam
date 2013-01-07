'use strict';

testus.controller('WizStep2Controller',
    function ($scope, $location, releaseWizardSvc, localStorage) {

        $scope.stories = [];
        $scope.selStories = releaseWizardSvc.getStories();

        jsRoutes.controllers.Stories.findAll().ajax({
            data: {data: $scope.keywords},
            success: function (data, status) {
                $scope.status = status;
                $scope.data = data;
                $scope.stories = data;

                $scope.$digest();
            },
            error: function (data, status) {
                $scope.data = data || "Request failed";
                $scope.status = status;
                $scope.$digest();
            }
        });

        $scope.next = function () {

            var res = [];
            angular.forEach($scope.selStories, function (key, value) {
                if (key == true) {
                    var st = jQuery.grep($scope.stories, function (n) {
                        return n.id == value;
                    });
                    res.push(st[0]);
                }
            });
            releaseWizardSvc.setSelectedStories(res);
            $location.path('/wiz3');
        };

    });