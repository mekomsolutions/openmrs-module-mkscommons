package org.openmrs.module.mkscommons.fragment.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class ObsChartsDashboardFragmentController {

	protected static final Log log = LogFactory.getLog(ObsChartsDashboardFragmentController.class);

	public void controller(	FragmentModel model, @FragmentParam("patientId") Patient patient, UiUtils ui
			,	@SpringBean("appFrameworkService") AppFrameworkService appFrameworkService
			,	@SpringBean("conceptService") ConceptService conceptService
			,	@SpringBean("obsService")	  ObsService obsService
			)
	{
		List<Extension> extensions = appFrameworkService.getExtensionsForCurrentUser("patientDashboard.obsChartsList");
		for (Extension extension: extensions) {
			Map<String, Object> params = extension.getExtensionParams();
		}
	}
	
}
