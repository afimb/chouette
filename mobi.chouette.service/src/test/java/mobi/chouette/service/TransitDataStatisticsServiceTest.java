package mobi.chouette.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mobi.chouette.model.statistics.LineStatistics;
import mobi.chouette.model.statistics.PublicLine;
import mobi.chouette.model.statistics.ValidityCategory;
import org.joda.time.DateMidnight;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.model.statistics.Period;

public class TransitDataStatisticsServiceTest {

	@Test
	public void categorizeValidityWithInvalidLines() {
		DateMidnight startDate = new DateMidnight();
		LineStatistics lineStats = new LineStatistics();
		lineStats.getPublicLines().add(createPublicLine("invalidNoDate", null));
		lineStats.getPublicLines().add(createPublicLine("invalidHistoricDate", startDate.plusDays(-2)));
		lineStats.getPublicLines().add(createPublicLine("validOutsideCategory", startDate.plusDays(2)));
		lineStats.getPublicLines().add(createPublicLine("validCat1", startDate.plusDays(7)));
		lineStats.getPublicLines().add(createPublicLine("validCat2", startDate.plusDays(15)));

		new TransitDataStatisticsService().categorizeValidity(lineStats, startDate.toDate(), new Integer[]{5, 10});
		Assert.assertEquals(lineStats.getValidityCategories().size(),4);
		Assert.assertTrue(getCategory(lineStats,-1).getLineNumbers().contains("invalidNoDate"));
		Assert.assertTrue(getCategory(lineStats,-1).getLineNumbers().contains("invalidHistoricDate"));
		Assert.assertTrue(getCategory(lineStats,0).getLineNumbers().contains("validOutsideCategory"));
		Assert.assertTrue(getCategory(lineStats,5).getLineNumbers().contains("validCat1"));
		Assert.assertTrue(getCategory(lineStats,10).getLineNumbers().contains("validCat2"));
	}

	@Test
	public void testMergeOverlappingPeriods() {
		List<Period> periods = new ArrayList<Period>();

		DateMidnight today = new DateMidnight();
		periods.add(createPeriod(today, 2));
		periods.add(createPeriod(today.plusDays(1), 2));

		List<Period> splitPeriods = new TransitDataStatisticsService().mergeOverlappingPeriods(periods);

		Assert.assertEquals(splitPeriods.size(), 1);


	}

	private Period createPeriod(DateMidnight startDate, int days) {
		DateMidnight endDate = startDate.plusDays(days);
		Period p1 = new Period(startDate.toDate(), endDate.toDate());

		return p1;

	}

	private ValidityCategory getCategory(LineStatistics lineStatistics, int numDaysAtLeastValid) {
		ValidityCategory matchingCategory = null;
		for (ValidityCategory validityCategory : lineStatistics.getValidityCategories()) {
			if (validityCategory.getNumDaysAtLeastValid() == numDaysAtLeastValid) {
				matchingCategory = validityCategory;
				break;
			}
		}
		return matchingCategory;
	}

	private PublicLine createPublicLine(String no, DateMidnight endDate) {
		PublicLine publicLine = new PublicLine(no);
		if (endDate != null) {
			DateMidnight startDate = endDate.plusDays(-1);
			publicLine.getEffectivePeriods().add(new Period(startDate.toDate(), endDate.toDate()));
		}

		return publicLine;
	}
}
