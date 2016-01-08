var app = angular.module('app', ['ngTouch', 'ui.grid']);

app.controller('MainCtrl', ['$scope', function ($scope) {

    $scope.myData = [
        {
            "firstName": "Cox",
            "lastName": "Carney",
            "company": "Enormo",
            "employed": true,
            "address": {
                "city": "hyd",
                "country": "india"
            }
        },
        {
            "firstName": "Lorraine",
            "lastName": "Wise",
            "company": "Comveyer",
            "employed": false,
            "address": {
                "city": "hyd",
                "country": "india"
            }
        },
        {
            "firstName": "Nancy",
            "lastName": "Waters",
            "company": "Fuelton",
            "employed": false,
            "address": {
                "city": "hyd",
                "country": "india"
            }
        }
    ];
}]);
