/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 17/11/12
 * Time: 01:22
 * To change this template use File | Settings | File Templates.
 */
function TestCtrl($scope, $http) {

    console.log("TestCtrl");
    $scope.stories = [];
    $scope.selStories = [];
    $scope.browsers = ['IE', 'Chrome', 'Firefox'];
    $scope.selBrowsers = {};
    $scope.langs = ['FR', 'EN', 'DE', 'ES'];
    $scope.selLangs = {};
    $scope.selection = [];


    jsRoutes.controllers.Stories.findAll().ajax({
        data: {data: $scope.keywords},
        success: function (data, status) {
            $scope.status = status;
            $scope.data = data;
            $scope.stories = data;

            $scope.selection = {};
            angular.forEach($scope.stories, function (story) {
                $scope.selection[story.id] = {
                    story: story,
                    browsers: {},
                    languages: {}
                };
            });

            $scope.$digest();
        },
        error: function (data, status) {
            $scope.data = data || "Request failed";
            $scope.status = status;
            $scope.$digest();
        }
    });

    $scope.applyParameters = function () {
        console.log("applyParameters");

        angular.forEach($scope.selection, function (story) {
            var copiedObject = {};
            jQuery.extend(copiedObject, $scope.selBrowsers);
            story.browsers = copiedObject;
            copiedObject = {};
            jQuery.extend(copiedObject, $scope.selLangs);
            story.languages = copiedObject;
        });

//        $scope.$digest();
    };
}