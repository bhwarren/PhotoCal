angular
    .module('example')
    .controller('confirmController', function($scope, supersonic) {

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
