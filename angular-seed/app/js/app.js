'use strict';

// Declare app level module which depends on filters, and services
angular.module('myApp', ['ui', 'ui.bootstrap', 'myApp.filters', 'myApp.services', 'myApp.directives']).
    config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
        $routeProvider.when('/editor', {
            templateUrl: '/templates/partials/editor.html',
            controller: EditorCtrl
        });
        $routeProvider.when('/all_demos', {
            templateUrl: '/templates/partials/all_demos.html',
            controller: AllDemoCtrl
        });
        $routeProvider.when('/editor/:id', {
            templateUrl: '/templates/partials/editor.html',
            controller: EditorCtrl
        });
        $routeProvider.when('/login', {
            templateUrl: '/templates/partials/login.html',
            controller: LoginCtrl
        });
        $routeProvider.when('/sample_model_code', {
            templateUrl: '/templates/partials/sample_model_code.html',
            controller: SampleModelCodeCtrl
        })
        $routeProvider.otherwise({redirectTo: '/editor'});
        // $locationProvider.html5Mode(true);
    }]).
    run(['$rootScope', '$http', function ($rootScope, $http) {
        // try to get logged user information when page refreshed
        if (!$rootScope.current) {
            $http.get('/api/Application/current').success(function (data) {
                $rootScope.current = data;
            });
        }
    }]);

angular.module('embedApp', ['ui', 'ui.bootstrap', 'myApp.filters', 'myApp.services', 'myApp.directives']).
    config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
        $routeProvider.when('/:id', {
            templateUrl: '/templates/partials/embed.html',
            controller: EditorCtrl
        });
        // $locationProvider.html5Mode(true);
    }]);
