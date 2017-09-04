package mobi.chouette.exchange.netexprofile.parser;

import static mobi.chouette.common.Constant.REFERENTIAL;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.NetexprofileImportParameters;
import mobi.chouette.exchange.netexprofile.jaxb.NetexXMLProcessingHelperFactory;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.util.Referential;

public class ServiceCalendarFrameParserTest {

	private NetexXMLProcessingHelperFactory netexImporter = new NetexXMLProcessingHelperFactory();

	private PublicationDeliveryParser parser = new PublicationDeliveryParser();

	@Test
	public void testParseWithoutServiceCalendarOpenEndedValidityCondition() throws Exception {

		Referential referential = new Referential();

		parseIntoReferential(referential, "src/test/data/ServiceCalendarFrameWithOpenEnd.xml");

		Timetable t1 = referential.getTimetables().get("KOL:DayType:1");
		Assert.assertNotNull(t1);
		Assert.assertEquals(t1.getStartOfPeriod(), org.joda.time.LocalDate.parse("2017-08-18")); // Start date in validity condition
		Assert.assertEquals(t1.getEndOfPeriod(), org.joda.time.LocalDate.parse("2017-12-23")); // Limited by last date in dayTypeAssignments

	}

	@Test
	public void testParseWithoutServiceCalendarOpenStartValidityCondition() throws Exception {

		Referential referential = new Referential();

		parseIntoReferential(referential, "src/test/data/ServiceCalendarFrameWithOpenStart.xml");

		Timetable t2 = referential.getTimetables().get("KOL:DayType:2");
		Assert.assertNotNull(t2);
		Assert.assertEquals(t2.getStartOfPeriod(), org.joda.time.LocalDate.parse("2017-10-27")); // Start date as first usable date in calendar
		Assert.assertEquals(t2.getEndOfPeriod(), org.joda.time.LocalDate.parse("2017-11-01")); // End date in validitiy condition
	}

	@Test
	public void testParseWithoutServiceCalendarWithCompleteValidityCondition() throws Exception {

		Referential referential = new Referential();

		parseIntoReferential(referential, "src/test/data/ServiceCalendarFrameWithCompleteValidityCondition.xml");

		Timetable t2 = referential.getTimetables().get("KOL:DayType:2");
		Assert.assertNotNull(t2);
		Assert.assertEquals(t2.getStartOfPeriod(), org.joda.time.LocalDate.parse("2017-08-18")); // Start date as start date in validity condition
		Assert.assertEquals(t2.getEndOfPeriod(), org.joda.time.LocalDate.parse("2017-11-01")); // End date in validitiy condition
	}

	@Test(enabled = false)
	// TODO validity condition overrides are broken
	public void testParseServiceCalendarWithDaytypesOutsideOfServiceCalendarBoundary() throws Exception {

		Referential referential = new Referential();

		parseIntoReferential(referential, "src/test/data/ServiceCalendarFrameWithServiceCalendarDatesOutsideCalendarBoundary.xml");

		Timetable t2 = referential.getTimetables().get("KOL:DayType:2");
		Assert.assertNotNull(t2);
		Assert.assertEquals(t2.getStartOfPeriod(), org.joda.time.LocalDate.parse("2016-10-17")); // Start date of service calendar
		Assert.assertEquals(t2.getEndOfPeriod(), org.joda.time.LocalDate.parse("2016-12-23")); // End date in service calendar
		Assert.assertEquals(t2.getEffectiveDates().size(), 0);
	}

	@Test
	// TODO validity condition overrides are broken
	public void testParseServiceCalendarWithDatesOutsideOfValditityCondition() throws Exception {

		Referential referential = new Referential();

		parseIntoReferential(referential, "src/test/data/ServiceCalendarFrameWithServiceCalendar.xml");

		Timetable t2 = referential.getTimetables().get("BRA:DayType:2");
		Assert.assertNotNull(t2);
		Assert.assertEquals(t2.getStartOfPeriod(), org.joda.time.LocalDate.parse("2016-09-19")); // Start date of service calendar
		Assert.assertEquals(t2.getEndOfPeriod(), org.joda.time.LocalDate.parse("2016-12-22")); // End date in service calendar // TODO 1 day off, must be fixed when date/time parsing is on local time
		Assert.assertEquals(t2.getEffectiveDates().size(), 0);
	}

	protected void parseIntoReferential(Referential referential, String netedFilePath)
			throws JAXBException, XMLStreamException, IOException, SAXException, Exception {
		Context context = new Context();
		context.put(REFERENTIAL, referential);

		NetexReferential netexReferential = new NetexReferential();
		context.put(Constant.NETEX_REFERENTIAL, netexReferential);

		context.put(Constant.NETEX_WITH_COMMON_DATA, true);
		context.put(Constant.CONFIGURATION, new NetexprofileImportParameters());

		PublicationDeliveryStructure pubDelivery = netexImporter.unmarshal(new File(netedFilePath), new HashSet<>());

		context.put(Constant.NETEX_DATA_JAVA, pubDelivery);

		parser.parse(context);
	}

}
