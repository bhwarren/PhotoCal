angular
    .module('example')
    .controller('calendarController', function($scope, supersonic) {

        $scope.eventname = "calendarview";

        $scope.events = (localStorage.getItem('events') === null) ? [] : JSON.parse(localStorage.getItem('events'));

        $scope.removeEvent = function(removeObject){
            var index = $scope.events.indexOf(removeObject);
            if(index != -1){
                $scope.events.splice(index,1);
                localStorage.setItem('events', JSON.stringify($scope.events));
            }
        };

        supersonic.data.channel('confirmedEvent').subscribe( function(message) {
            supersonic.logger.error("received a message " + JSON.stringify(message));

            supersonic.logger.error("before adding:"+JSON.stringify($scope.events));

            $scope.events.push(message);
            supersonic.logger.error("after adding:"+JSON.stringify($scope.events));

            localStorage.setItem('events', JSON.stringify($scope.events));

        });

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
