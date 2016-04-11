package org.openmrs.module.mkscommons.fragment.controller;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class ObsChartFragmentController {

	/**
	 *         Helps with rendering timeseries for numeric Concepts.
	 *         Where <code>x</code> is the Obs Datetime
	 *         and <code>y</code> is the Numeric value at that date & time
	 */
	public static class ChartPoint {

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
	}
	
	protected static final Log log = LogFactory.getLog(ObsChartFragmentController.class);

	public void controller(	FragmentModel model, @FragmentParam("patientId") Patient patient, UiUtils ui
			,	@SpringBean("conceptService") ConceptService conceptService
			,	@SpringBean("obsService")	  ObsService obsService
			)
	{
	}
}
