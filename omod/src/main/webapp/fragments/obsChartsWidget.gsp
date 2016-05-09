<%
	ui.includeJavascript("uicommons", "angular.min.js")
	ui.includeJavascript("uicommons", "angular-resource.min.js")
	ui.includeJavascript("uicommons", "angular-common.js")
	ui.includeJavascript("uicommons", "angular-app.js")

	ui.includeJavascript("mkscommons", "d3/d3.min.js")
	ui.includeJavascript("mkscommons", "n3-charts/LineChart.js")
	ui.includeCss("mkscommons", "n3-charts/LineChart.css")

	ui.includeJavascript("mkscommons", "obsCharts.js")
%>

<script type="text/javascript">

  window.OpenMRS = window.OpenMRS || {};

  var config = {};
  config.timeSeriesPerConcept = ${timeSeriesPerConcept};
  config.conceptNames = ${conceptNames};

</script>

<style>

.chart-legend {display: none};

.container {}

.tick text {
    font-size: 120%;
    font-family: "OpenSans";
}

</style>

<div class="info-section">
	<div class="info-header">
		<i class="icon-bar-chart"></i>
		<h3>${ ui.message("mkscommons.obschart.title").toUpperCase() }</h3>
	</div>
	<div class="info-body">
		<div ng-app="obsChartsApp" ng-controller="ObsChartsCtrl">
		
			<div ng-repeat="options in allOptions">
				<div>
					<h3 style"width: 50%" class="left">{{options.title}}</h3><p style"width: 50%" class="right">{{options.latestValue}}</p>
				</div>
				<p ng-if="options.latestDateTime">{{options.latestDateTime}}</p>
				<linechart ng-if="options.hasData" data="data" options="options"/>				
			</div>

		</div>
	</div>
</div>