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
            var message = {
                eventname: "Halloween Party222",
                location: "666 Elm Street",
                from: "10pm",
                until: "3am",
                description: "a party so good, you'll dream about it"
            };

            localStorage.setItem('last_new_event', JSON.stringify(message));
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

        localStorage.setItem('last_new_event', JSON.stringify(message));
        supersonic.ui.modal.show("example#confirm_modal");
    };
    //localStorage.clear();

  });

angular
    .module('example')
    .controller('calendarController', function($scope, supersonic) {

        $scope.eventname = "calendarview";

        $scope.events = (localStorage.getItem('events') === null) ? [] : JSON.parse(localStorage.getItem('events'));

        $scope.removeEvent = function(removeObject){
            var index = $scope.events.indexOf(removeObject);
            if(index != -1){
                $scope.events.splice(index,1);
                localStorage.setItem('events', JSON.stringify($scope.events));
            }
        };

        supersonic.data.channel('confirmedEvent').subscribe( function(message) {
            supersonic.logger.error("received a message " + JSON.stringify(message));

            supersonic.logger.error("before adding:"+JSON.stringify($scope.events));

            $scope.events.push(message);
            supersonic.logger.error("after adding:"+JSON.stringify($scope.events));

            localStorage.setItem('events', JSON.stringify($scope.events));

        });

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

        //first thing to do when the confirm modal shows is set the fields for display
        (function(){
            var last_new_event = JSON.parse(localStorage.getItem('last_new_event'));

            $scope.eventname = last_new_event.eventname;
            $scope.location = last_new_event.location;
            $scope.from = last_new_event.from;
            $scope.until = last_new_event.until;
            $scope.description = last_new_event.description;

            supersonic.logger.error("set successfully to: "+$scope.eventname);
            supersonic.logger.error("after showing modal");

       })();

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

            //$scope.events.push(eventObject);
            //localStorage.setItem('events', JSON.stringify($scope.events));
            supersonic.data.channel('confirmedEvent').publish(eventObject);

            supersonic.ui.dialog.alert("event saved and added to calendar");
        };

       $scope.cancel_event = function(){
           supersonic.logger.error("scope:"+$scope.eventname);

            supersonic.ui.dialog.alert("cancelled");
       };

    });
