angular
  .module('example')
  .controller('calendarController', function($scope, supersonic) {


      $scope.confirm_event = function(eventObject){
          //save the event locally for events page
          localStorage.clear();
          supersonic.logger.error("after clearing item");
          var events = localStorage.getItem('events');
          supersonic.logger.error(JSON.stringify(events));

          if(events === null){ 
              events = [];
              supersonic.logger.error("set events to empty arr");

          }
          else{
              supersonic.logger.error("before parse");
              events = JSON.parse(events);
              supersonic.logger.error("after parse");

          }
          supersonic.logger.error("before append");

          events.push(eventObject);
          supersonic.logger.error("before set events item 2nd");

          localStorage.setItem('events', JSON.stringify(events));

          //add the event to calendar
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
