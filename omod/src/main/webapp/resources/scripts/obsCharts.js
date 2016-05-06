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
          latestValue: lastPoint.y || "_____",
          latestDateTime: new Date(lastPoint.x) || "",
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