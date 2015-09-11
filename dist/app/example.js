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

      $scope.events = (localStorage.getItem('events') === null) ? [] : JSON.parse(localStorage.getItem('events'));

      $scope.confirm_event = function(){
          if (typeof $scope.eventname == 'undefined' || typeof $scope.from == 'undefined'){
              supersonic.ui.dialog.alert("Make sure to specify the event name or starting time. Event not saved.");
              return;
          }

          var eventObject = {
              eventname: $scope.eventname,
              location: $scope.location,
              from: $scope.from,
              until: $scope.until,
              description: $scope.description
          };

          $scope.events.push(eventObject);
          localStorage.setItem('events', JSON.stringify($scope.events));

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
