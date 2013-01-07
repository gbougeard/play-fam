function dndCtrl($scope) {

    $scope.model = [
        {
            "id": 1,
            "value": "Who the fuck is Arthur Digby Sellers?"
        },
        {
            "id": 2,
            "value": "I've seen a lot of spinals, Dude, and this guy is a fake. "
        },
        {
            "id": 3,
            "value": "But that is up to little Larry here. Isn't it, Larry?"
        },
        {
            "id": 4,
            "value": " I did not watch my buddies die face down in the mud so that this fucking strumpet."
        }
    ];

    $scope.source = [
        {
            "id": 5,
            "value": "What do you mean \"brought it bowling\"? I didn't rent it shoes."
        },
        {
            "id": 6,
            "value": "Keep your ugly fucking goldbricking ass out of my beach community! "
        },
        {
            "id": 7,
            "value": "What the fuck are you talking about? I converted when I married Cynthia!"
        },
        {
            "id": 8,
            "value": "Ja, it seems you forgot our little deal, Lebowski."
        }
    ];

    // watch, use 'true' to also receive updates when values
    // change, instead of just the reference
    $scope.$watch("model", function (value) {
        console.log("Model: " + value.map(function (e) {
            return e.id
        }).join(','));
    }, true);

    // watch, use 'true' to also receive updates when values
    // change, instead of just the reference
    $scope.$watch("source", function (value) {
        console.log("Source: " + value.map(function (e) {
            return e.id
        }).join(','));
    }, true);

    $scope.sourceEmpty = function() {
        return $scope.source.length == 0;
    }

    $scope.modelEmpty = function() {
        return $scope.model.length == 0;
    }
}