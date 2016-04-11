package org.openmrs.module.mkscommons.fragment.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.mkscommons.fragment.controller.ObsChartFragmentController.ChartPoint;

public class TestObsChartsDashboardFragmentController {

	private ObsChartsDashboardFragmentController ctrl = new ObsChartsDashboardFragmentController(); 
	
	@Test
	public void shouldSplitAndSortConceptTimeSeries() {
		
		DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
		
		final String UUID_1 = "uuid_1";
		final String UUID_2 = "uuid_2";
		
		final Date date0 = dateFormatter.parseDateTime("11/01/2016 15:00:00").toDate();
		final Date date1 = dateFormatter.parseDateTime("01/01/2016 16:30:00").toDate();
		final Date date2 = dateFormatter.parseDateTime("12/12/2015 12:35:00").toDate();
		final Date date3 = dateFormatter.parseDateTime("31/12/2015 01:00:00").toDate();
		
		final Double val0 = 36.5;
		final Double val1 = 36.0;
		final Double val2 = 37.1;
		final Double val3 = 37.1;

		// Setup
		List<Obs> allObs = new ArrayList<Obs>();
		{
			Concept concept = new Concept();
			concept.setUuid(UUID_1);
			Obs obs = new Obs();
			obs.setConcept(concept);
			obs.setObsDatetime(date0);
			obs.setValueNumeric(val0);
			allObs.add(obs);
		}
		{
			Concept concept = new Concept();
			concept.setUuid(UUID_1);
			Obs obs = new Obs();
			obs.setConcept(concept);
			obs.setObsDatetime(date1);
			obs.setValueNumeric(val1);
			allObs.add(obs);
		}
		{
			Concept concept = new Concept();
			concept.setUuid(UUID_2);
			Obs obs = new Obs();
			obs.setConcept(concept);
			obs.setObsDatetime(date2);
			obs.setValueNumeric(val2);
			allObs.add(obs);
		}
		{
			Concept concept = new Concept();
			concept.setUuid(UUID_2);
			Obs obs = new Obs();
			obs.setConcept(concept);
			obs.setObsDatetime(date3);
			obs.setValueNumeric(val3);
			allObs.add(obs);
		}
		
		// Replay
		Map<String, List<ChartPoint>> timeSeries = ctrl.getTimeSeriesPerConcept(allObs);
		
		
		// Assert
		
		List<ChartPoint> series1 = timeSeries.get(UUID_1);
		assertEquals(series1.size(), 2);
		ChartPoint point10 = series1.get(0);
		assertEquals(point10.getX(), date1);
		assertEquals(point10.getY(), val1);
		ChartPoint point11 = series1.get(1);
		assertEquals(point11.getY(), val0);
		assertEquals(point11.getX(), date0);
		
		List<ChartPoint> series2 = timeSeries.get(UUID_2);
		assertEquals(series2.size(), 2);
		ChartPoint point22 = series2.get(0);
		assertEquals(point22.getY(), val2);
		assertEquals(point22.getX(), date2);
		ChartPoint point23 = series2.get(1);
		assertEquals(point23.getY(), val3);
		assertEquals(point23.getX(), date3);
		
	}
	
}
