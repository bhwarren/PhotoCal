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
