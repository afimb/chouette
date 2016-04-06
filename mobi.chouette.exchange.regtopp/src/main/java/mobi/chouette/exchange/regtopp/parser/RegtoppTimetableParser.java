package mobi.chouette.exchange.regtopp.parser;

import org.joda.time.LocalDate;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeHeaderDKO;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.DaycodeById;
import mobi.chouette.exchange.regtopp.validation.Constant;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppTimetableParser implements Parser, Validator, Constant {

	@Override
	public void validate(Context context) throws Exception {

		// Konsistenssjekker, kjøres før parse-metode.

		// Det som kan sjekkes her er at antall poster stemmer og at alle referanser til andre filer er gyldige

	
	}

	@Override
	public void parse(Context context) throws Exception {

		// Her tar vi allerede konsistenssjekkede data (ref validate-metode over) og bygger opp tilsvarende struktur i chouette.
		// Merk at import er linje-sentrisk, så man skal i denne klassen returnerer 1 line med x antall routes og stoppesteder, journeypatterns osv

		Referential referential = (Referential) context.get(REFERENTIAL);

		// Clear any previous data as this referential is reused / TODO

		// Add all calendar entries to referential
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);
		DaycodeById dayCodeIndex = (DaycodeById) importer.getDayCodeById();

		RegtoppDayCodeHeaderDKO header = dayCodeIndex.getHeader();
		LocalDate calStartDate = header.getDate();

		// TODO try to find patterns (mon-fri, weekends etc)

		// TODO 2 ?- find end date of calendars, 392 is the max number of entries allowed (13 months approx)

		for (RegtoppDayCodeDKO entry : dayCodeIndex) {
			String chouetteTimetableId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.TIMETABLE_KEY, entry.getDayCodeId(), log);

			Timetable timetable = ObjectFactory.getTimetable(referential, chouetteTimetableId);

			java.sql.Date startDate = new java.sql.Date(calStartDate.toDateMidnight().toDate().getTime());
			java.sql.Date endDate = new java.sql.Date(calStartDate.plusDays(392).toDateMidnight().toDate().getTime());

			timetable.setStartOfPeriod(startDate);
			timetable.setEndOfPeriod(endDate);
					
//			timetable.addDayType(DayTypeEnum.Monday);
//			timetable.addDayType(DayTypeEnum.Tuesday);
//			timetable.addDayType(DayTypeEnum.Wednesday);
//			timetable.addDayType(DayTypeEnum.Thursday);
//			timetable.addDayType(DayTypeEnum.Friday);
//			timetable.addDayType(DayTypeEnum.Saturday);
//			timetable.addDayType(DayTypeEnum.Sunday);

			Period period = new Period(startDate, endDate);
			timetable.getPeriods().add(period);

			String includedArray = entry.getDayCode();

			for (int i = 0; i < 392; i++) {
				java.sql.Date currentDate = new java.sql.Date(calStartDate.plusDays(i).toDateMidnight().toDate().getTime());
				boolean included = includedArray.charAt(i) == '1';
				if(included) {
					log.info("Including in calendar: "+currentDate);
					timetable.addCalendarDay(new CalendarDay(currentDate, included));
				} else {
					log.info("Skipping excluded day in calendar: "+currentDate);
				}
			}

			
			
			NamingUtil.setDefaultName(timetable);
			timetable.setFilled(true);

			log.info("Adding timetable "+timetable);
		}
		
		

	}

	static {
		ParserFactory.register(RegtoppTimetableParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppTimetableParser();
			}
		});
	}

}
