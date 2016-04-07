<div class="info-section">
	<div class="info-header">
		<i class="icon-bar-chart"></i>
		<h3>${ ui.message("mkscommons.obschart.title").toUpperCase() }</h3>
	</div>
	<div class="info-body">
		<table>
		    <% if (obsList) { %>
		        <% obsList.each { %>
		            <tr>
		                <td>${ ui.format(it.conceptId.name) }</td>
		                <td> : </td>
		                <td>${ ui.format(it.valueNumeric) }</td>
		            </tr>
		        <% } %>
		    <% } else { %>
		        <tr>
		            <td colspan="3">${ ui.message("general.none") }</td>
		        </tr>
		    <% } %>
		</table>
	</div>
</div>