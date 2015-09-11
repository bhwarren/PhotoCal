angular.module('example', [
  // Declare here all AngularJS dependencies that are shared by the example module.
  'supersonic'
]);

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
  .controller('calendarController', function($scope, supersonic) {

      $scope.confirm_event = function(){

          var eventObject = {
              eventname: $scope.eventname,
              location: $scope.location,
              from: $scope.from,
              until: $scope.until,
              description: $scope.description
          };

          //save the event locally for events page
          var events = localStorage.getItem('events');

          if(events === null){
              events = [];
          }
          else{
              events = JSON.parse(events);
          }

          events.push(eventObject);
          localStorage.setItem('events', JSON.stringify(events));
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
