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
          encodingType: "png",
          saveToPhotoAlbum: true
        };

        supersonic.media.camera.takePicture(options).then( function(result){
            supersonic.ui.modal.show("example#confirm_modal");
        });
    };

    $scope.confirm = function(){
        var message = {
            eventname: "Halloween Party",
            location: "666 Elm Street",
            from: "10pm",
            until: "3am",
            description: "a party so good, you'll dream about it"
        };

        supersonic.data.channel('newEvent').publish(message);
    };

  });

angular
    .module('example')
    .controller('calendarController', function($scope, supersonic) {
        $scope.eventname = "adsafdddddddd";
        supersonic.bind($scope,"eventname");

         function setConfirmVars(message) {
             supersonic.logger.error("previously set to: "+$scope.eventname);

            $scope.eventname = message.eventname;
            $scope.from = message.from;
            supersonic.logger.error("set successfully to: "+$scope.eventname);
            supersonic.ui.modal.show("example#confirm_modal");
            supersonic.logger.error("after showing modal");

        }
        supersonic.data.channel('newEvent').subscribe( setConfirmVars );

        $scope.events = (localStorage.getItem('events') === null) ? [] : JSON.parse(localStorage.getItem('events'));

        $scope.removeEvent = function(removeObject){
            var index = $scope.events.indexOf(removeObject);
            if(index != -1){
                $scope.events.splice(index,1);
                localStorage.setItem('events', JSON.stringify($scope.events));
            }
        };

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
            supersonic.logger.error("scope:"+$scope.eventname);

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
