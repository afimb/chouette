package mobi.chouette.exchange.netexprofile.parser;

import java.sql.Date;
import java.text.ParseException;
import java.time.OffsetDateTime;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.DayOfWeekEnumeration;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.OperatingDay;
import org.rutebanken.netex.model.OperatingPeriod;
import org.rutebanken.netex.model.OperatingPeriod_VersionStructure;
import org.rutebanken.netex.model.PropertyOfDay;
import org.rutebanken.netex.model.ServiceCalendar;
import org.rutebanken.netex.model.ServiceCalendarFrame;
import org.rutebanken.netex.model.ValidBetween;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

public class ServiceCalendarParser extends NetexParser implements Parser, Constant {

	static final String LOCAL_CONTEXT = "ServiceCalendar";
	static final String VALID_BETWEEN = "validBetween";

	@Override
	public void parse(Context context) throws Exception {
		NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
		ServiceCalendarFrame serviceCalendarFrame = (ServiceCalendarFrame) context.get(NETEX_LINE_DATA_CONTEXT);
		ValidBetween validBetween = getValidBetweenForFrame(context);

		if (serviceCalendarFrame.getDayTypes() != null) {
			for (JAXBElement<? extends DataManagedObjectStructure> dayTypeElement : serviceCalendarFrame.getDayTypes().getDayType_()) {
				DayType dayType = (DayType) dayTypeElement.getValue();
				NetexObjectUtil.addDayTypeRef(netexReferential, dayType.getId(), dayType);
				addValidBetween(context, dayType.getId(), validBetween);
			}
		}
		if (serviceCalendarFrame.getDayTypeAssignments() != null) {
			for (DayTypeAssignment dayTypeAssignment : serviceCalendarFrame.getDayTypeAssignments().getDayTypeAssignment()) {
				String dayTypeIdRef = dayTypeAssignment.getDayTypeRef().getValue().getRef();
				NetexObjectUtil.addDayTypeAssignmentRef(netexReferential, dayTypeIdRef, dayTypeAssignment);
			}
		}
		if (serviceCalendarFrame.getOperatingPeriods() != null) {
			for (OperatingPeriod_VersionStructure operatingPeriodStruct : serviceCalendarFrame.getOperatingPeriods().getOperatingPeriodOrUicOperatingPeriod()) {
				OperatingPeriod operatingPeriod = (OperatingPeriod) operatingPeriodStruct;
				NetexObjectUtil.addOperatingPeriodRef(netexReferential, operatingPeriod.getId(), operatingPeriod);
			}
		}
		if (serviceCalendarFrame.getOperatingDays() != null) {
			for (OperatingDay operatingDay : serviceCalendarFrame.getOperatingDays().getOperatingDay()) {
				NetexObjectUtil.addOperatingDayRef(netexReferential, operatingDay.getId(), operatingDay);
			}
		}

		if (serviceCalendarFrame.getServiceCalendar() != null) {
			ServiceCalendar serviceCalendar = serviceCalendarFrame.getServiceCalendar();
			ValidBetween calendarValidBetween = getValidBetween(context, serviceCalendar);

			if (serviceCalendar.getDayTypes() != null) {
				for (JAXBElement<?> dayTypeElement : serviceCalendar.getDayTypes().getDayTypeRefOrDayType_()) {
					DayType dayType = (DayType) dayTypeElement.getValue();
					NetexObjectUtil.addDayTypeRef(netexReferential, dayType.getId(), dayType);
					addValidBetween(context, dayType.getId(), calendarValidBetween);
				}
			}
			if (serviceCalendar.getDayTypeAssignments() != null) {
				for (DayTypeAssignment dayTypeAssignment : serviceCalendar.getDayTypeAssignments().getDayTypeAssignment()) {
					String dayTypeIdRef = dayTypeAssignment.getDayTypeRef().getValue().getRef();
					NetexObjectUtil.addDayTypeAssignmentRef(netexReferential, dayTypeIdRef, dayTypeAssignment);
				}
			}
			if (serviceCalendar.getOperatingPeriods() != null) {
				for (Object genericOperatingPeriod : serviceCalendar.getOperatingPeriods().getOperatingPeriodRefOrOperatingPeriodOrUicOperatingPeriod()) {
					OperatingPeriod operatingPeriod = (OperatingPeriod) genericOperatingPeriod;
					NetexObjectUtil.addOperatingPeriodRef(netexReferential, operatingPeriod.getId(), operatingPeriod);
				}
			}
			if (serviceCalendar.getOperatingDays() != null) {
				for (Object genericOperatingDay : serviceCalendarFrame.getServiceCalendar().getOperatingDays().getOperatingDayRefOrOperatingDay()) {
					OperatingDay operatingDay = (OperatingDay) genericOperatingDay;
					NetexObjectUtil.addOperatingDayRef(netexReferential, operatingDay.getId(), operatingDay);
				}
			}
		}
		
		convertCalendarToTimetable(context);
	}

	private void convertCalendarToTimetable(Context context) throws ParseException {
		NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
		ValidBetween validBetween = getValidBetweenForFrame(context);
		Referential referential = (Referential) context.get(REFERENTIAL);

		netexReferential.getDayTypes().values();

		for (DayType dayType : netexReferential.getDayTypes().values()) {

			Timetable timetable = ObjectFactory.getTimetable(referential, dayType.getId());
			timetable.setStartOfPeriod(ParserUtils.getSQLDate(validBetween.getFromDate().toString()));
			timetable.setEndOfPeriod(ParserUtils.getSQLDate(validBetween.getToDate().toString()));
			if (dayType.getProperties() != null) {
				for (PropertyOfDay propertyOfDay : dayType.getProperties().getPropertyOfDay()) {
					List<DayOfWeekEnumeration> daysOfWeeks = propertyOfDay.getDaysOfWeek();

					for (DayOfWeekEnumeration dayOfWeek : daysOfWeeks) {
						List<DayTypeEnum> dayTypeEnums = NetexParserUtils.convertDayOfWeek(dayOfWeek);

						for (DayTypeEnum dayTypeEnum : dayTypeEnums) {
							timetable.addDayType(dayTypeEnum);
						}
					}
				}
			}
		}
		for (DayTypeAssignment dayTypeAssignment : netexReferential.getDayTypeAssignments()) {

			String dayTypeIdRef = dayTypeAssignment.getDayTypeRef().getValue().getRef();
			Timetable timetable = ObjectFactory.getTimetable(referential, dayTypeIdRef);

			if (dayTypeAssignment.getDate() != null) {
				OffsetDateTime date = dayTypeAssignment.getDate();

				if (isWithinValidRange(date, validBetween)) {
					boolean included = dayTypeAssignment.isIsAvailable() != null ? dayTypeAssignment.isIsAvailable() : Boolean.TRUE;
					timetable.addCalendarDay(new CalendarDay(java.sql.Date.valueOf(date.toLocalDate()), included));
				}
			} else if (dayTypeAssignment.getOperatingDayRef() != null) {
				String operatingDayIdRef = dayTypeAssignment.getOperatingDayRef().getRef();
				OperatingDay operatingDay = NetexObjectUtil.getOperatingDay(netexReferential, operatingDayIdRef);

				if (operatingDay.getCalendarDate() != null && isWithinValidRange(operatingDay.getCalendarDate(), validBetween)) {
					boolean included = dayTypeAssignment.isIsAvailable() != null ? dayTypeAssignment.isIsAvailable() : Boolean.TRUE;
					timetable.addCalendarDay(new CalendarDay(java.sql.Date.valueOf(operatingDay.getCalendarDate().toLocalDate()), included));
				}

			} else if (dayTypeAssignment.getOperatingPeriodRef() != null) {
				String operatingPeriodIdRef = dayTypeAssignment.getOperatingPeriodRef().getRef();
				OperatingPeriod operatingPeriod = NetexObjectUtil.getOperatingPeriod(netexReferential, operatingPeriodIdRef);

				Date startDate;
				Date endDate;

				if (operatingPeriod.getFromOperatingDayRef() != null) {
					OperatingDay operatingDay = NetexObjectUtil.getOperatingDay(netexReferential, operatingPeriod.getFromOperatingDayRef().getRef());
					startDate = ParserUtils.getSQLDate(operatingDay.getCalendarDate().toString());
				} else {
					startDate = ParserUtils.getSQLDate(operatingPeriod.getFromDate().toString());
				}
				if (operatingPeriod.getToOperatingDayRef() != null) {
					OperatingDay operatingDay = NetexObjectUtil.getOperatingDay(netexReferential, operatingPeriod.getToOperatingDayRef().getRef());
					endDate = ParserUtils.getSQLDate(operatingDay.getCalendarDate().toString());
				} else {
					endDate = ParserUtils.getSQLDate(operatingPeriod.getToDate().toString());
				}

				timetable.addPeriod(new Period(startDate, endDate));
			}
		}

	}

	private boolean isWithinValidRange(OffsetDateTime dateOfOperation, ValidBetween validBetween) {
		return !dateOfOperation.isBefore(validBetween.getFromDate()) && !dateOfOperation.isAfter(validBetween.getToDate());
	}

	private ValidBetween getValidBetweenForFrame(Context context) {
		Context parsingContext = (Context) context.get(PARSING_CONTEXT);
		Context publicationDeliveryContext = (Context) parsingContext.get(PublicationDeliveryParser.LOCAL_CONTEXT);

		ValidBetween calendarFrameValidity = (ValidBetween) publicationDeliveryContext.get(PublicationDeliveryParser.SERVICE_CALENDAR_FRAME);
		if (calendarFrameValidity != null) {
			return calendarFrameValidity;
		} else {
			ValidBetween compositeFrameValidity = (ValidBetween) publicationDeliveryContext.get(PublicationDeliveryParser.COMPOSITE_FRAME);
			if (compositeFrameValidity != null) {
				return compositeFrameValidity;
			}
		}

		return null;
	}

	private ValidBetween getValidBetween(Context context, ServiceCalendar serviceCalendar) throws Exception {
		Context parsingContext = (Context) context.get(PARSING_CONTEXT);
		Context publicationDeliveryContext = (Context) parsingContext.get(PublicationDeliveryParser.LOCAL_CONTEXT);

		if (serviceCalendar.getFromDate() != null && serviceCalendar.getToDate() != null) {
			OffsetDateTime fromDateTime = serviceCalendar.getFromDate();
			OffsetDateTime toDateTime = serviceCalendar.getToDate();
			return new ValidBetween().withFromDate(fromDateTime).withToDate(toDateTime);
		} else {
			ValidBetween entityValidity = getValidBetween(serviceCalendar);
			if (entityValidity != null) {
				return entityValidity;
			} else {
				ValidBetween calendarFrameValidity = (ValidBetween) publicationDeliveryContext.get(PublicationDeliveryParser.SERVICE_CALENDAR_FRAME);
				if (calendarFrameValidity != null) {
					return calendarFrameValidity;
				} else {
					ValidBetween compositeFrameValidity = (ValidBetween) publicationDeliveryContext.get(PublicationDeliveryParser.COMPOSITE_FRAME);
					if (compositeFrameValidity != null) {
						return compositeFrameValidity;
					}
				}
			}
		}

		return null;
	}

	private void addValidBetween(Context context, String objectId, ValidBetween validBetween) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(VALID_BETWEEN, validBetween);
	}

	static {
		ParserFactory.register(ServiceCalendarParser.class.getName(), new ParserFactory() {
			private ServiceCalendarParser instance = new ServiceCalendarParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
