package mobi.chouette.exchange.regtopp.importer.parser;

import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.importer.CalendarStrategy;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeHeaderDKO;
import mobi.chouette.model.Timetable;

public class ObjectidCreatorTest {

	@Test
	public void recreateTimetableObjectIdWithDate() {
		Timetable tt = new Timetable();
		tt.setObjectId("TST:Timetable:01234567-2016-01-01");

		RegtoppImportParameters configuration = new RegtoppImportParameters();
		configuration.setObjectIdPrefix("TST");
		configuration.setCalendarStrategy(CalendarStrategy.ADD);
		RegtoppDayCodeHeaderDKO header = new RegtoppDayCodeHeaderDKO();
		header.setDate(new LocalDate(2016, 1, 1));
		String recomputeTimetableId = ObjectIdCreator.recomputeTimetableId(configuration , "999", tt, header );
	
		Assert.assertEquals(recomputeTimetableId, "TST:Timetable:9993456-2016-01-01");
		
	}

	@Test
	public void recreateTimetableObjectIdWithoutDate() {
		Timetable tt = new Timetable();
		tt.setObjectId("TST:Timetable:01234567");

		RegtoppImportParameters configuration = new RegtoppImportParameters();
		configuration.setObjectIdPrefix("TST");
		configuration.setCalendarStrategy(CalendarStrategy.UPDATE);
		RegtoppDayCodeHeaderDKO header = new RegtoppDayCodeHeaderDKO();
		header.setDate(new LocalDate(2016, 1, 1));
		String recomputeTimetableId = ObjectIdCreator.recomputeTimetableId(configuration , "999", tt, header );
	
		Assert.assertEquals(recomputeTimetableId, "TST:Timetable:9993456");
		
	}

}
