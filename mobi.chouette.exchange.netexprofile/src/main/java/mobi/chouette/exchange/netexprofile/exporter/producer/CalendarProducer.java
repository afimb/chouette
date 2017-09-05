package mobi.chouette.exchange.netexprofile.exporter.producer;

import com.google.common.base.Joiner;
import mobi.chouette.common.Context;
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Line;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.xml.bind.JAXBElement;

import static mobi.chouette.exchange.netexprofile.exporter.producer.CalendarProducer.DAY_TYPES_KEY;
import static mobi.chouette.exchange.netexprofile.exporter.producer.CalendarProducer.DAY_TYPE_ASSIGNMENTS_KEY;
import static mobi.chouette.exchange.netexprofile.exporter.producer.CalendarProducer.OPERATING_PERIODS_KEY;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.*;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class CalendarProducer extends NetexProducer {

	public static final String DAY_TYPES_KEY = "DayTypes";
	public static final String DAY_TYPE_ASSIGNMENTS_KEY = "DayTypeAssignments";
	public static final String OPERATING_PERIODS_KEY = "OperatingPeriods";

	static final String LOCAL_CONTEXT = "ServiceCalendar";
	static final String DAY_TYPE_IDS = "dayTypeIds";

	private static final String DAY_TYPE_PATTERN = "MMM_EEE_dd";

	public void produce(Context context, ExportableData exportableData, ExportableNetexData exportableNetexData) {

		for (Timetable timetable : exportableData.getTimetables()) {

			String netexDaytypeId = NetexProducerUtils.generateNetexId(timetable);
			if (!exportableNetexData.getDayTypes().containsKey(netexDaytypeId)) {
				DayType dayType = netexFactory.createDayType();
				NetexProducerUtils.populateId(timetable, dayType);

				List<DayOfWeekEnumeration> dayOfWeekEnumerations = NetexProducerUtils.toDayOfWeekEnumeration(timetable.getDayTypes());
				if (!dayOfWeekEnumerations.isEmpty()) {
					dayType.setProperties(createPropertiesOfDay_RelStructure(dayOfWeekEnumerations));
				}

				exportableNetexData.getDayTypes().put(netexDaytypeId, dayType);

				DayTypeRefStructure dayTypeRef = netexFactory.createDayTypeRefStructure();
				NetexProducerUtils.populateReference(timetable, dayTypeRef, true);

				int counter = 0; // Used for creating unique dayTypeAssignments
				// Operating periods
				for (int i = 0; i < timetable.getPeriods().size(); i++) {
					counter++;

					Period p = timetable.getPeriods().get(i);
					// Create Operating period
					OperatingPeriod operatingPeriod = new OperatingPeriod().withVersion(dayType.getVersion())
							.withId(NetexProducerUtils.translateObjectId(netexDaytypeId, "OperatingPeriod"))
							.withFromDate(TimeUtil.toOffsetDateTime(p.getStartDate())).withToDate(TimeUtil.toOffsetDateTime(p.getEndDate()));
					exportableNetexData.getOperatingPeriods().add(operatingPeriod);

					OperatingPeriodRefStructure operatingPeriodRef = netexFactory.createOperatingPeriodRefStructure();
					NetexProducerUtils.populateReference(operatingPeriod, operatingPeriodRef, true);

					// Assign operatingperiod to daytype
					DayTypeAssignment dayTypeAssignment = netexFactory.createDayTypeAssignment()
							.withId(NetexProducerUtils.translateObjectId(netexDaytypeId, "DayTypeAssignment") + "-" + counter).withVersion("1")
							.withOrder(BigInteger.ONE).withDayTypeRef(netexFactory.createDayTypeRef(dayTypeRef)).withOperatingPeriodRef(operatingPeriodRef);
					exportableNetexData.getDayTypeAssignments().add(dayTypeAssignment);

				}

				for (CalendarDay day : timetable.getCalendarDays()) {
					counter++;

					DayTypeAssignment dayTypeAssignment = netexFactory.createDayTypeAssignment()
							.withId(NetexProducerUtils.translateObjectId(netexDaytypeId, "DayTypeAssignment") + "-" + counter).withVersion("1")
							.withOrder(BigInteger.ONE).withDayTypeRef(netexFactory.createDayTypeRef(dayTypeRef))
							.withDate(TimeUtil.toOffsetDateTime(day.getDate()));

					if (day.getIncluded() != null && !day.getIncluded()) {
						day.setIncluded(day.getIncluded());
					}
					exportableNetexData.getDayTypeAssignments().add(dayTypeAssignment);
				}

			}
		}

	}

	private PropertiesOfDay_RelStructure createPropertiesOfDay_RelStructure(List<DayOfWeekEnumeration> dayOfWeekEnumerations) {
		PropertyOfDay propertyOfDay = netexFactory.createPropertyOfDay();
		for (DayOfWeekEnumeration dayOfWeekEnumeration : dayOfWeekEnumerations) {
			propertyOfDay.getDaysOfWeek().add(dayOfWeekEnumeration);
		}

		PropertiesOfDay_RelStructure propertiesOfDay = netexFactory.createPropertiesOfDay_RelStructure();
		propertiesOfDay.getPropertyOfDay().add(propertyOfDay);
		return propertiesOfDay;
	}

}
