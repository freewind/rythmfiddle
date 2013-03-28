'use strict';

/* Filters */

angular.module('myApp.filters', []).
    filter('interpolate', ['version', function (version) {
        return function (text) {
            return String(text).replace(/\%VERSION\%/mg, version);
        }
    }]).
    filter('codePretty', function () {
        return function (code) {
            // hack around incorrect tokenization
            var newV = code.replace('.done-true', 'doneTrue');
            newV = prettyPrintOne(newV, undefined, true);
            // hack around incorrect tokenization
            newV = newV.replace('doneTrue', '.done-true');
            console.log('----------------------------');
            console.log(newV);
            return newV;
        }
    });
