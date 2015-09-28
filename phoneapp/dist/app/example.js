angular.module('example', [
  // Declare here all AngularJS dependencies that are shared by the example module.
  'supersonic'
]);

angular
    .module('example')
    .controller('CalendarController', function($scope, supersonic) {

        /*var hidePreview = supersonic.ui.views.current.whenVisible( function() {
            cordova.plugins.camerapreview.hide();
            hidePreview();
        });*/
        //cordova.plugins.camerapreview.hide();

        leftButton = new supersonic.ui.NavigationBarButton( {
          title: "",
          onTap: function() {
            $scope.goToPhotos();
          }
        });
        rightButton = new supersonic.ui.NavigationBarButton( {
          title: "",
          onTap: function() {
            supersonic.ui.drawers.open('right');
          }
        });

        options = {
          title: "Events",
          overrideBackButton: true,
          backButton: leftButton,
          buttons: {
            right: [rightButton]

          }
      };

        supersonic.ui.navigationBar.update(options);

        //$scope.eventname = "calendarview";

        $scope.events = (localStorage.getItem('events') === null) ?
                        [] : JSON.parse(localStorage.getItem('events'));

        $scope.eventsAdded = ($scope.events.length === 0) ?
                            "No events added yet" : "Click the x to delete an event";

        $scope.goToPhotos = function(){
            supersonic.logger.error("fffffffff");
            localStorage.setItem('currentView', "photos");
            supersonic.ui.drawers.close();
            supersonic.ui.layers.popAll();
        };

        $scope.goToEvents = function(){
            localStorage.setItem('currentView', "events");
            supersonic.ui.drawers.close();
            cordova.plugins.camerapreview.hide();
        };


        $scope.goToAbout = function(){
            localStorage.setItem('currentView', "about");
            supersonic.ui.drawers.close();
            cordova.plugins.camerapreview.hide();

            supersonic.ui.modal.show("example#about");

        };

        $scope.removeEvent = function(removeObject) {
            var index = $scope.events.indexOf(removeObject);
            if (index != -1) {
                $scope.events.splice(index, 1);
                localStorage.setItem('events', JSON.stringify($scope.events));
                supersonic.logger.error("after removed: " + JSON.stringify(
                    $scope.events));
            }
            $scope.eventsAdded = ($scope.events.length === 0) ?
                "No events added yet" :
                "Click the x to delete an event";
        };

        supersonic.data.channel('confirmedEvent').subscribe(function(message) {
            //have to re-intialize events because the channel doesn't register removes
            $scope.events = (localStorage.getItem('events') === null) ?
                            [] : JSON.parse(localStorage.getItem('events'));

            $scope.events.push(message);
            localStorage.setItem('events', JSON.stringify($scope.events));
        });

        $scope.openCalendar = function() {
            cordova.plugins.camerapreview.switchCamera();
            supersonic.ui.drawers.close();

            supersonic.device.platform().then(function(platform) {
                if (platform.name == "Android") {
                    window.plugins.calendar.openCalendar();
                } else {
                    supersonic.app.openURL("calshow://");
                }
            });
        };

        $scope.pickPhotoPass = function(){
            supersonic.data.channel('pickPhotoPass').publish(true);
            supersonic.logger.error("sent request to pick photo");
        };

    });

angular
    .module('example')
    .controller('ConfirmController', function($scope, supersonic) {

        cordova.plugins.camerapreview.hide();


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

        document.addEventListener("deviceready", function() {

        supersonic.ui.drawers.whenWillClose(function() {

            if(localStorage.getItem('currentView') == "photos"){
                cordova.plugins.camerapreview.show();
            }
            supersonic.logger.error("drawer closed, shown preview");
            supersonic.logger.error("currentview keys: "+JSON.stringify(supersonic.ui.views.current.id));
        });

        supersonic.ui.views.current.whenHidden(function() {
            cordova.plugins.camerapreview.hide();
            supersonic.logger.error("dhide preview b/c hidden");
        });

        if ($('#drawerList').length) {
            return;
        }

        supersonic.ui.views.current.whenVisible(function() {
            cordova.plugins.camerapreview.show();
        });

        $scope.showDrawer = function() {
            supersonic.ui.drawers.open('right').then(function() {
                cordova.plugins.camerapreview.hide();
            });
        };


        //this took forever to figure out since there are only
        //10 billion ways of detecting when things are properly loaded
        //and it's still not right
        //supersonic.ui.views.find("photos").then( function(startedView) {
        //supersonic.logger.error("this sucks");
        //$(document).ready(function() {
        //document.addEventListener("deviceready", function() {
            $(document).ready(function() {
                //if this is the drawer opening this script, don't restart the camera preview
                //neccesary
                if ($('#drawerList').length) {
                    return;
                }

                localStorage.setItem('currentView', "photos");

                supersonic.logger.error("before");
                var hgt = $(document).height();
                var wdt = $(document).width();

                //var offset = $('#beginInner').offset().top + $('#navbar').height();
                //var topOffset = $('#beginInner').offset().top + $('#beginInner').position().top + parseInt($('#beginInner').css("margin-top").replace("px", "")) + 4;
                var topOffset = 45;
                var buttonSize = $('#shutter').outerHeight(true);

                $('#beginInner').height(hgt - topOffset - buttonSize);

                supersonic.logger.error("offset ht:" + topOffset + " hgt:" + hgt + "wdith: " + wdt);

                var tapEnabled = true; //enable tap take picture
                var dragEnabled = false; //enable preview box drag across the screen
                var toBack = false; //send preview box to the back of the webview
                var rect = {
                    x: 0,
                    y: topOffset,
                    width: wdt,
                    height: hgt - topOffset - buttonSize
                };
                //supersonic.logger.error("after rect");
                cordova.plugins.camerapreview.startCamera( rect, "back", tapEnabled, dragEnabled, toBack);
                cordova.plugins.camerapreview.switchCamera(); //switch to back to rear camerapreview b/c specifying back isn't working
                supersonic.logger.error("this sucks");
            });

        //}, false);


        //store the event locally, and add it to our calendar via a modal
        function confirmEvent(message) {
            var begin = Date.future(message.from);
            var end = Date.future(message.until);

            message.from = (begin == "Invalid Date") ? "" : begin.long();
            message.until = (end == "Invalid Date" || (begin !=
                "Invalid Date" && begin.isAfter(end))) ? "" : end.long();

            localStorage.setItem('last_new_event', JSON.stringify(message));
            supersonic.ui.modal.show("example#confirm_modal");
        }

        $scope.takePicture = function(){
            cordova.plugins.camerapreview.takePicture();
        };

        //do something with the picture just taken CCP
        cordova.plugins.camerapreview.setOnPictureTakenHandler(function(result) {
            //supersonic.logger.error(result[0]);//originalPicturePath;
            //supersonic.logger.error(result[1]);//previewPicturePath;

            var message = {
                eventname: "carolina neuroscience club",
                location: "genome g100",
                from: "9/30 at 7:30pm",
                until: "",
                description: "carolina neuroscience club is hosting a panel...."
            };

            confirmEvent(message);
            //hide preview
            cordova.plugins.camerapreview.hide();

        });

        $scope.pickPhoto = function() {
            cordova.plugins.camerapreview.switchCamera();
            cordova.plugins.camerapreview.hide();

            supersonic.logger.error("clicked pickphoto");

            supersonic.ui.drawers.close();

            var options = {
                quality: 50,
                allowEdit: true,
                encodingType: "png",
            };

            supersonic.media.camera.getFromPhotoLibrary(options).then(
                function(result) {
                    // Do something with the image URI
                    var message = {
                        eventname: "carolina neuroscience club",
                        location: "genome g100",
                        from: "9/14 at 7:30pm",
                        until: "",
                        description: "carolina neuroscience club is hosting a panel...."
                    };
                    confirmEvent(message);

                });
        };

        supersonic.data.channel('pickPhotoPass').subscribe(function(bool) {
            if(bool){
                supersonic.logger.error("recieved request to pick photo");
                $scope.pickPhoto();
            }
        });
    }, false);

    });
