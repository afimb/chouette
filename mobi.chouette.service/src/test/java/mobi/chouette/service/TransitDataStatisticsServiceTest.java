package mobi.chouette.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateMidnight;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.model.statistics.Period;

public class TransitDataStatisticsServiceTest   {



	@Test
	public void testMergeOverlappingPeriods() {
		List<Period> periods = new ArrayList<Period>();

		DateMidnight today = new DateMidnight();
		periods.add(createPeriod(today, 2));
		periods.add(createPeriod(today.plusDays(1), 2));
		
		List<Period> splitPeriods = new TransitDataStatisticsService().mergeOverlappingPeriods(periods);
		
		Assert.assertEquals(splitPeriods.size(),1);
		
		
	}

	private Period createPeriod(DateMidnight startDate, int days) {
		DateMidnight endDate = startDate.plusDays(days);
		Period p1 =  new Period(startDate.toDate(),endDate.toDate());
		
		return p1;
		
	}
	
}
