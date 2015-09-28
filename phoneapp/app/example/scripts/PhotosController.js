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
