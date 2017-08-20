package mobi.chouette.exchange.netexprofile.parser;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.rutebanken.helper.calendar.CalendarPattern;
import org.rutebanken.helper.calendar.CalendarPatternAnalyzer;
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
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

public class ServiceCalendarFrameParser extends NetexParser implements Parser, Constant {

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
		findSignificantDaysForTimetables(context);
	}

	private void findSignificantDaysForTimetables(Context context) {
		Referential referential = (Referential) context.get(REFERENTIAL);

		for (Timetable t : referential.getSharedTimetables().values()) {
			if (t.getPeriods().size() == 0 && t.getDayTypes().size() == 0 && t.getPeculiarDates().size() > 0 && t.getExcludedDates().size() == 0) {
				// Only handle simple included days for now

				List<org.joda.time.LocalDate> includedDates = t.getPeculiarDates();
				Set<LocalDate> includedDays = new HashSet<LocalDate>();
				for (org.joda.time.LocalDate d : includedDates) {
					includedDays.add(TimeUtil.toLocalDateFromJoda(d));
				}

				CalendarPattern pattern = new CalendarPatternAnalyzer().computeCalendarPattern(includedDays);
				
				
				if (pattern != null && !pattern.significantDays.isEmpty()) {

					// Remove and re-add
					t.getCalendarDays().clear();

					// Add the period detected
					org.joda.time.LocalDate from = TimeUtil.toJodaLocalDate(pattern.from);
					org.joda.time.LocalDate to = TimeUtil.toJodaLocalDate(pattern.to);
					t.addPeriod(new Period(from, to));

					// Convert from java.time.DayOfWeek to chouette DayTypeEnum
					Set<DayTypeEnum> significantDayTypes = new HashSet<>();
					for (DayOfWeek d : pattern.significantDays) {
						significantDayTypes.add(convertFromDayOfWeek(d));
					}

					// Add day types
					for (DayTypeEnum dayType : significantDayTypes) {
						t.addDayType(dayType);
					}

					// Add extra inclusions and exclusions
					for (LocalDate d : pattern.additionalDates) {
						t.addCalendarDay(new CalendarDay(TimeUtil.toJodaLocalDate(d), true));
					}
					for (LocalDate d : pattern.excludedDates) {
						t.addCalendarDay(new CalendarDay(TimeUtil.toJodaLocalDate(d), false));
					}
				}
			}

		}

	}


	private DayTypeEnum convertFromDayOfWeek(DayOfWeek dayType) {
		switch (dayType) {
		case MONDAY:
			return DayTypeEnum.Monday;
		case TUESDAY:
			return DayTypeEnum.Tuesday;
		case WEDNESDAY:
			return DayTypeEnum.Wednesday;
		case THURSDAY:
			return DayTypeEnum.Thursday;
		case FRIDAY:
			return DayTypeEnum.Friday;
		case SATURDAY:
			return DayTypeEnum.Saturday;
		case SUNDAY:
			return DayTypeEnum.Sunday;
		default:
			return null;
		}

	}

	private void convertCalendarToTimetable(Context context) throws ParseException {
		NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
		ValidBetween validBetween = getValidBetweenForFrame(context);
		Referential referential = (Referential) context.get(REFERENTIAL);

		for (DayType dayType : netexReferential.getDayTypes().values()) {

			Timetable timetable = ObjectFactory.getTimetable(referential, dayType.getId());
			if(validBetween.getFromDate() != null) {
				timetable.setStartOfPeriod(TimeUtil.toJodaLocalDateTime(validBetween.getFromDate()).toLocalDate());
			}

			if(validBetween.getToDate() != null) {
				timetable.setEndOfPeriod(TimeUtil.toJodaLocalDateTime(validBetween.getToDate()).toLocalDate());
			}
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
					timetable.addCalendarDay(new CalendarDay(TimeUtil.toJodaLocalDate(date.toLocalDate()), included));
				}
			} else if (dayTypeAssignment.getOperatingDayRef() != null) {
				String operatingDayIdRef = dayTypeAssignment.getOperatingDayRef().getRef();
				OperatingDay operatingDay = NetexObjectUtil.getOperatingDay(netexReferential, operatingDayIdRef);

				if (operatingDay.getCalendarDate() != null && isWithinValidRange(operatingDay.getCalendarDate(), validBetween)) {
					boolean included = dayTypeAssignment.isIsAvailable() != null ? dayTypeAssignment.isIsAvailable() : Boolean.TRUE;
					timetable.addCalendarDay(new CalendarDay(TimeUtil.toJodaLocalDate(operatingDay.getCalendarDate().toLocalDate()), included));
				}

			} else if (dayTypeAssignment.getOperatingPeriodRef() != null) {
				String operatingPeriodIdRef = dayTypeAssignment.getOperatingPeriodRef().getRef();
				OperatingPeriod operatingPeriod = NetexObjectUtil.getOperatingPeriod(netexReferential, operatingPeriodIdRef);

				org.joda.time.LocalDate startDate;
				org.joda.time.LocalDate endDate;

				if (operatingPeriod.getFromOperatingDayRef() != null) {
					OperatingDay operatingDay = NetexObjectUtil.getOperatingDay(netexReferential, operatingPeriod.getFromOperatingDayRef().getRef());
					startDate = TimeUtil.toJodaLocalDateTime(operatingDay.getCalendarDate()).toLocalDate();
				} else {
					startDate = TimeUtil.toJodaLocalDateTime(operatingPeriod.getFromDate()).toLocalDate();
				}
				if (operatingPeriod.getToOperatingDayRef() != null) {
					OperatingDay operatingDay = NetexObjectUtil.getOperatingDay(netexReferential, operatingPeriod.getToOperatingDayRef().getRef());
					endDate = TimeUtil.toJodaLocalDateTime(operatingDay.getCalendarDate()).toLocalDate();
				} else {
					endDate = TimeUtil.toJodaLocalDateTime(operatingPeriod.getToDate()).toLocalDate();
				}

				// Cut of operating period to validity condition
				org.joda.time.LocalDate validFrom = new org.joda.time.LocalDate(validBetween.getFromDate().toInstant().toEpochMilli());
				org.joda.time.LocalDate validTo = new org.joda.time.LocalDate(validBetween.getToDate().toInstant().toEpochMilli());
				
				if(endDate.isBefore(validFrom) || startDate.isAfter(validTo)) {
					// Outside of validFrom/to envelope
				} else {
					// At least partially inside envelope
					if(startDate.isBefore(validFrom)) {
						startDate = validFrom;
					}
					if(endDate.isAfter(validTo)) {
						endDate = validTo;
					}
					
					
					timetable.addPeriod(new Period(startDate, endDate));
				}
				
			}
		}
		
		for(Timetable t : referential.getTimetables().values()) {
			if(t.getStartOfPeriod() == null || t.getEndOfPeriod() == null) {
				List<org.joda.time.LocalDate> effectiveDates = t.getEffectiveDates();
				if(effectiveDates.size() > 0) {
					Collections.sort(effectiveDates);
					if(t.getStartOfPeriod() == null) {
						t.setStartOfPeriod(effectiveDates.get(0));
					}
					if(t.getEndOfPeriod() == null) {
						t.setEndOfPeriod(effectiveDates.get(effectiveDates.size()-1));
					}
				}
			}
		}

	}

	private boolean isWithinValidRange(OffsetDateTime dateOfOperation, ValidBetween validBetween) {
		if(validBetween == null) {
			// Always valid
			return true;
		} else if(validBetween.getFromDate() != null && validBetween.getToDate() != null) {
			// Limited by both from and to date
			return !dateOfOperation.isBefore(validBetween.getFromDate()) && !dateOfOperation.isAfter(validBetween.getToDate());
		} else if(validBetween.getFromDate() != null) {
			// Must be after valid start date
			return !dateOfOperation.isBefore(validBetween.getFromDate());
		} else if(validBetween.getToDate() != null){
			// Must be before valid start date
			return dateOfOperation.isBefore(validBetween.getToDate());
		} else {
			// Both from and to empty
			return true;
		}
		
		
		
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
		ParserFactory.register(ServiceCalendarFrameParser.class.getName(), new ParserFactory() {
			private ServiceCalendarFrameParser instance = new ServiceCalendarFrameParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
