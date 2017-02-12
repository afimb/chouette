package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.model.*;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.Network;

import javax.xml.bind.JAXBElement;
import java.time.LocalDate;
import java.util.*;

@Log4j
public class PublicationDeliveryParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {
		boolean isCommonDelivery = context.get(NETEX_WITH_COMMON_DATA) != null && context.get(NETEX_LINE_DATA_JAVA) == null;
		Referential referential = (Referential) context.get(REFERENTIAL);
        String contextKey = isCommonDelivery ? NETEX_COMMON_DATA_JAVA : NETEX_LINE_DATA_JAVA;
		PublicationDeliveryStructure publicationDelivery = (PublicationDeliveryStructure) context.get(contextKey);
		List<JAXBElement<? extends Common_VersionFrameStructure>> dataObjectFrames = publicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame();

		List<CompositeFrame> compositeFrames = NetexObjectUtil.getFrames(CompositeFrame.class, dataObjectFrames);
		if(compositeFrames.size() > 0) {
			
			// Parse inside a composite frame
			for(CompositeFrame compositeFrame : compositeFrames) {
				List<JAXBElement<? extends Common_VersionFrameStructure>> frames = compositeFrame.getFrames().getCommonFrame();
				List<ResourceFrame> resourceFrames = NetexObjectUtil.getFrames(ResourceFrame.class, frames);
				List<ServiceFrame> serviceFrames = NetexObjectUtil.getFrames(ServiceFrame.class, frames);
				List<SiteFrame> siteFrames = NetexObjectUtil.getFrames(SiteFrame.class, frames);
				List<ServiceCalendarFrame> serviceCalendarFrames = NetexObjectUtil.getFrames(ServiceCalendarFrame.class, frames);
				List<TimetableFrame> timetableFrames = new ArrayList<>();

				if (!isCommonDelivery) {
					timetableFrames = NetexObjectUtil.getFrames(TimetableFrame.class, frames);
				}

				// pre processing
				preParseReferentialDependencies(context, serviceFrames ,compositeFrame, isCommonDelivery);

				// normal processing
				parseResourceFrames(context, resourceFrames,compositeFrame);
				parseSiteFrames(context, siteFrames,compositeFrame);
				parseServiceFrames(context, serviceFrames ,compositeFrame, isCommonDelivery);
				parseServiceCalendarFrame(context, serviceCalendarFrames ,compositeFrame);

				if (!isCommonDelivery) {
					parseTimetableFrames(context, timetableFrames,compositeFrame);
				}
				
			}
		} else {
			// Not using composite frame
			List<ResourceFrame> resourceFrames = NetexObjectUtil.getFrames(ResourceFrame.class, dataObjectFrames);
			List<ServiceFrame> serviceFrames = NetexObjectUtil.getFrames(ServiceFrame.class, dataObjectFrames);
			List<SiteFrame> siteFrames = NetexObjectUtil.getFrames(SiteFrame.class, dataObjectFrames);
			List<ServiceCalendarFrame> serviceCalendarFrames = NetexObjectUtil.getFrames(ServiceCalendarFrame.class, dataObjectFrames);
			List<TimetableFrame> timetableFrames = new ArrayList<>();

			if (!isCommonDelivery) {
				timetableFrames = NetexObjectUtil.getFrames(TimetableFrame.class, dataObjectFrames);
			}

			// pre processing
			preParseReferentialDependencies(context, serviceFrames, null, isCommonDelivery);

			// normal processing
			parseResourceFrames(context, resourceFrames,null);
			parseSiteFrames(context, siteFrames,null);
			parseServiceFrames(context, serviceFrames, null,isCommonDelivery);
			parseServiceCalendarFrame(context, serviceCalendarFrames,null);

			if (!isCommonDelivery) {
				parseTimetableFrames(context, timetableFrames,null);
			}
		}
		
		

		// post processing
		sortStopPoints(referential);
		updateBoardingAlighting(referential);
	}

	@SuppressWarnings("unchecked")
	private void preParseReferentialDependencies(Context context, List<ServiceFrame> serviceFrames, CompositeFrame compositeFrame, boolean isCommonDelivery) throws Exception {
		Map<String, String> stopAssignments = (Map<String, String>) context.get(NETEX_STOP_ASSIGNMENTS);
		if (stopAssignments == null) {
			stopAssignments = new HashMap<>();
			context.put(NETEX_STOP_ASSIGNMENTS, stopAssignments);
		}
		for (ServiceFrame serviceFrame : serviceFrames) {
			StopAssignmentsInFrame_RelStructure stopAssignmentsStructure = serviceFrame.getStopAssignments();
			if (stopAssignmentsStructure != null) {
				List<JAXBElement<? extends StopAssignment_VersionStructure>> stopAssignmentElements = stopAssignmentsStructure.getStopAssignment();

				for (JAXBElement<? extends StopAssignment_VersionStructure> stopAssignmentElement : stopAssignmentElements) {
					PassengerStopAssignment passengerStopAssignment = (PassengerStopAssignment) stopAssignmentElement.getValue();
					ScheduledStopPointRefStructure scheduledStopPointRef = passengerStopAssignment.getScheduledStopPointRef();

					QuayRefStructure quayRef = passengerStopAssignment.getQuayRef();
					if (scheduledStopPointRef != null && quayRef != null) {
					    if (!stopAssignments.containsKey(scheduledStopPointRef.getRef())) {
                            stopAssignments.put(scheduledStopPointRef.getRef(), quayRef.getRef());
                        }
					}
				}
			}

			if (!isCommonDelivery) {

				// preparsing mandatory for stop places to parse correctly
				TariffZonesInFrame_RelStructure tariffZonesStruct = serviceFrame.getTariffZones();
				if (tariffZonesStruct != null) {
					context.put(NETEX_LINE_DATA_CONTEXT, tariffZonesStruct);
					StopPlaceParser stopPlaceParser = (StopPlaceParser) ParserFactory.create(StopPlaceParser.class.getName());
					stopPlaceParser.parse(context);
				}
			} else {

			}
        }
    }

    private void parseResourceFrames(Context context, List<ResourceFrame> resourceFrames, CompositeFrame compositeFrame) throws Exception {
		for (ResourceFrame resourceFrame : resourceFrames) {
			OrganisationsInFrame_RelStructure organisationsInFrameStruct = resourceFrame.getOrganisations();
			if (organisationsInFrameStruct != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, organisationsInFrameStruct);
				OrganisationParser organisationParser = (OrganisationParser) ParserFactory.create(OrganisationParser.class.getName());
				organisationParser.parse(context);
			}
		}
	}

	private void parseSiteFrames(Context context, List<SiteFrame> siteFrames, CompositeFrame compositeFrame) throws Exception {
		for (SiteFrame siteFrame : siteFrames) {
            StopPlacesInFrame_RelStructure stopPlacesStruct = siteFrame.getStopPlaces();
			if (stopPlacesStruct != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, stopPlacesStruct);
				StopPlaceParser stopPlaceParser = (StopPlaceParser) ParserFactory.create(StopPlaceParser.class.getName());
				stopPlaceParser.parse(context);
			}
		}
	}

	private void parseServiceFrames(Context context, List<ServiceFrame> serviceFrames, CompositeFrame compositeFrame, boolean isCommonDelivery) throws Exception {
		for (ServiceFrame serviceFrame : serviceFrames) {
			if (!isCommonDelivery) {
				Network network = serviceFrame.getNetwork();
				context.put(NETEX_LINE_DATA_CONTEXT, network);
				NetworkParser networkParser = (NetworkParser) ParserFactory.create(NetworkParser.class.getName());
				networkParser.parse(context);

				LinesInFrame_RelStructure linesInFrameStruct = serviceFrame.getLines();
				context.put(NETEX_LINE_DATA_CONTEXT, linesInFrameStruct);
				LineParser lineParser = (LineParser) ParserFactory.create(LineParser.class.getName());
				lineParser.parse(context);

				RoutesInFrame_RelStructure routesInFrameStruct = serviceFrame.getRoutes();
				context.put(NETEX_LINE_DATA_CONTEXT, routesInFrameStruct);
				RouteParser routeParser = (RouteParser) ParserFactory.create(RouteParser.class.getName());
				routeParser.parse(context);
			}

			if (!isCommonDelivery) {
				JourneyPatternsInFrame_RelStructure journeyPatternStruct = serviceFrame.getJourneyPatterns();
				context.put(NETEX_LINE_DATA_CONTEXT, journeyPatternStruct);
                JourneyParser journeyParser = (JourneyParser) ParserFactory.create(JourneyParser.class.getName());
                journeyParser.parse(context);

				TransfersInFrame_RelStructure connectionsStruct = serviceFrame.getConnections();
				if (connectionsStruct != null) {
					// TODO implement connection link parser
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void parseServiceCalendarFrame(Context context, List<ServiceCalendarFrame> serviceCalendarFrames, CompositeFrame compositeFrame) throws Exception {
		Referential referential = (Referential) context.get(REFERENTIAL);
		Map<String, LocalDate> operatingDays = new HashMap<>();

		for (ServiceCalendarFrame serviceCalendarFrame : serviceCalendarFrames) {
			DayTypeAssignmentsInFrame_RelStructure dayTypeAssignmentStruct = serviceCalendarFrame.getDayTypeAssignments();

			if (dayTypeAssignmentStruct != null) {
				List<DayTypeAssignment> dayTypeAssignments = dayTypeAssignmentStruct.getDayTypeAssignment();

				for (DayTypeAssignment dayTypeAssignment : dayTypeAssignments) {
					JAXBElement<? extends DayTypeRefStructure> dayTypeRefElement = dayTypeAssignment.getDayTypeRef();
					LocalDate dateOfOperation = dayTypeAssignment.getDate();

					if (dayTypeRefElement != null && dateOfOperation != null) {
						String dayTypeIdRef = dayTypeRefElement.getValue().getRef();

						if (!operatingDays.containsKey(dayTypeIdRef)) {
							operatingDays.put(dayTypeIdRef, dateOfOperation);
						}
					}
				}
			}

			DayTypesInFrame_RelStructure dayTypesStruct = serviceCalendarFrame.getDayTypes();

			if (dayTypesStruct != null) {
				List<JAXBElement<? extends DataManagedObjectStructure>> dayTypeElements = dayTypesStruct.getDayType_();

				for (JAXBElement<? extends DataManagedObjectStructure> dayTypeElement : dayTypeElements) {
					// For each timetable

					DayType dayType = (DayType) dayTypeElement.getValue();
					Timetable timetable = ObjectFactory.getTimetable(referential, dayType.getId());

					// check if day type has properties, if not look for assignments with explicit dates
					PropertiesOfDay_RelStructure propertiesOfDayStruct = dayType.getProperties();

					if (propertiesOfDayStruct != null && !propertiesOfDayStruct.getPropertyOfDay().isEmpty()) {
						List<PropertyOfDay> propertyOfDayList = propertiesOfDayStruct.getPropertyOfDay();

						for (PropertyOfDay propertyOfDay : propertyOfDayList) {
							List<DayOfWeekEnumeration> daysOfWeeks = propertyOfDay.getDaysOfWeek();
							for(DayOfWeekEnumeration dayOfWeek : daysOfWeeks) {
								List<DayTypeEnum> convertDayOfWeek = NetexUtils.convertDayOfWeek(dayOfWeek);
								for(DayTypeEnum e : convertDayOfWeek) {
									timetable.addDayType(e);
								}
							}
						}

						// if day type has no properties it is assumed to have explicit dates through assignments
					} else {
						if (!operatingDays.isEmpty() && operatingDays.containsKey(dayType.getId())) {
							LocalDate dateOfOperation = operatingDays.get(dayType.getId());
							timetable.addCalendarDay(new CalendarDay(java.sql.Date.valueOf(dateOfOperation), true));
						} else {
							throw new RuntimeException("Found no valid day types in properties or assignments");
						}
					}

					ValidityConditions_RelStructure validityConditionsStruct = serviceCalendarFrame.getContentValidityConditions();
					if(validityConditionsStruct == null && compositeFrame != null) {
						validityConditionsStruct = compositeFrame.getValidityConditions();
					}

					assert validityConditionsStruct != null;
					List<Object> availabilityConditionElements = validityConditionsStruct.getValidityConditionRefOrValidBetweenOrValidityCondition_();

					for(Object genericValidityCondition : availabilityConditionElements) {
						JAXBElement<?> j = (JAXBElement<?>) genericValidityCondition;
						if(j.getValue() instanceof AvailabilityCondition) {
							AvailabilityCondition availabilityCondition = ((JAXBElement<AvailabilityCondition>) j).getValue();
							Period period = new Period();
							period.setStartDate(ParserUtils.getSQLDate(availabilityCondition.getFromDate().toString()));
							period.setEndDate(ParserUtils.getSQLDate(availabilityCondition.getToDate().toString()));

							timetable.addPeriod(period);
						} else {
							throw new RuntimeException("Only support AvailabilityCondition as validityCondition");
						}
					}
				}
			} else {
				throw new RuntimeException("Only able to parse daytypes for now");
			}

			/*
			
			Parser calendarParser = ParserFactory.create(CalendarParser.class.getName());
            
            ValidityConditions_RelStructure validityConditionsStruct = serviceCalendarFrame.getContentValidityConditions();
            if(validityConditionsStruct == null && compositeFrame != null) {
            	validityConditionsStruct = compositeFrame.getValidityConditions();
            }
            if (validityConditionsStruct != null) {
                context.put(NETEX_LINE_DATA_CONTEXT, validityConditionsStruct);
                calendarParser.parse(context);
            }
            DayTypesInFrame_RelStructure dayTypeStruct = serviceCalendarFrame.getDayTypes();
            if (dayTypeStruct != null) {
                context.put(NETEX_LINE_DATA_CONTEXT, dayTypeStruct);
                calendarParser.parse(context);
            }
            OperatingDaysInFrame_RelStructure operatingDaysStruct = serviceCalendarFrame.getOperatingDays();
            if (operatingDaysStruct != null) {
                context.put(NETEX_LINE_DATA_CONTEXT, operatingDaysStruct);
                calendarParser.parse(context);
            }
            OperatingPeriodsInFrame_RelStructure operatingPeriodsStruct = serviceCalendarFrame.getOperatingPeriods();
            if (operatingPeriodsStruct != null) {
                context.put(NETEX_LINE_DATA_CONTEXT, operatingPeriodsStruct);
                calendarParser.parse(context);
            }
            */
		}
	}

	private void parseTimetableFrames(Context context, List<TimetableFrame> timetableFrames, CompositeFrame compositeFrame) throws Exception {
		for (TimetableFrame timetableFrame : timetableFrames) {
			JourneysInFrame_RelStructure vehicleJourneysStruct = timetableFrame.getVehicleJourneys();
			context.put(NETEX_LINE_DATA_CONTEXT, vehicleJourneysStruct);
			Parser journeyParser = ParserFactory.create(JourneyParser.class.getName());
			journeyParser.parse(context);
		}
	}

	protected void sortStopPoints(Referential referential) {
		// Sort stopPoints on JourneyPattern
		Collection<JourneyPattern> journeyPatterns = referential.getJourneyPatterns().values();
		for (JourneyPattern jp : journeyPatterns) {
			List<StopPoint> stopPoints = jp.getStopPoints();
			stopPoints.sort(Comparator.comparing(StopPoint::getPosition));
			jp.setDepartureStopPoint(stopPoints.get(0));
			jp.setArrivalStopPoint(stopPoints.get(stopPoints.size() - 1));
		}

		// Sort stopPoints on route
		Collection<Route> routes = referential.getRoutes().values();
		for (Route r : routes) {
			List<StopPoint> stopPoints = r.getStopPoints();
			stopPoints.sort(Comparator.comparing(StopPoint::getPosition));
		}
	}

	private void updateBoardingAlighting(Referential referential) {

		for (Route route : referential.getRoutes().values()) {
			boolean invalidData = false;
			boolean usefullData = false;

			b1: for (JourneyPattern jp : route.getJourneyPatterns()) {
				for (VehicleJourney vj : jp.getVehicleJourneys()) {
					for (VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops()) {
						if (!updateStopPoint(vjas)) {
							invalidData = true;
							break b1;
						}
					}
				}
			}
			if (!invalidData) {
				// check if every stoppoints were updated, complete missing ones to
				// normal; if all normal clean all
				for (StopPoint sp : route.getStopPoints()) {
					if (sp.getForAlighting() == null)
						sp.setForAlighting(AlightingPossibilityEnum.normal);
					if (sp.getForBoarding() == null)
						sp.setForBoarding(BoardingPossibilityEnum.normal);
				}
				for (StopPoint sp : route.getStopPoints()) {
					if (!sp.getForAlighting().equals(AlightingPossibilityEnum.normal)) {
						usefullData = true;
						break;
					}
					if (!sp.getForBoarding().equals(BoardingPossibilityEnum.normal)) {
						usefullData = true;
						break;
					}
				}

			}
			if (invalidData || !usefullData) {
				// remove useless informations
				for (StopPoint sp : route.getStopPoints()) {
					sp.setForAlighting(null);
					sp.setForBoarding(null);
				}
			}

		}
	}

	private boolean updateStopPoint(VehicleJourneyAtStop vjas) {
		StopPoint sp = vjas.getStopPoint();
		BoardingPossibilityEnum forBoarding = getForBoarding(vjas.getBoardingAlightingPossibility());
		AlightingPossibilityEnum forAlighting = getForAlighting(vjas.getBoardingAlightingPossibility());
		if (sp.getForBoarding() != null && !sp.getForBoarding().equals(forBoarding))
			return false;
		if (sp.getForAlighting() != null && !sp.getForAlighting().equals(forAlighting))
			return false;
		sp.setForBoarding(forBoarding);
		sp.setForAlighting(forAlighting);
		return true;
	}

	private AlightingPossibilityEnum getForAlighting(BoardingAlightingPossibilityEnum boardingAlightingPossibility) {
		if (boardingAlightingPossibility == null)
			return AlightingPossibilityEnum.normal;
		switch (boardingAlightingPossibility) {
		case BoardAndAlight:
			return AlightingPossibilityEnum.normal;
		case AlightOnly:
			return AlightingPossibilityEnum.normal;
		case BoardOnly:
			return AlightingPossibilityEnum.forbidden;
		case NeitherBoardOrAlight:
			return AlightingPossibilityEnum.forbidden;
		case BoardAndAlightOnRequest:
			return AlightingPossibilityEnum.request_stop;
		case AlightOnRequest:
			return AlightingPossibilityEnum.request_stop;
		case BoardOnRequest:
			return AlightingPossibilityEnum.normal;
		}
		return null;
	}

	private BoardingPossibilityEnum getForBoarding(BoardingAlightingPossibilityEnum boardingAlightingPossibility) {
		if (boardingAlightingPossibility == null)
			return BoardingPossibilityEnum.normal;
		switch (boardingAlightingPossibility) {
		case BoardAndAlight:
			return BoardingPossibilityEnum.normal;
		case AlightOnly:
			return BoardingPossibilityEnum.forbidden;
		case BoardOnly:
			return BoardingPossibilityEnum.normal;
		case NeitherBoardOrAlight:
			return BoardingPossibilityEnum.forbidden;
		case BoardAndAlightOnRequest:
			return BoardingPossibilityEnum.request_stop;
		case AlightOnRequest:
			return BoardingPossibilityEnum.normal;
		case BoardOnRequest:
			return BoardingPossibilityEnum.request_stop;
		}
		return null;
	}

	static {
		ParserFactory.register(PublicationDeliveryParser.class.getName(), new ParserFactory() {
			private PublicationDeliveryParser instance = new PublicationDeliveryParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
