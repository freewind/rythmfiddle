'use strict';

/* Controllers */


function MyCtrl1() {
}
MyCtrl1.$inject = [];


function MyCtrl2() {
}
MyCtrl2.$inject = [];

function IndexCtrl($scope) {

}
IndexCtrl.$inject = ['$scope'];

function EditorCtrl($scope, $http, $routeParams, $dialog, $location) {
    $scope.currentCode = {
        id: $routeParams.id
    };
    $scope.codes = [];
    $scope.result = {
        htmlCode: null,
        javaCode: null
    };
    $scope.run = run;
    $scope.save = save;
    $scope.removeCurrentCode = removeCurrentCode;

    loadCodeList();

    function removeCurrentCode() {
        if (confirm('Are you sure to delete current demo')) {
            $http.post('/api/Application/delete', {
                id: $scope.currentCode.id
            }).success(function () {
                    $location.path('/editor');
                });
        }
    }

    function loadCodeList() {
        $http.get('/api/Application/list').success(function (data) {
            $scope.codes = data;
        });
    }

    if ($scope.currentCode.id) {
        $http.get('/api/Application/load?id=' + $scope.currentCode.id).success(function (data) {
            $scope.currentCode = data;
            $scope.run();
        });
    }

    function save() {
        var d = $dialog.dialog({
            resolve: {
                code: $scope.currentCode
            }
        });
        d.open('/templates/partials/save_dialog.html', SaveDialogCtrl).then(function (code) {
            $scope.editorForm.codeSource.$dirty = false;
            loadCodeList();
            $location.path('/editor/' + code.id);
        });
    }

    function run() {
        $http.post('/api/Application/run', $scope.currentCode).success(function (data) {
            $scope.result = data;
        });
    }
}
EditorCtrl.$inject = ['$scope', '$http', '$routeParams', '$dialog', '$location'];

function SaveDialogCtrl($scope, dialog, code, $http) {
    $scope.code = code;
    $scope.close = function () {
        dialog.close();
    }
    $scope.save = function () {
        $http.post('/api/Application/save', code).success(function (code) {
            dialog.close(code);
        }).error(function (data) {
                alert(data);
            });
    }
}

SaveDialogCtrl.$inject = ['$scope', 'dialog', 'code', '$http'];

function LoginCtrl($rootScope, $scope, $location, $http) {
    $scope.username = null;
    $scope.password = null;
    $scope.submit = submit;

    function submit() {
        $http.post('/api/Application/login', {
            username: $scope.username,
            password: $scope.password
        }).success(function (data) {
                if (data.error) {
                    alert(data.error);
                } else {
                    $rootScope.current = $scope.username;
                    $location.path("/editor");
                }
            });
    }
}
LoginCtrl.$inject = ['$rootScope', '$scope', '$location', '$http'];