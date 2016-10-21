package mobi.chouette.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateMidnight;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.service.TransitDataStatisticsService.Period;

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
		TransitDataStatisticsService.Period p1 = new TransitDataStatisticsService(). new Period();
		p1.from = startDate.toDate();
		p1.to = endDate.toDate();
		
		
		return p1;
		
	}
	
}
