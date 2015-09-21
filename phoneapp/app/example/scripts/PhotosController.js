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
