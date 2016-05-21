angular
  .module('obsChartsApp', ['n3-line-chart'])

  .controller('ObsChartsCtrl', ['$scope', '$window', function($scope, $window) {
    
    $scope.charts = [];

    $scope.data = {}; // data is a map of arrays (of series)
    $scope.allOptions = [];  

    angular.forEach($window.config.timeSeriesPerConcept, function(timeSeries, uuid) {
      
      var lastPoint = timeSeries[timeSeries.length-1];

      var hasData = false;
      if (timeSeries.length > 1) {  // We want at least one point to chart
        $scope.data[uuid] = timeSeries;
        hasData = true;
      }

      this.push(  // n3-charts options
        {
          /* We had a few custom members */
          title: $window.config.conceptNames[uuid], // We added this member as a convenience
          units: $window.config.conceptUnits[uuid], // We added this member as a convenience
          latestValue: lastPoint.y || "_____",
          latestDateTime: formatMyDate(new Date(lastPoint.x)) || "",
          hasData: hasData,

          series: [
            {
              dataset: uuid,  // that's how the options are mapped to the data
              axis: "y",
              key: "y",
              color: "#009384",
              type: ['dot']
            }
          ],
          axes: {
            x: {key: "x", type: "date"},
            y: {
              padding: {min:0, max: 20}
            }
          }
        });

    }, $scope.allOptions);
    
  }]);

  function formatMyDate(myDate){
    var todayDate = new Date();
    var formattedDate = myDate.getDate() + '-' + (myDate.getMonth() + 1) + '-' + myDate.getFullYear();
    var seconds = addZero(myDate.getSeconds());
    var minutes = addZero(myDate.getMinutes());
    var hour = addZero(myDate.getHours());
    var formattedTime = hour + ":" + minutes + ":" + seconds;
    
    if(myDate == (todayDate.getDate() -1)){
      var message = "Yesterday @ " + formattedTime;
      return message;
    }
    else{
      var message = "Today @ " + formattedTime;
      return message;
    }
  }

  function addZero(i) {
    if (i < 10) {
        i = "0" + i;
    }
    return i;
  }