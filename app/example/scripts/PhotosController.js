angular
  .module('example')
  .controller('PhotosController', function($scope, supersonic) {

    $scope.getCamera = function(){
        var options = {
          quality: 50,
          allowEdit: false,
          //targetWidth: 300,
          //targetHeight: 300,
          encodingType: "png",
          saveToPhotoAlbum: true
        };

        supersonic.media.camera.takePicture(options).then( function(result){
            supersonic.ui.modal.show("example#confirm_modal");

        });
    };

    $scope.confirm = function(){
        supersonic.ui.modal.show("example#confirm_modal");
    };



  });
