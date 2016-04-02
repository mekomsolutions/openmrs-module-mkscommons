package org.openmrs.module.mkscommons.fragment.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
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

public class ObsChartsDashboardFragmentController {

	private static final long ONE_DAY = 24*60*60*1000;
	protected static final Log log = LogFactory
			.getLog(ObsChartsDashboardFragmentController.class);

	public void controller(FragmentModel model, @FragmentParam("patientId") Patient patient, UiUtils ui,
			@SpringBean("appFrameworkService") AppFrameworkService appFrameworkService,
			@SpringBean("conceptService") ConceptService conceptService,
			@SpringBean("obsService") ObsService obsService,
			@SpringBean("personService") PersonService personService) {
		
		List<Concept> conceptsList = new ArrayList<Concept>();
		List<Person> personsList = new ArrayList<Person>();
		personsList.add(personService.getPerson(patient.getPersonId()));
		
		/* Need to know about this portion of code ... */
		List<Extension> extensions = appFrameworkService.getExtensionsForCurrentUser("patientDashboard.obsChartsList");
		
		for (Extension extension : extensions) {
			
			Map<String, Object> params = extension.getExtensionParams();
		}
		
		/* Getting time range: End date is NOW, Start date is 24 hours before */
		Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        Date startDate = cal.getTime();
        
        startDate.setTime(startDate.getTime() - (ONE_DAY));
        
        //TODO: using PatientIDs, List of Concepts, Start and End Dates range
        model.addAttribute("obsList", obsService.getObservations(personsList, null, conceptsList, null, null, null, null, null, null, startDate, endDate, true));
	}

}
