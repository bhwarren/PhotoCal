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
                    eventname: "carolina neuroscience club",
                    location: "genome g100",
                    from: "7:30pm",
                    until: "",
                    description: "carolina neuroscience club is hosting a panel...."
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

  });
