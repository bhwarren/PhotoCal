angular.module('example', [
  // Declare here all AngularJS dependencies that are shared by the example module.
  'supersonic'
]);

angular
    .module('example')
    .controller('CalendarController', function($scope, supersonic) {

        //override the nav bar with the correct functionality
        //doesn't work with 'var' for some reason
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
            backButton: leftButton,  //override back button w/ our own
            buttons: {
                right: [rightButton]
            }
        };
        //finally enable our new navbar
        supersonic.ui.navigationBar.update(options);


        //get the events from local storage for display
        $scope.events = (localStorage.getItem('events') === null) ?
                        [] : JSON.parse(localStorage.getItem('events'));

        //if no events, say so in header, otherwise show relevant message
        $scope.eventsAdded = ($scope.events.length === 0) ?
                            "No events added yet" : "Click the x to delete an event";

        //function show the main page to take a photo
        $scope.goToPhotos = function(){
            localStorage.setItem('currentView', "photos");
            supersonic.ui.drawers.close();
            supersonic.ui.layers.popAll();
        };

        //show the saved events page
        $scope.goToEvents = function(){
            localStorage.setItem('currentView', "events");
            supersonic.ui.drawers.close();
            cordova.plugins.camerapreview.hide();
        };

        //show the about page
        $scope.goToAbout = function(){
            localStorage.setItem('currentView', "about");
            supersonic.ui.drawers.close();
            cordova.plugins.camerapreview.hide();

            supersonic.ui.modal.show("example#about");

        };

        //remove the event from the saved list
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

        //listen for an event to add to the calendar
        supersonic.data.channel('confirmedEvent').subscribe(function(message) {
            //normalize the date & time
            var begin = Date.future(message.from);
            var end = Date.future(message.until);

            //if begin time is invalid, set it to nothing, else normalize
            message.from = (begin == "Invalid Date") ? "" : begin.long();
            //if end time is invalid, or begin is after end, set end to nothing, esle normalize
            message.until = (end == "Invalid Date" || (begin != "Invalid Date" && begin.isAfter(end))) ?
                            "" : end.long();

            //localStorage.setItem('last_new_event', JSON.stringify(message));


            //add it to actual calendar
            window.plugins.calendar.createEventInteractively(message.eventname, message.location, message.description, begin, end,
                //execute if adding successfully
                function(){
                    supersonic.logger.error("success adding to real calender");

                    //have to re-intialize events because the channel doesn't register removes
                    //push this new event onto the stored events
                    $scope.events = (localStorage.getItem('events') === null) ?
                                    [] : JSON.parse(localStorage.getItem('events'));
                    $scope.events.push(message);
                    localStorage.setItem('events', JSON.stringify($scope.events));
                },
                //execute if error opening calendar app
                function(error){
                    supersonic.ui.dialog.alert("failure adding to calender");
                    supersonic.logger.error("failure adding to real calendar: "+error);
                }
            );
            
        });

        //function to open the native calendar on todays date
        $scope.openCalendar = function() {
            cordova.plugins.camerapreview.switchCamera(); //switch camera needed because of quirks
            supersonic.ui.drawers.close();

            supersonic.device.platform().then(function(platform) {
                window.plugins.calendar.openCalendar();

                // if (platform.name == "Android") {
                //    window.plugins.calendar.openCalendar();
                // }
                // else {
                //     supersonic.app.openURL("calshow://");
                // }
            });
        };

        //request that the photoscontroller do an upload
        //used in the drawer
        $scope.pickPhotoPass = function(){
            supersonic.data.channel('pickPhotoPass').publish();
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

        //this listener is needed for the camera preview to work properly
        document.addEventListener("deviceready", function() {

            //if the drawer invoked this controller, just return
            if ($('#drawerList').length) {
                return;
            }

            //when the drawer closes, show preview if on photos.html
            supersonic.ui.drawers.whenWillClose(function() {
                if(localStorage.getItem('currentView') == "photos"){
                    cordova.plugins.camerapreview.show();
                }
                supersonic.logger.error("drawer closed, shown preview");
            });

            //set listener to hide preview when Photos.html is hidden
            supersonic.ui.views.current.whenHidden(function() {
                cordova.plugins.camerapreview.hide();
                supersonic.logger.error("dhide preview b/c hidden");
            });

            //set listener to show preview when Photos.html is visible
            supersonic.ui.views.current.whenVisible(function() {
                cordova.plugins.camerapreview.show();
            });

            //hide the preview when showing the drawer
            $scope.showDrawer = function() {
                supersonic.ui.drawers.open('right').then(function() {
                    cordova.plugins.camerapreview.hide();
                });
            };


            //wait for view to load properly to get correct height
            //not working quite right, but usable
            $(document).ready(function() {
                //if this is the drawer opening this script, don't restart the camera preview
                //neccesary
                if ($('#drawerList').length) {
                    return;
                }

                localStorage.setItem('currentView', "photos");

                //get needed dimensions for display
                var hgt = $(document).height();
                var wdt = $(document).width();
                //var offset = $('#beginInner').offset().top + $('#navbar').height();
                //var topOffset = $('#beginInner').offset().top + $('#beginInner').position().top + parseInt($('#beginInner').css("margin-top").replace("px", "")) + 4;
                var topOffset = 45;
                var buttonSize = $('#shutter').outerHeight(true);
                $('#beginInner').height(hgt - topOffset - buttonSize);
                //supersonic.logger.error("offset ht:" + topOffset + " hgt:" + hgt + "wdith: " + wdt);

                var tapEnabled = true;
                var dragEnabled = false;
                var toBack = false; //send preview box to the back of the webview
                var rect = {
                    x: 0,
                    y: topOffset,
                    width: wdt,
                    height: hgt - topOffset - buttonSize
                };

                cordova.plugins.camerapreview.startCamera( rect, "back", tapEnabled, dragEnabled, toBack);
                cordova.plugins.camerapreview.switchCamera();  //switch to back to rear camerapreview b/c specifying back isn't working

            });

            //store the event locally, and add it to our calendar interactively
            function confirmEvent(message) {
                supersonic.data.channel('confirmedEvent').publish(message);
                //show preview again after adding
                cordova.plugins.camerapreview.show();
            }


            //function for invoking taking a picture
            $scope.takePicture = function(){
                cordova.plugins.camerapreview.takePicture();
            };

            //do something with the picture whenever it's taken
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
                //cordova.plugins.camerapreview.hide();

            });

            //function for uploading photo to server for analysis
            $scope.pickPhoto = function() {
                cordova.plugins.camerapreview.switchCamera(); //needed for quirks in preview plugin
                cordova.plugins.camerapreview.hide();

                supersonic.logger.error("clicked pickphoto");

                supersonic.ui.drawers.close();

                var options = {
                    quality: 50,
                    allowEdit: true,
                    encodingType: "png",
                };

                //use the plugin to do actual picking of photos
                supersonic.media.camera.getFromPhotoLibrary(options).then(
                    function(result) {
                        // Do something with the image URI
                        var message = {
                            eventname: "carolina neuroscience club",
                            location: "genome g100",
                            from: "9/30 at 7:30pm",
                            until: "",
                            description: "carolina neuroscience club is hosting a panel...."
                        };
                        confirmEvent(message);

                    });
            };

            //listen for requests to upload photos
            supersonic.data.channel('pickPhotoPass').subscribe(function() {
                supersonic.logger.error("recieved request to pick photo");
                $scope.pickPhoto();

            });

        //end of device ready listener
        }, false);

    });
