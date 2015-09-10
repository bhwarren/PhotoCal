angular.module('example', [
  // Declare here all AngularJS dependencies that are shared by the example module.
  'supersonic'
]);

angular
  .module('example')
  .controller('LearnMoreController', function($scope, supersonic) {

    $scope.navbarTitle = "Learn More";

  });

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

angular
  .module('example')
  .controller('SettingsController', function($scope, supersonic) {
    $scope.navbarTitle = "Settings";
  });

angular
  .module('example')
  .controller('calendarController', function($scope, supersonic) {


      $scope.confirm_event = function(eventObject){
          //save the event locally for events page
          var events = localStorage.getItem('events').parse();
          if(!events){
              events = {};
          }
          events.append(eventObject);
          localStorage.setItem('events', JSON.stringify(events));

          //add the event to calendar
          supersonic.ui.dialog.alert("event saved and added to calendar");
      };

      $scope.cancel_event = function(){
          supersonic.ui.dialog.alert("cancelled");
      };

    $scope.openCalendar = function(){
        supersonic.ui.drawers.close();
        supersonic.device.platform().then( function(platform) {
             if(platform.name == "Android"){
                 supersonic.ui.dialog.alert("need a plugin to open 4 android, sorry");
             }
             else{
                 supersonic.app.openURL("calshow://");
             }
         });

    };
 });

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

angular
  .module('example')
  .controller('sharedController', function($scope, supersonic) {

      $scope.openMenu = function(){
          supersonic.ui.drawers.open("right");
      };

  });
