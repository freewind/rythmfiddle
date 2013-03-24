'use strict';

/* Controllers */


function BodyCtrl($scope, $dialog) {
    $scope.showAbout = showAbout;
    function showAbout() {
        $dialog.dialog().open('/templates/partials/about.html', AboutEditor);
    }
}
BodyCtrl.$inject = ['$scope', '$dialog'];

function EditorCtrl($scope, $http, $routeParams, $dialog, $location) {
    $scope.codes = [];
    $scope.currentCode = {
        id: $routeParams.id,
        desc: null,
        params: null,
        files: [
            {
                filename: "main.html",
                source: "",
                isMain: true,
                editing: false
            }
        ]
    };
    $scope.result = {
        error: '',
        renderedCode: ''
    }
    $scope.currentFile = $scope.currentCode.files[0];
    $scope.highlightRun = false;

    $scope.run = run;
    $scope.save = save;
    $scope.removeCurrentCode = removeCurrentCode;
    $scope.newFile = newFile;
    $scope.saveFileName = saveFileName;
    $scope.switchFile = switchFile;
    $scope.removeFile = removeFile;
    $scope.setMain = setMain;
    $scope.editFileName = editFileName;
    $scope.cancelEditFileName = cancelEditFileName;

    $scope.$watch("currentCode", function (newVal, oldVal) {
        if (newVal == oldVal) {
            $scope.highlightRun = false;
        } else {
            $scope.highlightRun = true;
        }
    }, true);

    loadCodeList();
    if ($scope.currentCode.id) {
        $http.get('/api/Application/load?id=' + $scope.currentCode.id).success(function (data) {
            $scope.currentCode = data;
            $scope.currentFile = data.files[0];
            $scope.run();
        });
    }

    function setMain(file) {
        _.each($scope.currentCode.files, function (file) {
            file.isMain = false;
        });
        file.isMain = true;
    }

    function switchFile(file) {
        $scope.currentFile = file;
    }

    function removeFile(file) {
        if (confirm('Delete this template?')) {
            $scope.currentCode.files = _.without($scope.currentCode.files, file);
            if ($scope.currentCode.files.length == 0) {
                $scope.currentFile = null;
            }
        }
    }

    // 1. not null
    // 2. unique
    function saveFileName(file) {
        var editName = file.editName;
        if (!editName) {
            alert('filename should not be empty');
        } else {
            if (editName !== file.filename) {
                var exist = _.find($scope.currentCode.files, function (item) {
                    return item.filename === editName || item.filename.indexOf(editName + ".") === 0;
                });
                if (exist) {
                    alert('Duplicate filename or prefix, please use another one.');
                    return;
                }
            }
            file.filename = editName;
            file.editing = false;
        }
    }

    function editFileName(file) {
        file.editName = file.filename;
        file.editing = true;
    }

    function cancelEditFileName(file) {
        if (!file.filename) {
            alert("Original filename is empty, you can't cancel");
            return;
        }
        file.editName = null;
        file.editing = false;
    }

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
            $scope.highlightRun = false;
        });
    }

    function newFile() {
        var oriCount = $scope.currentCode.files.length;
        $scope.currentFile = {
            filename: '',
            source: '',
            isMain: oriCount === 0,
            editing: true
        };
        $scope.currentCode.files.push($scope.currentFile);
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

function AboutEditor($scope, dialog) {
    $scope.close = function () {
        dialog.close();
    }
}
AboutEditor.$inject = ['$scope', 'dialog'];