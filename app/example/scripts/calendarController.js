angular
  .module('example')
  .controller('calendarController', function($scope, supersonic) {

      $scope.confirm_event = function(){

          var eventObject = {
              eventname: $scope.eventname,
              location: $scope.location,
              from: $scope.from,
              until: $scope.until,
              description: $scope.description
          };

          //save the event locally for events page
          var events = localStorage.getItem('events');

          if(events === null){
              events = [];
          }
          else{
              events = JSON.parse(events);
          }

          events.push(eventObject);
          localStorage.setItem('events', JSON.stringify(events));
          supersonic.ui.dialog.alert("event saved and added to calendar");
      };

      $scope.cancel_event = function(){
          supersonic.ui.dialog.alert("cancelled");
      };

        $scope.openCalendar = function(){
            supersonic.ui.drawers.close();
            supersonic.device.platform().then( function(platform) {
                 if(platform.name == "Android"){
                     supersonic.ui.dialog.alert("need a plugin to open 4 android, sorry");
                 }
                 else{
                     supersonic.app.openURL("calshow://");
                 }
             });

        };
 });
