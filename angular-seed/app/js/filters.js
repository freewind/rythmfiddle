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
            // checkout http://stackoverflow.com/questions/5499078/fastest-method-to-escape-html-tags-as-html-entities
            var tagsToReplace = {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;'
            };
            
            function replaceTag(tag) {
                return tagsToReplace[tag] || tag;
            }
            
            function safe_tags_replace(str) {
                return str ? str.replace(/[&<>]/g, replaceTag) : "";
            }
            
            return safe_tags_replace(str);
            
//            if (str) {
//                return str.replace(/[<]/g, "&lt;").replace(/[>]/g, "&gt;");
//            }
//            return str;
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
