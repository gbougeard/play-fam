'use strict';

fam.filter('range', function() {
    return function(input, total) {
        total = parseInt(total);
        for (var i=0; i<total; i++)
            input.push(i);
        return input;
    };
});

fam.controller('FormationCtrl', ["$scope",
    function($scope) {

        $scope.items = [];
        $scope.list1 = [];
        $scope.list2 = [];

        $scope.startCallback = function(event, ui) {
            console.log('You started draggin', event, ui);
        };

        $scope.stopCallback = function(event, ui) {
            console.log('Why did you stop draggin me?', event, ui);
        };

        $scope.dragCallback = function(event, ui) {
            console.log('hey, look I`m flying', event, ui);
        };

        $scope.dropCallback = function(event, ui) {
            var drop = $(this);
            var drag = $(ui.draggable[0]);
            console.log('hey, you dumped me :-(', event, ui, drop, drag);
        };

        $scope.overCallback = function(event, ui) {
            console.log('Look, I`m over you', event, ui);
        };

        $scope.outCallback = function(event, ui) {
            console.log('I`m not, hehe', event, ui);
        };

    }]);