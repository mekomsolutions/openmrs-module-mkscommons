package org.openmrs.module.mkscommons.fragment.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
public class ObsChartsWidgetFragmentController {
	
	public static class ChartPoint implements Comparable<ChartPoint> {

		public Date x;
		public Double y;
		
		public ChartPoint(Date x, Double y) {
			this.x = x;
			this.y = y;
		}

		public Date getX() {
			return x;
		}

		public void setX(Date x) {
			this.x = x;
		}

		public Double getY() {
			return y;
		}

		public void setY(Double y) {
			this.y = y;
		}

		/*
		 * This is necessary to sort time series (= arrays of ChartPoint).
		 */
		@Override
		public int compareTo(ChartPoint chartPoint) {
			return this.getX().compareTo(chartPoint.getX());
		}
	}

	protected static final Log log = LogFactory.getLog(ObsChartsWidgetFragmentController.class);

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
					Concept concept = conceptService.getConceptByUuid(uuid);// See the comments on 190-th line of code.
					if(concept == null) {
						log.warn("The concept with UUID: " + uuid
								+ ", was not found in the Concept Dictionary.");
						continue;
					}
					if (concept.isNumeric()) // Concept is added only if NUMERIC.
						conceptsList.add(concept);
					else
						log.warn("The concept with NAME: "
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
        
        model.addAttribute("timeSeriesPerConcept", ui.toJson(getTimeSeriesPerConcept(obsList)));
        model.addAttribute("conceptNames", ui.toJson(getConceptNames(obsList)));
        model.addAttribute("conceptUnits", ui.toJson(getConceptUnits(obsList, conceptService)));
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
		
		for (List<ChartPoint> timeSerie : allChartPointsMap.values()) {
			Collections.sort(timeSerie);
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
	
	/**
	 * Gets Concept Numeric Units considering the given Concept UUID
	 * 
	 * @param allObs
	 *            List of Obs that will be giving the Corresponding Concepts
	 * @return Map of String: UUID and String: Concept Units for specific Locale
	 */
	protected Map<String, String> getConceptUnits(List<Obs> allObs, ConceptService conceptService) {
		
		Map<String, String> conceptUnits = new HashMap<String, String>();
		
		for(Obs obs: allObs){
			String uuid = obs.getConcept().getUuid();
			/* Getting the units for the Numeric Concept using UUID */
			conceptUnits.put(uuid, conceptService.getConceptNumericByUuid(uuid).getUnits()); //This can be also used in the controller method instead of getConceptByUuid method...
		}
		
		return conceptUnits;
	}
}
