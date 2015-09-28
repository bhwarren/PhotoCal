angular
    .module('example')
    .controller('CalendarController', function($scope, supersonic) {

        /*var hidePreview = supersonic.ui.views.current.whenVisible( function() {
            cordova.plugins.camerapreview.hide();
            hidePreview();
        });*/
        cordova.plugins.camerapreview.hide();


        //$scope.eventname = "calendarview";

        $scope.events = (localStorage.getItem('events') === null) ?
                        [] : JSON.parse(localStorage.getItem('events'));

        $scope.eventsAdded = ($scope.events.length === 0) ?
                            "No events added yet" : "Click the x to delete an event";

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
