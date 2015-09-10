angular
  .module('example')
  .controller('sharedController', function($scope, supersonic) {

      $scope.openMenu = function(){
          supersonic.ui.drawers.open("right");
      };

  });
