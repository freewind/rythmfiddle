'use strict';

/* Filters */

angular.module('myApp.filters', []).
    filter('interpolate', ['version', function (version) {
        return function (text) {
            return String(text).replace(/\%VERSION\%/mg, version);
        }
    }]).
    filter('codePretty', function () {
        return function (code, showLineNums) {
            // hack around incorrect tokenization
            var newV = code.replace('.done-true', 'doneTrue');
            newV = prettyPrintOne(newV, undefined, showLineNums);
            // hack around incorrect tokenization
            newV = newV.replace('doneTrue', '.done-true');
            return newV;
        }
    });
