<div class="info-section">
	<div class="info-header">
		<i class="icon-bar-chart"></i>
		<h3>${ ui.message("mkscommons.obschart.title").toUpperCase() }</h3>
	</div>
	<div class="info-body">
		<table>
		    <% if (timeSeriesPerConcept) { %> /* Checking whether it's populated so far! */
		        <% timeSeriesPerConcept.each { uuid, chartPoints -> %> /* Renaming my KEY: uuid, and VALUE: chartPoints for easy access */
		            <tr>
		                <td>${ conceptNames.get('uuid') }</td> /* Retrieving the corresponding name of a Concept by its UUID */
		                <td>
		                	<table>
		                		<% if (chartPoints) { %> /* Checking whether it's not empty */
		                			<% chartPoints.each { point -> %> /* looping through the List of ChartPoint*/
			                			<tr>
			                				<td>${ ui.format(point.x) }</td>
			                				<td>${ ui.format(point.y) }</td>
			                			</tr>
			                		<% } %>
		                		<% } else { %>
		                			<tr>
		                				<td colspan="2">${ ui.message("general.none") }</td>
		                			</tr>
		                		<% } %>
		                	</table>
						</td>
		            </tr>
		        <% } %>
		    <% } else { %>
		        <tr>
		            <td colspan="2">${ ui.message("general.none") }</td>
		        </tr>
		    <% } %>
		</table>
	</div>
</div>