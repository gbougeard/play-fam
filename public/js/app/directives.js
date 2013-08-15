'use strict';

//define(['webjars!angular.js'],
//    function() {

        /* Directives */


        angular.module('fam.directives', []).
//            directive('appVersion', ['version', function(version) {
//                return function(scope, elm, attrs) {
//                    elm.text(version);
//                };
//            }])
    directive('editInPlace', function () {
        return {
            restrict: 'E',
            scope: { value: '=' },
            template: //                        '<span ng-click="edit()" ng-bind="value"></span><input ng-model="value"></input>'
                '<span class="span1"><button><i class="icon-edit" ng-click="edit()" ng-bind="value"></i></button></span>' +
                    '<span class="span1"><span class="badge">{{value.matchplayer.num}}</span></span>' +
                    '<span class="span8">{{value.player.firstName}} {{value.player.lastName}} </span>' +
                    '<span class="span1"><span class="label">{{value.matchplayer.note}} </span></span>' +
                    '<span class="span1">{{value.matchplayer.timePlayed}}</span> ',


            link: function ( $scope, element, attrs ) {
                        // Let's get a reference to the input element, as we'll want to reference it.
                        var inputElement = angular.element( element.children()[1] );

                        // This directive should have a set class so we can style it.
                        element.addClass( 'edit-in-place' );

                        // Initially, we're not editing.
                        $scope.editing = false;

                        // ng-click handler to activate edit-in-place
                        $scope.edit = function () {
                            $scope.editing = true;

                            // We control display through a class on the directive itself. See the CSS.
                            element.addClass( 'active' );

                            // And we must focus the element.
                            // `angular.element()` provides a chainable array, like jQuery so to access a native DOM function,
                            // we have to reference the first element in the array.
                            inputElement[0].focus();
                        };

                        // When we leave the input, we're done editing.
                        inputElement.prop( 'onblur', function() {
                            $scope.editing = false;
                            element.removeClass( 'active' );
                        });
                    }
                };
            });


//    });