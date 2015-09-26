angular
    .module('example')
    .controller('PhotosController', function($scope, supersonic) {


        //this took forever to figure out since there are only
        //10 billion ways of detecting when things are properly loaded
        //and it's still not right
        //supersonic.ui.views.find("photos").then( function(startedView) {
        //supersonic.logger.error("this sucks");

        var showPreview = supersonic.ui.views.current.whenVisible( function() {
            cordova.plugins.camerapreview.show();
            showPreview();
        });

        var hidePreview = supersonic.ui.views.current.whenHidden( function() {
            cordova.plugins.camerapreview.hide();
            hidePreview();
        });

        $scope.showDrawer = function(){
            supersonic.ui.drawers.open('right').then( function() {
                cordova.plugins.camerapreview.hide();
            });
        };



        $(document).ready(function(){
            //if this is the drawer opening this script, don't restart the camera preview
            //neccesary
            if($('#drawerList').length){
                return;
            }

            supersonic.logger.error("before");
            var hgt = $(document).height();
            var wdt = $(document).width();

            //var offset = $('#beginInner').offset().top + $('#navbar').height();
            //var topOffset = $('#beginInner').offset().top + $('#beginInner').position().top + parseInt($('#beginInner').css("margin-top").replace("px", "")) + 4;
            var topOffset = 45;

            supersonic.logger.error( "offset ht:"+ topOffset+" hgt:" + hgt + "wdith: "+wdt  );

            var tapEnabled = true; //enable tap take picture
            var dragEnabled = false; //enable preview box drag across the screen
            var toBack = true; //send preview box to the back of the webview
            var rect = {x: 0,
                        y: topOffset,
                        width: wdt,
                        height: hgt - topOffset
            };
            //supersonic.logger.error("after rect");
            cordova.plugins.camerapreview.startCamera(rect, "front", tapEnabled, dragEnabled, toBack);
            cordova.plugins.camerapreview.switchCamera(); //switch to back to rear camerapreview b/c specifying back isn't working
            supersonic.logger.error("this sucks");

        });


        //store the event locally, and add it to our calendar
        function confirmEvent(message){
            localStorage.setItem('last_new_event', JSON.stringify(message));
            supersonic.ui.modal.show("example#confirm_modal");
        }

        //do something with the picture just taken CCP
        cordova.plugins.camerapreview.setOnPictureTakenHandler(function(result){
            //supersonic.logger.error(result[0]);//originalPicturePath;
            //supersonic.logger.error(result[1]);//previewPicturePath;

            var message = {
                eventname: "carolina neuroscience club",
                location: "genome g100",
                from: "7:30pm on 9/14",
                until: "",
                description: "carolina neuroscience club is hosting a panel...."
            };

            confirmEvent(message);

            //hide preview
            //cordova.plugins.camerapreview.hide();

        });


        // $scope.getCamera = function(){
        //     var options = {
        //       quality: 50,
        //       allowEdit: false,
        //       encodingType: "png",
        //       saveToPhotoAlbum: true
        //     };
        //
        //     supersonic.media.camera.takePicture(options).then( function(result){
        //         var message = {
        //             eventname: "carolina neuroscience club",
        //             location: "genome g100",
        //             from: "7:30pm on 9/14",
        //             until: "",
        //             description: "carolina neuroscience club is hosting a panel...."
        //         };
        //
        //         confirmEvent(message);
        //     });
        // };
        //
        // $scope.confirm = function(){
        //     var message = {
        //         eventname: "Halloween Party",
        //         location: "666 Elm Street",
        //         from: "10pm on 10/31",
        //         until: "3am on 11/1",
        //         description: "a party so good, you'll dream about it"
        //     };
        //
        //     confirmEvent(message);
        // };

        $scope.pickPhoto = function(){
            supersonic.logger.error("clicked pickphoto");

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
