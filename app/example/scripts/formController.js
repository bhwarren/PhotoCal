angular
    .module('example')
    .controller('formController', function($scope, supersonic) {
        supersonic.logger.info("working");
        //the necessary parse.js is in dist/components
        Parse.initialize("QJFRh4kOQE9BUkNrLnzVb2wMzpDXBClI924yDPKr",  "rFDDxElNe4GXt5swG9vAODJS5SElopzsaQNbqifS");

        $scope.testParse = function(){
            supersonic.logger.info("parse started");

            var TestObject = Parse.Object.extend("TestObject");
            var testObject = new TestObject();
            testObject.save({foo: "bar"}).then(function(object) {
                supersonic.logger.info("yay! it worked");
            });
      };

  });
