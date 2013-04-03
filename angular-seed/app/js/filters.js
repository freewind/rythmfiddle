'use strict';

/* Filters */

angular.module('myApp.filters', []).
    filter('interpolate', ['version', function (version) {
        return function (text) {
            return String(text).replace(/\%VERSION\%/mg, version);
        }
    }]).
    filter('codePretty', function () {
        function escape(str) {
            if (str) {
                return str.replace(/[&]/g, "&amp;").replace(/[<]/g, "&lt;").replace(/[>]/g, "&gt;");
            }
            return str;
        }

        return function (code, showLineNums) {
            // hack around incorrect tokenization
            var newV = escape(code).replace('.done-true', 'doneTrue');
            newV = prettyPrintOne(newV, undefined, showLineNums);
            // hack around incorrect tokenization
            newV = newV.replace('doneTrue', '.done-true');
            return newV;
        }
    });
