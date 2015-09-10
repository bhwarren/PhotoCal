angular
  .module('example')
  .controller('confirmController', function($scope, supersonic) {
    $scope.confirm_event = function(){
        supersonic.ui.dialog.alert("added to calendar");
    };
    $scope.cancel_event = function(){
        supersonic.ui.dialog.alert("cancelled");
    };
  });
