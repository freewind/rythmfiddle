'use strict';

/* Directives */


angular.module('myApp.directives', []).
    directive('appVersion', ['version', function (version) {
        return function (scope, elm, attrs) {
            elm.text(version);
        };
    }]).
    directive('autofocus', ['$timeout', function ($timeout) {
        return {
            restrict: 'A',
            link: function (scope, elm, attrs) {
                attrs.$observe('autofocus', function (val) {
                    if (val) {
                        $timeout(function () {
                            elm.focus();
                        }, 50);
                    }
                })
            }
        }
    }]);