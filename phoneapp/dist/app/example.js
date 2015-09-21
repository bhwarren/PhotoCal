angular.module('example', [
  // Declare here all AngularJS dependencies that are shared by the example module.
  'supersonic'
]);

angular
    .module('example')
    .controller('CalendarController', function($scope, supersonic) {

        //$scope.eventname = "calendarview";

        $scope.events = (localStorage.getItem('events') === null) ? [] : JSON.parse(localStorage.getItem('events'));

        $scope.removeEvent = function(removeObject){
            var index = $scope.events.indexOf(removeObject);
            if(index != -1){
                $scope.events.splice(index,1);
                localStorage.setItem('events', JSON.stringify($scope.events));
                supersonic.logger.error("after removed: "+JSON.stringify($scope.events));
            }
        };

        supersonic.data.channel('confirmedEvent').subscribe( function(message) {
            //have to re-intialize events because the channel doesn't register removes
            $scope.events = (localStorage.getItem('events') === null) ? [] : JSON.parse(localStorage.getItem('events'));

            $scope.events.push(message);
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
    .controller('ConfirmController', function($scope, supersonic) {

        //first thing to do when the confirm modal shows is set the fields for display
        function init(){
            var last_new_event = JSON.parse(localStorage.getItem('last_new_event'));

            $scope.eventname = last_new_event.eventname;
            $scope.location = last_new_event.location;
            $scope.from = last_new_event.from;
            $scope.until = last_new_event.until;
            $scope.description = last_new_event.description;

            supersonic.logger.error("set successfully to: "+$scope.eventname);
            supersonic.logger.error("after showing modal");

       }
       init();

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

           supersonic.data.channel('confirmedEvent').publish(eventObject);
           supersonic.ui.dialog.alert("event saved and added to calendar");
        };

       $scope.cancel_event = function(){
           supersonic.logger.error("scope:"+$scope.eventname);
           supersonic.ui.dialog.alert("cancelled");
       };

    });

angular
    .module('example')
    .controller('FormController', function($scope, supersonic) {
        supersonic.logger.info("working");
        //the necessary parse.js is in dist/components
        Parse.initialize("QJFRh4kOQE9BUkNrLnzVb2wMzpDXBClI924yDPKr",  "rFDDxElNe4GXt5swG9vAODJS5SElopzsaQNbqifS");

        $scope.testParse = function(){
            supersonic.logger.info("parse started");

            var TestObject = Parse.Object.extend("TestObject");
            var testObject = new TestObject();
            testObject.save({foo: "bar"}).then(function(object) {
                supersonic.logger.info("yay! it worked");
            });
      };

  });

angular
    .module('example')
    .controller('PhotosController', function($scope, supersonic) {

        function confirmEvent(message){
            localStorage.setItem('last_new_event', JSON.stringify(message));
            supersonic.ui.modal.show("example#confirm_modal");
        }

        $scope.getCamera = function(){
            var options = {
              quality: 50,
              allowEdit: false,
              encodingType: "png",
              saveToPhotoAlbum: true
            };

            supersonic.media.camera.takePicture(options).then( function(result){
                var message = {
                    eventname: "carolina neuroscience club",
                    location: "genome g100",
                    from: "7:30pm on 9/14",
                    until: "",
                    description: "carolina neuroscience club is hosting a panel...."
                };

                confirmEvent(message);
            });
        };

        $scope.confirm = function(){
            var message = {
                eventname: "Halloween Party",
                location: "666 Elm Street",
                from: "10pm on 10/31",
                until: "3am on 11/1",
                description: "a party so good, you'll dream about it"
            };

            confirmEvent(message);
        };

        $scope.pickPhoto = function(){
            supersonic.ui.drawers.close();

            var options = {
              quality: 50,
              allowEdit: true,
              encodingType: "png",
            };

            supersonic.media.camera.getFromPhotoLibrary(options).then( function(result){
                // Do something with the image URI
                var message = {
                    eventname: "carolina neuroscience club",
                    location: "genome g100",
                    from: "7:30pm on 9/14",
                    until: "",
                    description: "carolina neuroscience club is hosting a panel...."
                };
                confirmEvent(message);

            });
        };

  });
