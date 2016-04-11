package org.openmrs.module.mkscommons.fragment.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.mkscommons.fragment.controller.ObsChartFragmentController.ChartPoint;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 * Reads an extension configuration file to find out the list of concepts that
 * should be charted. We need to at the same time 
 *   ** Implement the controller: 
 * 			- List the concepts
 * 			- Retrieve the 24h set of date-times/values for the obs coded by those concepts 
 * 			- Store them in a convenient way to be sent to the view (obsChartsDashboard.gsp) 
 *   ** Fine tune the configuration file while implementing.
 */
public class ObsChartsDashboardFragmentController {

	protected static final Log log = LogFactory.getLog(ObsChartsDashboardFragmentController.class);

	public void controller(FragmentModel model, @FragmentParam("patientId") Patient patient, UiUtils ui,
			@SpringBean("appFrameworkService") AppFrameworkService appFrameworkService,
			@SpringBean("conceptService") ConceptService conceptService,
			@SpringBean("obsService") ObsService obsService,
			@SpringBean("personService") PersonService personService) {
		
		List<Concept> conceptsList = new ArrayList<Concept>();
		List<Person> personsList = new ArrayList<Person>();
		personsList.add(personService.getPerson(patient.getPersonId()));
		
		List<Extension> extensions = appFrameworkService.getExtensionsForCurrentUser("patientDashboard.obsChartsList");
		
		for (Extension extension : extensions) {
			
			@SuppressWarnings("unchecked")
			List<String> uuids = (List<String>) extension.getExtensionParams().get("conceptsUuids");
			
			if (uuids != null) {
				for (String uuid : uuids){
					Concept concept = conceptService.getConceptByUuid(uuid);
					if(concept == null) {
						log.warn("WARNING: The concept with UUID: " + uuid
								+ ", is was not found in the Concept Dictionary.");
						continue;
					}
					if (concept.isNumeric()) // Concept is added only if NUMERIC.
						conceptsList.add(concept);
					else
						log.warn("WARNING: The concept with NAME: "
								+ concept.getName(Context.getLocale())
										.getName() + " and UUID: " + uuid
								+ ", is excluded because it is non-numeric and cannot be charted.");
				}
			}
		}
		
		/* Getting time range: End date is NOW, Start date is 24 hours before */
		DateTime endDate = DateTime.now();
		DateTime startDate = endDate.minusHours(24);
        
        /* Retrieving OBS using Persons list, List of Concepts, Start and End Dates range */
        List<Obs> obsList = obsService.getObservations(personsList, null, conceptsList, null, null, null, null, null, null, startDate.toDate(), endDate.toDate(), false);
        
        model.addAttribute("timeSeriesPerConcept", getTimeSeriesPerConcept(obsList));
        model.addAttribute("conceptNames", getConceptNames(obsList));
        
	}
	

	/**
	 * Gets the Time Series per each Concept corresponding to each Obs in the
	 * given List
	 * 
	 * @param allObs
	 *            List of Obs that will be giving the Corresponding Concepts
	 * @return Map of String: UUID and ChartPoint which is X and Y coordinates
	 *         for a better View
	 */
	protected Map<String, List<ChartPoint>> getTimeSeriesPerConcept(List<Obs> allObs) {
		
		Map<String, List<ChartPoint>> allChartPointsMap = new HashMap<String, List<ChartPoint>>();
		
		for(Obs obs: allObs){
			
			if(allChartPointsMap.keySet().contains(obs.getConcept().getUuid()))
				/* If the KEY exists, just add the OBS to the list */
				allChartPointsMap.get(obs.getConcept().getUuid()).add(new ChartPoint(obs.getObsDatetime(), obs.getValueNumeric()));
			
			else{
				List<ChartPoint> chartPoints = new ArrayList<ChartPoint>();
				chartPoints.add(new ChartPoint(obs.getObsDatetime(), obs.getValueNumeric()));
				allChartPointsMap.put(obs.getConcept().getUuid(), chartPoints);
			}
		}
		
		return allChartPointsMap;
	}
	
	/**
	 * Gets Concept Name considering the User Locale and a given UUID
	 * 
	 * @param allObs
	 *            List of Obs that will be giving the Corresponding Concepts
	 * @return Map of String: UUID and String: Concept Name for specific Locale
	 */
	protected Map<String, String> getConceptNames(List<Obs> allObs) {
		
		Map<String, String> conceptNames = new HashMap<String, String>();
		
		for(Obs obs: allObs)
			/* Getting the name with specific Default Locale in OpenMRS */
			conceptNames.put(obs.getConcept().getUuid(), (obs.getConcept().getName(Context.getLocale()).getName()));
		
		return conceptNames;
	}
}
