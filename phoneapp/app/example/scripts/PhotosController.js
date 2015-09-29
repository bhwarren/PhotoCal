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
