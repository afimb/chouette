package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.NetexprofileImportParameters;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexNamespaceContext;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidatorFactory;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import org.rutebanken.netex.model.*;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.util.Collection;
import java.util.List;

@Log4j
public class NorwayLineNetexProfileValidator extends AbstractValidator implements NetexProfileValidator {

	public static final String LOCAL_CONTEXT = "NetexPublicationDelivery";
	public static final String NAME = "NorwayLineNetexProfileValidator";
	public static final String PREFIX = "1-NETEX-";

	private static final String FRAME_1 = "1-NETEX-Frame-1";
	private static final String FRAME_2 = "1-NETEX-Frame-2";
	private static final String FRAME_3 = "1-NETEX-Frame-3";
	private static final String FRAME_4 = "1-NETEX-Frame-4";
	private static final String FRAME_5 = "1-NETEX-Frame-5";

	@Override
	public void addObjectReference(Context context, DataManagedObjectStructure object) {}

	@Override
	protected void initializeCheckPoints(Context context) {
		addItemToValidation(context, PREFIX, "Frame", 5, "E", "E", "E", "E", "E");
	}

	@Override
	public void validate(Context context) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NetexNamespaceContext());
		context.put(NETEX_LINE_DATA_XPATH, xpath);

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		PublicationDeliveryStructure lineDeliveryStructure = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
		Document dom = (Document) context.get(NETEX_LINE_DATA_DOM);
		NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);

		//StopRegistryIdValidator stopRegisterValidator = new StopRegistryIdValidator();

		// TODO consider xpath validation
		// validateElementPresent(context, xpath, dom, "//n:ServiceFrame", "1", "No ServiceFrame", _1_NETEX_SERVICEFRAME);
		// validateMinOccursOfElement(context, xpath, dom, "count(//n:ServiceFrame/n:Network)", 0, _2_NETEX_SERVICEFRAME_NETWORK);
		// validateElementNotPresent(context, xpath, dom, "//n:SiteFrame/n:stopPlaces/n:StopPlace", "1", "Should not contain StopPlaces", _2_NETEX_SITEFRAME_STOPPLACE);
		// validateExternalReferenceCorrect(context, xpath, dom, "//n:StopPlaceRef/@ref", stopRegisterValidator, _2_NETEX_STOPPLACE_REF);

		// TODO add profile validation elements based on external reference data (dom)

		// TODO consider check if frames present through xpath validation, before actual validation of frame
		// TODO add profile validation elements based on java codex
		validateResourceFrame(context, referential);
		validateSiteFrame(context, referential);
		validateServiceFrame(context, referential);
		validateServiceCalendarFrame(context, referential);
		validateTimetableFrame(context, referential);

		return;
    }

	private void validateResourceFrame(Context context, NetexReferential referential) throws Exception {
		// TODO consider moving up one level in call hierarchy
		DataLocation dataLocation = new DataLocation((String)context.get(FILE_NAME));
		Collection<ResourceFrame> resourceFrames = referential.getResourceFrames().values();
		prepareCheckPoint(context, FRAME_1);

		if (isCollectionEmpty(resourceFrames)) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
			validationReporter.addCheckPointReportError(context, FRAME_1, "Missing mandatory frame : 'ResourceFrame'", dataLocation);
		} else {
			for (ResourceFrame resourceFrame : resourceFrames) {

				// validate data sources
				DataSourcesInFrame_RelStructure dataSourcesStruct = resourceFrame.getDataSources();
				if (dataSourcesStruct != null && !isCollectionEmpty(dataSourcesStruct.getDataSource())) {
					log.info("DataSources present");
					List<DataSource> dataSources = dataSourcesStruct.getDataSource();
					for (DataSource dataSource : dataSources) {
						// TODO validate data source instance
					}
				}

				// validate responsibility sets
				ResponsibilitySetsInFrame_RelStructure responsibilitySetsStruct = resourceFrame.getResponsibilitySets();
				if (responsibilitySetsStruct != null && !isCollectionEmpty(responsibilitySetsStruct.getResponsibilitySet())) {
					log.info("ResponsibilitySets present");
					List<ResponsibilitySet> responsibilitySets = responsibilitySetsStruct.getResponsibilitySet();
					for (ResponsibilitySet responsibilitySet : responsibilitySets) {
						// TODO validate responsibility set instance
					}
				}

				TypesOfValueInFrame_RelStructure typesOfValueStruct = resourceFrame.getTypesOfValue();
				if (typesOfValueStruct != null && !isCollectionEmpty(typesOfValueStruct.getValueSetOrTypeOfValue())) {
					log.info("TypesOfValues present");
					List<JAXBElement<? extends DataManagedObjectStructure>> typesOfValueElements = typesOfValueStruct.getValueSetOrTypeOfValue();
					for (JAXBElement<? extends DataManagedObjectStructure> typesOfValueElement : typesOfValueElements) {
						// TODO downcast and validate
					}
				}

				// validate organisations
				// TODO consider if this is a problem or not, we already have all organisations of all frames in referential right?
				// TODO this will probably cause same validation to occur multiple times, once per frame if more than one
				// TODO this is true for all validations where we already added refs to global referential
				// TODO in other words true for routes, lines, organisations, stoppoints etc...
				// TODO same goes for parsing
				Collection<Organisation> organisations = referential.getOrganisations().values();
				if (!isCollectionEmpty(organisations)) {
					log.info("Organisations present");
					// TODO consider adding the data to be validated in constant VALIDATION_DATA
					OrganisationValidator organisationValidator = (OrganisationValidator) ValidatorFactory.create(OrganisationValidator.class.getName(), context);
					organisationValidator.validate(context, null);
				}

				// validate groups of operators
				GroupsOfOperatorsInFrame_RelStructure groupsOfOperatorsStruct = resourceFrame.getGroupsOfOperators();
				if (groupsOfOperatorsStruct != null && !isCollectionEmpty(groupsOfOperatorsStruct.getGroupOfOperators())) {
					log.info("GroupsOfOperators present");
					List<GroupOfOperators> groupOfOperatorsList = groupsOfOperatorsStruct.getGroupOfOperators();
					for (GroupOfOperators groupOfOperators : groupOfOperatorsList) {
						// TODO validate group of operators instance
					}
				}

				// validate equipments
				EquipmentsInFrame_RelStructure equipmentsStruct = resourceFrame.getEquipments();
				if (equipmentsStruct != null && !isCollectionEmpty(equipmentsStruct.getEquipment())) {
					log.info("Equipments present");
					List<JAXBElement<? extends Equipment_VersionStructure>> equipmentElements = equipmentsStruct.getEquipment();
					for (JAXBElement<? extends Equipment_VersionStructure> equipmentElement : equipmentElements) {
						Equipment_VersionStructure equipment = equipmentElement.getValue();
						// TODO downcast and validate
					}
				}

				// validate vehichle types
				VehicleTypesInFrame_RelStructure vehicleTypesStruct = resourceFrame.getVehicleTypes();
				if (vehicleTypesStruct != null && !isCollectionEmpty(vehicleTypesStruct.getCompoundTrainOrTrainOrVehicleType())) {
					log.info("VehicleTypes present");
					List<VehicleType_VersionStructure> vehicleTypeStructList = vehicleTypesStruct.getCompoundTrainOrTrainOrVehicleType();
					for (VehicleType_VersionStructure vehicleTypeStruct : vehicleTypeStructList) {
						// TODO validate
					}
				}

				// validate vehicles
				VehiclesInFrame_RelStructure vehiclesStruct = resourceFrame.getVehicles();
				if (vehiclesStruct != null && !isCollectionEmpty(vehiclesStruct.getTrainElementOrVehicle())) {
					log.info("Vehicles present");
					List<DataManagedObjectStructure> vehicles = vehiclesStruct.getTrainElementOrVehicle();
					for (DataManagedObjectStructure vehicle : vehicles) {
						// TODO downcast and validate
					}
				}

				// validate schematic maps
				SchematicMapsInFrame_RelStructure schematicMapsStruct = resourceFrame.getSchematicMaps();
				if (schematicMapsStruct != null && !isCollectionEmpty(schematicMapsStruct.getSchematicMap())) {
					log.info("SchematicMaps present");
					List<SchematicMap> schematicMapList = schematicMapsStruct.getSchematicMap();
					for (SchematicMap schematicMap : schematicMapList) {
						// TODO validate
					}
				}

				// validate groups of entities
				GroupOfEntitiesInFrame_RelStructure groupsOfEntities = resourceFrame.getGroupsOfEntities();
				if (groupsOfEntities != null && !isCollectionEmpty(groupsOfEntities.getGeneralGroupOfEntities())) {
					log.info("GroupOfEntities present");
					List<GeneralGroupOfEntities> generalGroupOfEntities = groupsOfEntities.getGeneralGroupOfEntities();
					for (GeneralGroupOfEntities generalGroupOfEntity : generalGroupOfEntities) {
						// TODO downcast and validate
					}
				}
			}
		}
	}

	private void validateSiteFrame(Context context, NetexReferential referential)  throws Exception {
		// TODO consider moving up one level in call hierarchy
		DataLocation dataLocation = new DataLocation((String)context.get(FILE_NAME));
		Collection<SiteFrame> siteFrames = referential.getSiteFrames().values();
		prepareCheckPoint(context, FRAME_2);

		if (isCollectionEmpty(siteFrames)) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
			validationReporter.addCheckPointReportError(context, FRAME_2, "Missing mandatory frame : 'SiteFrame'", dataLocation);
		} else {
			for (SiteFrame siteFrame : siteFrames) {
				// TODO add validation of site frame elements

				// validate topographic places
				TopographicPlacesInFrame_RelStructure topographicPlacesStruct = siteFrame.getTopographicPlaces();
				if (topographicPlacesStruct != null && !isCollectionEmpty(topographicPlacesStruct.getTopographicPlace())) {
					log.info("TopographicPlace present");
					List<TopographicPlace> topographicPlaces = topographicPlacesStruct.getTopographicPlace();
					for (TopographicPlace topographicPlace : topographicPlaces) {
						// TODO validate
					}
				}

				// validate addresses
				AddressesInFrame_RelStructure addressesStruct = siteFrame.getAddresses();
				if (addressesStruct != null && !isCollectionEmpty(addressesStruct.getAddress())) {
					log.info("Addresses present");
					List<JAXBElement<? extends Address_VersionStructure>> addressElements = addressesStruct.getAddress();
					for (JAXBElement<? extends Address_VersionStructure> addressElement : addressElements) {
						// TODO downcast and validate
						//Address_VersionStructure value = addressElement.getValue();
					}
				}

				// validate accesses
				AccessesInFrame_RelStructure accessesStruct = siteFrame.getAccesses();
				if (accessesStruct != null && !isCollectionEmpty(accessesStruct.getAccess())) {
					log.info("Accesses present");
					List<Access> accesses = accessesStruct.getAccess();
					for (Access access : accesses) {
						// TODO validate access instance
					}
				}

				// validate stop places
				StopPlacesInFrame_RelStructure stopPlacesStruct = siteFrame.getStopPlaces();
				if (stopPlacesStruct != null && !isCollectionEmpty(stopPlacesStruct.getStopPlace())) {
					log.info("StopPlaces present");
					List<StopPlace> stopPlaces = stopPlacesStruct.getStopPlace();
					for (StopPlace stopPlace : stopPlaces) {
						// TODO validate stop place
					}
				}

				// validate flexible stop places
				FlexibleStopPlacesInFrame_RelStructure flexibleStopPlacesStruct = siteFrame.getFlexibleStopPlaces();
				if (flexibleStopPlacesStruct != null && !isCollectionEmpty(flexibleStopPlacesStruct.getFlexibleStopPlace())) {
					log.info("FlexibleStopPlaces present");
					List<FlexibleStopPlace> flexibleStopPlaces = flexibleStopPlacesStruct.getFlexibleStopPlace();
					for (FlexibleStopPlace flexibleStopPlace : flexibleStopPlaces) {
						// TODO validate
					}
				}

				// validate points of interest
				PointsOfInterestInFrame_RelStructure pointsOfInterestStruct = siteFrame.getPointsOfInterest();
				if (pointsOfInterestStruct != null && !isCollectionEmpty(pointsOfInterestStruct.getPointOfInterest())) {
					log.info("PointsOfInterest present");
					List<PointOfInterest> pointOfInterestList = pointsOfInterestStruct.getPointOfInterest();
					for (PointOfInterest pointOfInterest : pointOfInterestList) {
						// TODO validate
					}
				}

				// validate parkings
				ParkingsInFrame_RelStructure parkingsStruct = siteFrame.getParkings();
				if (parkingsStruct != null && !isCollectionEmpty(parkingsStruct.getParking())) {
					log.info("Parkings present");
					List<Parking> parkings = parkingsStruct.getParking();
					for (Parking parking : parkings) {
						// TODO validate
					}
				}

				// validate navigation paths
				NavigationPathsInFrame_RelStructure navigationPathsStruct = siteFrame.getNavigationPaths();
				if (navigationPathsStruct != null && !isCollectionEmpty(navigationPathsStruct.getNavigationPath())) {
					log.info("NavigationPaths present");
					List<NavigationPath> navigationPaths = navigationPathsStruct.getNavigationPath();
					for (NavigationPath navigationPath : navigationPaths) {
						// TODO validate
					}
				}

				// validate site facility sets
				SiteFacilitySetsInFrame_RelStructure siteFacilitySetsStruct = siteFrame.getSiteFacilitySets();
				if (siteFacilitySetsStruct != null && !isCollectionEmpty(siteFacilitySetsStruct.getSiteFacilitySet())) {
					log.info("SiteFacilitySets present");
					List<SiteFacilitySet> siteFacilitySets = siteFacilitySetsStruct.getSiteFacilitySet();
					for (SiteFacilitySet siteFacilitySet : siteFacilitySets) {
						// TODO validate
					}
				}
			}
		}
	}

	private void validateServiceFrame(Context context, NetexReferential referential) throws Exception {
		// TODO consider moving up one level in call hierarchy
		DataLocation dataLocation = new DataLocation((String)context.get(FILE_NAME));
		Collection<ServiceFrame> serviceFrames = referential.getServiceFrames().values();
		prepareCheckPoint(context, FRAME_3);

		if (isCollectionEmpty(serviceFrames)) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
			validationReporter.addCheckPointReportError(context, FRAME_3, "Missing mandatory frame : 'ServiceFrame'", dataLocation);
		} else {
			for (ServiceFrame serviceFrame : serviceFrames) {
				// TODO validate frame elements

				// validate network
				Network network = serviceFrame.getNetwork();
				if (network != null) {
					log.info("Network present");
					// TODO consider validating this Network instance, or instance registered in referential
					NetworkValidator networkValidator = (NetworkValidator) ValidatorFactory.create(NetworkValidator.class.getName(), context);
					networkValidator.validate(context, network);
				}

				// validate route points
				Collection<RoutePoint> routePoints = referential.getRoutePoints().values();
				if (!isCollectionEmpty(routePoints)) {
					log.info("Route points present");
					RoutePointValidator routePointValidator = (RoutePointValidator) ValidatorFactory.create(RoutePointValidator.class.getName(), context);
					routePointValidator.validate(context, null);
				}

				// validate routes
				Collection<Route> routes = referential.getRoutes().values();
				if (!isCollectionEmpty(routes)) {
					log.info("Routes present");
					RouteValidator routeValidator = (RouteValidator) ValidatorFactory.create(RouteValidator.class.getName(), context);
					routeValidator.validate(context, null);
				}

				// validate flexiblePointProperties
				FlexiblePointProperties_RelStructure flexiblePointPropertiesStruct = serviceFrame.getFlexiblePointProperties();
				if (flexiblePointPropertiesStruct != null && !isCollectionEmpty(flexiblePointPropertiesStruct.getFlexiblePointProperties())) {
					log.info("FlexiblePointProperties present");
					List<FlexiblePointProperties> flexiblePointProperties = flexiblePointPropertiesStruct.getFlexiblePointProperties();
					for (FlexiblePointProperties flexiblePointProperty : flexiblePointProperties) {
						// TODO validate
					}
				}

				// validate flexibleLinkProperties
				FlexibleLinkProperties_RelStructure flexibleLinkPropertiesStruct = serviceFrame.getFlexibleLinkProperties();
				if (flexibleLinkPropertiesStruct != null && !isCollectionEmpty(flexibleLinkPropertiesStruct.getFlexibleLinkProperties())) {
					log.info("FlexibleLinkProperties present");
					List<FlexibleLinkProperties> flexibleLinkProperties = flexibleLinkPropertiesStruct.getFlexibleLinkProperties();
					for (FlexibleLinkProperties flexibleLinkProperty : flexibleLinkProperties) {
						// TODO validate
					}
				}

				// validate commonSections
				CommonSectionsInFrame_RelStructure commonSectionsStruct = serviceFrame.getCommonSections();
				if (commonSectionsStruct != null && !isCollectionEmpty(commonSectionsStruct.getCommonSection())) {
					log.info("CommonSections present");
					List<CommonSection> commonSections = commonSectionsStruct.getCommonSection();
					for (CommonSection commonSection : commonSections) {
						// TODO validate
					}
				}

				// validate lines
				Collection<Line> lines = referential.getLines().values();
				if (!isCollectionEmpty(lines)) {
					log.info("Lines present");
					LineValidator lineValidator = (LineValidator) ValidatorFactory.create(LineValidator.class.getName(), context);
					lineValidator.validate(context, null);
				}

				// validate groups of lines
				GroupsOfLinesInFrame_RelStructure groupsOfLinesStruct = serviceFrame.getGroupsOfLines();
				if (groupsOfLinesStruct != null && !isCollectionEmpty(groupsOfLinesStruct.getGroupOfLines())) {
					log.info("GroupsOfLines present");
					List<GroupOfLines> groupsOfLines = groupsOfLinesStruct.getGroupOfLines();
					for (GroupOfLines groupOfLines : groupsOfLines) {
						// TODO validate
					}
				}

				// validate destination displays
				DestinationDisplaysInFrame_RelStructure destinationDisplaysStruct = serviceFrame.getDestinationDisplays();
				if (destinationDisplaysStruct != null && !isCollectionEmpty(destinationDisplaysStruct.getDestinationDisplay())) {
					log.info("DestinationDisplays present");
					List<DestinationDisplay> destinationDisplays = destinationDisplaysStruct.getDestinationDisplay();
					for (DestinationDisplay destinationDisplay : destinationDisplays) {
						// TODO validate
					}
				}

				// validate scheduled stop points
				// TODO implement separate validator
				Collection<ScheduledStopPoint> scheduledStopPoints = referential.getScheduledStopPoints().values();
				if (!isCollectionEmpty(scheduledStopPoints)) {
					log.info("ScheduledStopPoints present");
					for (ScheduledStopPoint scheduledStopPoint : scheduledStopPoints) {
						// TODO validate
					}
				}

				// validate service patterns
				ServicePatternsInFrame_RelStructure servicePatternsStruct = serviceFrame.getServicePatterns();
				if (servicePatternsStruct != null && !isCollectionEmpty(servicePatternsStruct.getServicePatternOrJourneyPatternView())) {
					log.info("ServicePatterns present");
					List<Object> servicePatterns = servicePatternsStruct.getServicePatternOrJourneyPatternView();
					for (Object servicePattern : servicePatterns) {
						// TODO downcast and validate
					}
				}

				// validate tariff zones
				TariffZonesInFrame_RelStructure tariffZonesStruct = serviceFrame.getTariffZones();
				if (tariffZonesStruct != null && !isCollectionEmpty(tariffZonesStruct.getTariffZone())) {
					log.info("TariffZones present");
					List<TariffZone> tariffZones = tariffZonesStruct.getTariffZone();
					for (TariffZone tariffZone : tariffZones) {
						// TODO validate
					}
				}

				// validate stop assignments
				// TODO make type more generic in referential (StopAssignment)
				// TODO implement separate validator
				Collection<PassengerStopAssignment> stopAssignments = referential.getPassengerStopAssignments().values();
				if (!isCollectionEmpty(stopAssignments)) {
					log.info("PassengerStopAssignments present");
					for (PassengerStopAssignment stopAssignment : stopAssignments) {
						// TODO up-/downcast and validate
					}
				}

				// validate timing points
				TimingPointsInFrame_RelStructure timingPointsStruct = serviceFrame.getTimingPoints();
				if (timingPointsStruct != null && !isCollectionEmpty(timingPointsStruct.getTimingPoint())) {
					log.info("TimingPoints present");
					List<TimingPoint> timingPoints = timingPointsStruct.getTimingPoint();
					for (TimingPoint timingPoint : timingPoints) {
						// TODO validate
					}
				}

				// validate timing links
				TimingLinksInFrame_RelStructure timingLinksStruct = serviceFrame.getTimingLinks();
				if (timingLinksStruct != null && !isCollectionEmpty(timingLinksStruct.getTimingLink())) {
					log.info("TimingLinks present");
					List<TimingLink> timingLinks = timingLinksStruct.getTimingLink();
					for (TimingLink timingLink : timingLinks) {
						// TODO validate
					}
				}

				// validate journey patterns
				// TODO implement separate validator for journey patterns
				Collection<JourneyPattern> journeyPatterns = referential.getJourneyPatterns().values();
				if (!isCollectionEmpty(journeyPatterns)) {
					log.info("JourneyPatterns present");
					for (JourneyPattern journeyPattern : journeyPatterns) {
						// TODO validate
					}
				}

				// validate service exclusions
				ServiceExclusionsInFrame_RelStructure serviceExclusionsStruct = serviceFrame.getServiceExclusions();
				if (serviceExclusionsStruct != null && !isCollectionEmpty(serviceExclusionsStruct.getServiceExclusion())) {
					log.info("ServiceExclusions present");
					List<ServiceExclusion> serviceExclusions = serviceExclusionsStruct.getServiceExclusion();
					for (ServiceExclusion serviceExclusion : serviceExclusions) {
						// TODO validate
					}
				}

				// validate notices
				NoticesInFrame_RelStructure noticesStruct = serviceFrame.getNotices();
				if (noticesStruct != null && !isCollectionEmpty(noticesStruct.getNotice())) {
					log.info("Notices present");
					List<Notice> notices = noticesStruct.getNotice();
					for (Notice notice : notices) {
						// TODO validate
					}
				}

				// validate notice assignments
				NoticeAssignmentsInFrame_RelStructure noticeAssignmentsStruct = serviceFrame.getNoticeAssignments();
				if (noticeAssignmentsStruct != null && !isCollectionEmpty(noticeAssignmentsStruct.getNoticeAssignment_())) {
					log.info("NoticeAssignments present");
					List<JAXBElement<? extends DataManagedObjectStructure>> noticeAssignmentElements = noticeAssignmentsStruct.getNoticeAssignment_();
					for (JAXBElement<? extends DataManagedObjectStructure> noticeAssignmentElement : noticeAssignmentElements) {
						// TODO downcast and validate
					}
				}
			}
		}
	}

	private void validateServiceCalendarFrame(Context context, NetexReferential referential) {
		// TODO consider moving up one level in call hierarchy
		DataLocation dataLocation = new DataLocation((String)context.get(FILE_NAME));
		Collection<ServiceCalendarFrame> serviceCalendarFrames = referential.getServiceCalendarFrames().values();
		prepareCheckPoint(context, FRAME_4);

		if (isCollectionEmpty(serviceCalendarFrames)) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
			validationReporter.addCheckPointReportError(context, FRAME_4, "Missing mandatory frame : 'ServiceCalendarFrame'", dataLocation);
		} else {
			for (ServiceCalendarFrame serviceCalendarFrame : serviceCalendarFrames) {

				// validate service calendar
				ServiceCalendar serviceCalendar = serviceCalendarFrame.getServiceCalendar();
				if (serviceCalendar != null) {
					log.info("Service calendar present");
					// TODO validate
				}

				// validate day types
				// TODO implement separate validator
				Collection<DayType> dayTypes = referential.getDayTypes().values();
				if (!isCollectionEmpty(dayTypes)) {
					log.info("DayTypes present");
					for (DayType dayType : dayTypes) {
						// TODO validate
					}
				}

				// validate timebands
				TimebandsInFrame_RelStructure timebandsStruct = serviceCalendarFrame.getTimebands();
				if (timebandsStruct != null && !isCollectionEmpty(timebandsStruct.getTimeband())) {
					log.info("Timebands present");
					List<Timeband> timebands = timebandsStruct.getTimeband();
					for (Timeband timeband : timebands) {
						// TODO validate
					}
				}

				// validate operating days
				OperatingDaysInFrame_RelStructure operatingDaysStruct = serviceCalendarFrame.getOperatingDays();
				if (operatingDaysStruct != null && !isCollectionEmpty(operatingDaysStruct.getOperatingDay())) {
					log.info("OperatingDays present");
					List<OperatingDay> operatingDays = operatingDaysStruct.getOperatingDay();
					for (OperatingDay operatingDay : operatingDays) {
						// TODO validate
					}
				}

				// validate operating periods
				OperatingPeriodsInFrame_RelStructure operatingPeriodsStruct = serviceCalendarFrame.getOperatingPeriods();
				if (operatingPeriodsStruct != null && !isCollectionEmpty(operatingPeriodsStruct.getOperatingPeriodOrUicOperatingPeriod())) {
					log.info("OperatingPeriods present");
					List<OperatingPeriod_VersionStructure> operatingPeriods = operatingPeriodsStruct.getOperatingPeriodOrUicOperatingPeriod();
					for (OperatingPeriod_VersionStructure operatingPeriod : operatingPeriods) {
						// TODO downcast and validate
					}
				}

				// validate day type assignments
				DayTypeAssignmentsInFrame_RelStructure dayTypeAssignmentsStruct = serviceCalendarFrame.getDayTypeAssignments();
				if (dayTypeAssignmentsStruct != null && !isCollectionEmpty(dayTypeAssignmentsStruct.getDayTypeAssignment())) {
					log.info("DayTypeAssignments present");
					List<DayTypeAssignment> dayTypeAssignments = dayTypeAssignmentsStruct.getDayTypeAssignment();
					for (DayTypeAssignment dayTypeAssignment : dayTypeAssignments) {
						// TODO validate
					}
				}
			}
		}
	}

	private void validateTimetableFrame(Context context, NetexReferential referential) {
		// TODO consider moving up one level in call hierarchy
		DataLocation dataLocation = new DataLocation((String)context.get(FILE_NAME));
		Collection<TimetableFrame> timetableFrames = referential.getTimetableFrames().values();
		prepareCheckPoint(context, FRAME_5);

		if (isCollectionEmpty(timetableFrames)) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
			validationReporter.addCheckPointReportError(context, FRAME_5, "Missing mandatory frame : 'TimetableFrame'", dataLocation);
		} else {
			for (TimetableFrame timetableFrame : timetableFrames) {

				// validate bookingtimes/validityconditions, which one?
				ContainedAvailabilityConditions_RelStructure bookingTimesStruct = timetableFrame.getBookingTimes();
				if (bookingTimesStruct != null && !isCollectionEmpty(bookingTimesStruct.getAvailabilityCondition())) {
					log.info("ContainedAvailabilityConditions present");
					List<AvailabilityCondition> availabilityConditions = bookingTimesStruct.getAvailabilityCondition();
					for (AvailabilityCondition availabilityCondition : availabilityConditions) {
						// TODO validate
					}
				}
				// validate bookingtimes/validityconditions, which one?
				ValidityConditions_RelStructure validityConditionsStruct = timetableFrame.getValidityConditions();
				if (validityConditionsStruct != null && !isCollectionEmpty(validityConditionsStruct.getValidityConditionRefOrValidBetweenOrValidityCondition_())) {
					log.info("ValidityConditions present");
					List<Object> validityConditions = validityConditionsStruct.getValidityConditionRefOrValidBetweenOrValidityCondition_();
					for (Object validityCondition : validityConditions) {
						// TODO downcast and validate
					}
				}

				// validate vehicle journeys
				// TODO implement separate validator
				Collection<ServiceJourney> serviceJourneys = referential.getServiceJourneys().values();
				if (!isCollectionEmpty(serviceJourneys)) {
					log.info("ServiceJourneys present");
					for (ServiceJourney serviceJourney : serviceJourneys) {
						// TODO validate
					}
				}

				// validate frequency groups
				FrequencyGroupsInFrame_RelStructure frequencyGroupsStruct = timetableFrame.getFrequencyGroups();
				if (frequencyGroupsStruct != null && !isCollectionEmpty(frequencyGroupsStruct.getHeadwayJourneyGroupOrRhythmicalJourneyGroup())) {
					log.info("FrequencyGroups present");
					List<JourneyFrequencyGroup_VersionStructure> frequencyGroups = frequencyGroupsStruct.getHeadwayJourneyGroupOrRhythmicalJourneyGroup();
					for (JourneyFrequencyGroup_VersionStructure frequencyGroup : frequencyGroups) {
						// TODO validate
					}
				}

				// validate groups of services
				GroupsOfServicesInFrame_RelStructure groupsOfServicesStruct = timetableFrame.getGroupsOfServices();
				if (groupsOfServicesStruct != null && !isCollectionEmpty(groupsOfServicesStruct.getGroupOfServices())) {
					log.info("GroupsOfServices present");
					List<GroupOfServices> groupsOfServices = groupsOfServicesStruct.getGroupOfServices();
					for (GroupOfServices groupOfServices : groupsOfServices) {
						// TODO validate
					}
				}

				// validate journey part couples
				JourneyPartCouplesInFrame_RelStructure journeyPartCouplesStruct = timetableFrame.getJourneyPartCouples();
				if (journeyPartCouplesStruct != null && !isCollectionEmpty(journeyPartCouplesStruct.getJourneyPartCouple())) {
					log.info("JourneyPartCouples present");
					List<JourneyPartCouple> journeyPartCouples = journeyPartCouplesStruct.getJourneyPartCouple();
					for (JourneyPartCouple journeyPartCouple : journeyPartCouples) {
						// TODO validate
					}
				}

				// validate coupled journeys
				CoupledJourneysInFrame_RelStructure coupledJourneysStruct = timetableFrame.getCoupledJourneys();
				if (coupledJourneysStruct != null && !isCollectionEmpty(coupledJourneysStruct.getCoupledJourney())) {
					log.info("CoupledJourneys present");
					List<CoupledJourney> coupledJourneys = coupledJourneysStruct.getCoupledJourney();
					for (CoupledJourney coupledJourney : coupledJourneys) {
						// TODO validate
					}
				}

				// validate service facility sets
				ServiceFacilitySetsInFrame_RelStructure serviceFacilitySetsStruct = timetableFrame.getServiceFacilitySets();
				if (serviceFacilitySetsStruct != null && !isCollectionEmpty(serviceFacilitySetsStruct.getServiceFacilitySet())) {
					log.info("ServiceFacilitySets present");
					List<ServiceFacilitySet> serviceFacilitySets = serviceFacilitySetsStruct.getServiceFacilitySet();
					for (ServiceFacilitySet serviceFacilitySet : serviceFacilitySets) {
						// TODO validate
					}
				}

				// validate flexible service properties
				FlexibleServicePropertiesInFrame_RelStructure flexibleServicePropertiesStruct = timetableFrame.getFlexibleServiceProperties();
				if (flexibleServicePropertiesStruct != null && !isCollectionEmpty(flexibleServicePropertiesStruct.getFlexibleServiceProperties())) {
					log.info("FlexibleServiceProperties present");
					List<FlexibleServiceProperties> flexibleServicePropertiesList = flexibleServicePropertiesStruct.getFlexibleServiceProperties();
					for (FlexibleServiceProperties flexibleServiceProperties : flexibleServicePropertiesList) {
						// TODO validate
					}
				}

				// validate journey meetings
				JourneyMeetingsInFrame_RelStructure journeyMeetingsStruct = timetableFrame.getJourneyMeetings();
				if (journeyMeetingsStruct != null && !isCollectionEmpty(journeyMeetingsStruct.getJourneyMeeting())) {
					log.info("JourneyMeetings present");
					List<JourneyMeeting> journeyMeetings = journeyMeetingsStruct.getJourneyMeeting();
					for (JourneyMeeting journeyMeeting : journeyMeetings) {
						// TODO validate
					}
				}

				// validate journey interchanges
				JourneyInterchangesInFrame_RelStructure journeyInterchangesStruct = timetableFrame.getJourneyInterchanges();
				if (journeyInterchangesStruct != null && !isCollectionEmpty(journeyInterchangesStruct.getServiceJourneyPatternInterchangeOrServiceJourneyInterchange())) {
					log.info("JourneyInterchanges present");
					List<Interchange_VersionStructure> journeyInterchanges = journeyInterchangesStruct.getServiceJourneyPatternInterchangeOrServiceJourneyInterchange();
					for (Interchange_VersionStructure journeyInterchange : journeyInterchanges) {
						// TODO downcast and validate
					}
				}
			}
		}
	}

	public static class DefaultValidatorFactory extends NetexProfileValidatorFactory {
		@Override
		protected NetexProfileValidator create(Context context) {
			NorwayLineNetexProfileValidator instance = (NorwayLineNetexProfileValidator) context.get(NAME);
			if (instance == null) {
				instance = new NorwayLineNetexProfileValidator();
				context.put(NAME, instance);
			}
			return instance;
		}
	}

	static {
		NetexProfileValidatorFactory.factories.put(NorwayLineNetexProfileValidator.class.getName(),
				new NorwayLineNetexProfileValidator.DefaultValidatorFactory());
	}

}
