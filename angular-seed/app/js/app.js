'use strict';

// Declare app level module which depends on filters, and services
angular.module('myApp', ['ui', 'ui.bootstrap', 'myApp.filters', 'myApp.services', 'myApp.directives']).
    config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
        $routeProvider.when('/index', {
            templateUrl: '/templates/partials/index.html',
            controller: IndexCtrl
        });
        $routeProvider.when('/editor', {
            templateUrl: '/templates/partials/editor.html',
            controller: EditorCtrl
        });
        $routeProvider.when('/editor/:id', {
            templateUrl: '/templates/partials/editor.html',
            controller: EditorCtrl
        });
        $routeProvider.when('/login', {
            templateUrl: '/templates/partials/login.html',
            controller: LoginCtrl
        })
        $routeProvider.otherwise({redirectTo: '/editor'});
        $locationProvider.html5Mode(true);
    }]);
