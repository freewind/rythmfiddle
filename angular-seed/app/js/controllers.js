'use strict';

/* Controllers */


function BodyCtrl($scope, $dialog, $location, $http) {
    $scope.siteInfo = {};
    $scope.showAbout = showAbout;
    $scope.currentPath = currentPath;

    getSiteInfo();

    function showAbout() {
        $dialog.dialog().open('/templates/partials/about.html', AboutEditor);
    }

    function currentPath() {
        return $location.path();
    }

    function getSiteInfo() {
        $http.get('/api/Application/siteInfo').success(function (data) {
            $scope.siteInfo = data;
        });
    }
}
BodyCtrl.$inject = ['$scope', '$dialog', '$location', '$http'];

function EditorCtrl($scope, $http, $routeParams, $dialog, $location, $timeout) {
    $scope.codes = [];
    $scope.currentCode = {
        id: $routeParams.id,
        desc: null,
        params: null,
        showInMenu: false,
        files: [
            {
                filename: "main.html",
                source: "",
                isMain: true,
                editing: false,
                active: true
            }
        ]
    };
    $scope.result = {
        error: '',
        renderedCode: ''
    }
    $scope.highlightRun = false;
    $scope.resultPageActive = false;
    $scope.running = false;

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
    $scope.getActiveFile = getActiveFile;
    $scope.getMainFile = getMainFile;


    $scope.$watch("currentCode", function (newVal, oldVal) {
        if (newVal == oldVal) {
            $scope.highlightRun = false;
        } else {
            $scope.highlightRun = true;
        }
    }, true);

    $scope.$watch("resultPageActive", function (val) {
        if (val) {
            run();
        }
    });

    loadCodeList();
    if ($scope.currentCode.id) {
        $http.get('/api/Application/load?id=' + $scope.currentCode.id).success(function (data) {
            $scope.currentCode = data;
            getMainFile().active = true;
        });
    }

    function getMainFile() {
        return _.find($scope.currentCode.files, function (file) {
            return file.isMain === true;
        });
    }

    function getActiveFile() {
        return _.find($scope.currentCode.files, function (f) {
            return f.active === true;
        })
    }

    function setMain(file) {
        _.each($scope.currentCode.files, function (file) {
            file.isMain = false;
        });
        file.isMain = true;
    }

    function switchFile(file) {
        _.each($scope.currentCode.files, function (f) {
            f.active = false;
        });
        file.active = true;
    }

    function removeFile(file, noAlert) {
        if ($scope.currentCode.files.length > 1) {
            if (noAlert || confirm('Delete this template?')) {
                $scope.currentCode.files = _.without($scope.currentCode.files, file);
                if (!getMainFile()) {
                    setMain($scope.currentCode.files[0]);
                }
                if (!getActiveFile()) {
                    switchFile($scope.currentCode.files[0]);
                }
            }
        } else {
            alert("Can't delete the last one template");
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
        file.editing = !file.editing;
    }

    function cancelEditFileName(file) {
        if (!file.filename) {
            removeFile(file, true);
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
        $http.get('/api/Application/list?showInMenu=true').success(function (data) {
            $scope.codes = data;
        });
    }

    function save() {
        var d = $dialog.dialog({
            resolve: {
                code: function () {
                    return $scope.currentCode;
                }
            }
        });
        d.open('/templates/partials/save_dialog.html', SaveDialogCtrl).then(function (code) {
            if (code) {
                $scope.editorForm.codeSource.$dirty = false;
                loadCodeList();
                $location.path('/editor/' + code.id);
            }
        });
    }

    function run() {
        if ($scope.currentCode.files.length > 0) {
            $scope.running = true;
            $scope.highlightRun = false;
            $scope.result = {
                renderedCode: 'running on server ...'
            };
            var start = new Date().getTime();
            $http.post('/api/Application/run', $scope.currentCode).success(function (data) {
                var waitMore = new Date().getTime() - start;
                if (waitMore < 600) waitMore = 600;
                $timeout(function () {
                    $scope.result = data;
                    $scope.running = false;
                }, waitMore);
            });
        }
    }


    function newFile() {
        var newOne = {
            filename: '',
            source: '',
            isMain: false,
            editing: true,
            active: true
        };
        $scope.currentCode.files.push(newOne);
        switchFile(newOne);
        if (!getMainFile()) {
            newOne.isMain = true;
        }
    }

}
EditorCtrl.$inject = ['$scope', '$http', '$routeParams', '$dialog', '$location', '$timeout'];

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

function AllDemoCtrl($scope, $http) {
    $scope.allDemos = [];
    loadAll();

    function loadAll() {
        $http.get('/api/Application/list').success(function (data) {
            $scope.allDemos = data;
        })
    }
}
AllDemoCtrl.$inject = ['$scope', '$http']