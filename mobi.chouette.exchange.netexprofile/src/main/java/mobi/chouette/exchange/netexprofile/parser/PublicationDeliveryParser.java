package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.RoutePointValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.*;

@Log4j
public class PublicationDeliveryParser implements NetexParser {

    @Override
    public void initReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        if (referential == null) {
            referential = new NetexReferential();
            context.put(NETEX_REFERENTIAL, referential);
        } else {
            referential.clear();
        }

        PublicationDeliveryStructure publicationDelivery = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
        List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame = publicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame();

        initResourceFrameRefs(context, referential, topLevelFrame);
        initSiteFrameRefs(referential, topLevelFrame);
        initServiceCalendarFrameRefs(referential, topLevelFrame);
        initServiceFrameRefs(context, referential, topLevelFrame);
        initTimetableFrameRefs(context, referential, topLevelFrame);
    }

    private void initResourceFrameRefs(Context context, NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) throws Exception {
        List<ResourceFrame> resourceFrames = getFrames(ResourceFrame.class, topLevelFrame);
        for (ResourceFrame resourceFrame : resourceFrames) {
            NetexObjectUtil.addResourceFrameReference(referential, resourceFrame.getId(), resourceFrame);

            // 1. initialize organisations
            OrganisationsInFrame_RelStructure organisationsInFrameStruct = resourceFrame.getOrganisations();
            context.put(NETEX_LINE_DATA_CONTEXT, organisationsInFrameStruct);
            OrganisationParser organisationParser = (OrganisationParser) ParserFactory.create(OrganisationParser.class.getName());
            organisationParser.initReferentials(context);
        }
    }

    private void initSiteFrameRefs(NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) {
        List<SiteFrame> siteFrames = getFrames(SiteFrame.class, topLevelFrame);
        for (SiteFrame siteFrame : siteFrames) {
            NetexObjectUtil.addSiteFrameReference(referential, siteFrame.getId(), siteFrame);

            StopPlacesInFrame_RelStructure stopPlacesStruct = siteFrame.getStopPlaces();
            List<StopPlace> stopPlaces = stopPlacesStruct.getStopPlace();
            for (StopPlace stopPlace : stopPlaces) {
                // TODO implement
            }
        }
    }

    private void initServiceCalendarFrameRefs(NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) {
        List<ServiceCalendarFrame> serviceCalendarFrames = getFrames(ServiceCalendarFrame.class, topLevelFrame);
        for (ServiceCalendarFrame serviceCalendarFrame : serviceCalendarFrames) {
            NetexObjectUtil.addServiceCalendarFrameReference(referential, serviceCalendarFrame.getId(), serviceCalendarFrame);

            DayTypesInFrame_RelStructure dayTypeStruct = serviceCalendarFrame.getDayTypes();
            List<JAXBElement<? extends DataManagedObjectStructure>> dayTypeStructElements = dayTypeStruct.getDayType_();
            for (JAXBElement<? extends DataManagedObjectStructure> dayTypeStructElement : dayTypeStructElements) {
                DayType dayType = (DayType) dayTypeStructElement.getValue();
                // TODO consider generating a more sophisticated id
                NetexObjectUtil.addDayTypeReference(referential, dayType.getId(), dayType);
            }
        }
    }

    private void initServiceFrameRefs(Context context, NetexReferential referential,
                                    List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) throws Exception {
        List<ServiceFrame> serviceFrames = getFrames(ServiceFrame.class, topLevelFrame);
        for (ServiceFrame serviceFrame : serviceFrames) {
            NetexObjectUtil.addServiceFrameReference(referential, serviceFrame.getId(), serviceFrame);

            // 1. parse networks
            Network network = serviceFrame.getNetwork();
            context.put(NETEX_LINE_DATA_CONTEXT, network);
            NetworkParser networkParser = (NetworkParser) ParserFactory.create(NetworkParser.class.getName());
            networkParser.initReferentials(context);

            // 2. parse route points
            RoutePointValidator routePointValidator = (RoutePointValidator) ValidatorFactory.create(RoutePointValidator.class.getName(), context);
            RoutePointsInFrame_RelStructure routePointsStructure = serviceFrame.getRoutePoints();
            List<RoutePoint> routePoints = routePointsStructure.getRoutePoint();
            for (RoutePoint routePoint : routePoints) {
                String objectId = routePoint.getId();

                // 1. initialize stop point references
                List<String> stopPointIds = NetexObjectUtil.getStopPointRefsOfRoutePoint(routePoint);
                for (String stopPointId : stopPointIds) {
                    routePointValidator.addStopPointReference(context, objectId, stopPointId);
                }
                NetexObjectUtil.addRoutePointReference(referential, routePoint.getId(), routePoint);
                routePointValidator.addObjectReference(context, routePoint);
            }

            // 3. parse routes
            RoutesInFrame_RelStructure routesInFrameStruct = serviceFrame.getRoutes();
            context.put(NETEX_LINE_DATA_CONTEXT, routesInFrameStruct);
            RouteParser routeParser = (RouteParser) ParserFactory.create(RouteParser.class.getName());
            routeParser.initReferentials(context);

            // 4. parse lines
            LinesInFrame_RelStructure linesInFrameStruct = serviceFrame.getLines();
            context.put(NETEX_LINE_DATA_CONTEXT, linesInFrameStruct);
            LineParser lineParser = (LineParser) ParserFactory.create(LineParser.class.getName());
            lineParser.initReferentials(context);

            // 5. parse stop assignments (connection between stop points and stop places)
            StopAssignmentsInFrame_RelStructure stopAssignmentsStructure = serviceFrame.getStopAssignments();
            List<JAXBElement<? extends StopAssignment_VersionStructure>> stopAssignmentElements = stopAssignmentsStructure.getStopAssignment();
            for (JAXBElement<? extends StopAssignment_VersionStructure> stopAssignmentElement : stopAssignmentElements) {
                PassengerStopAssignment passengerStopAssignment = (PassengerStopAssignment) stopAssignmentElement.getValue();
                // TODO consider generating a more sophisticated id
                NetexObjectUtil.addPassengerStopAssignmentReference(referential, passengerStopAssignment.getId(), passengerStopAssignment);
            }

            // 6. parse scheduled stop points
            ScheduledStopPointsInFrame_RelStructure scheduledStopPointStruct = serviceFrame.getScheduledStopPoints();
            context.put(NETEX_LINE_DATA_CONTEXT, scheduledStopPointStruct);
            ScheduledStopPointParser scheduledStopPointParser = (ScheduledStopPointParser) ParserFactory.create(ScheduledStopPointParser.class.getName());
            scheduledStopPointParser.initReferentials(context);

            // 7. parse journey patterns
            JourneyPatternsInFrame_RelStructure journeyPatternStruct = serviceFrame.getJourneyPatterns();
            List<JAXBElement<?>> journeyPatternElements = journeyPatternStruct.getJourneyPattern_OrJourneyPatternView();
            for (JAXBElement<?> journeyPatternElement : journeyPatternElements) {
                JourneyPattern journeyPattern = (JourneyPattern) journeyPatternElement.getValue();
                // TODO consider generating a more sophisticated id
                NetexObjectUtil.addJourneyPatternReference(referential, journeyPattern.getId(), journeyPattern);
            }
        }
    }

    private void initTimetableFrameRefs(Context context, NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) {
        List<TimetableFrame> timetableFrames = getFrames(TimetableFrame.class, topLevelFrame);
        for (TimetableFrame timetableFrame : timetableFrames) {
            NetexObjectUtil.addTimetableFrameReference(referential, timetableFrame.getId(), timetableFrame);

            JourneysInFrame_RelStructure vehicleJourneysStruct = timetableFrame.getVehicleJourneys();
            List<Journey_VersionStructure> serviceJourneyStructs = vehicleJourneysStruct.getDatedServiceJourneyOrDeadRunOrServiceJourney();
            for (Journey_VersionStructure serviceJourneyStruct : serviceJourneyStructs) {
                ServiceJourney serviceJourney = (ServiceJourney) serviceJourneyStruct;
                // TODO consider generating a more sophisticated id
                NetexObjectUtil.addServiceJourneyReference(referential, serviceJourney.getId(), serviceJourney);
            }
        }
    }

    private <T> List<T> getFrames(Class<T> clazz, List<JAXBElement<? extends Common_VersionFrameStructure>> compositeFrameOrCommonFrame) {
        List<T> foundFrames = new ArrayList<>();
        for (JAXBElement<? extends Common_VersionFrameStructure> frame : compositeFrameOrCommonFrame) {
            if (frame.getValue() instanceof CompositeFrame) {
                CompositeFrame compositeFrame = (CompositeFrame) frame.getValue();
                Frames_RelStructure frames = compositeFrame.getFrames();
                List<JAXBElement<? extends Common_VersionFrameStructure>> commonFrames = frames.getCommonFrame();
                for (JAXBElement<? extends Common_VersionFrameStructure> commonFrame : commonFrames) {
                    T value = (T) commonFrame.getValue();
                    if (value.getClass().equals(clazz)) {
                        foundFrames.add(value);
                    }
                }
            } else if (frame.getValue().equals(clazz)) {
                foundFrames.add((T) frame.getValue());
            }
        }
        return foundFrames;
    }

    @Override
    public void parse(Context context) throws Exception {
        @SuppressWarnings("unchecked") List<PublicationDeliveryStructure> commonData =
                (List<PublicationDeliveryStructure>) context.get(NETEX_COMMON_DATA);

        PublicationDeliveryStructure lineData = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
        PublicationDeliveryStructure.DataObjects dataObjects = lineData.getDataObjects();
        List<JAXBElement<? extends Common_VersionFrameStructure>> compositeFrameOrCommonFrame = dataObjects.getCompositeFrameOrCommonFrame();

        Referential referential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        Map<String, Object> cachedNetexData = new HashMap<>();
        context.put(NETEX_LINE_DATA_ID_CONTEXT, cachedNetexData);

        // TODO: find out how to handle common data frames, this is how it was done in previous version
/*
        List<Object> foundFrames = new ArrayList<Object>();
        if (commonData != null) {
            for (PublicationDeliveryStructure common : commonData) {
                foundFrames.addAll(findFrames(class1, common, null));
            }
        }
*/

        // parse organisations (in resource frames)
        Parser organisationsParser = ParserFactory.create(OrganisationParser.class.getName());
        organisationsParser.parse(context);

        // parse stop places (in site frames)
        parseSiteFrames(context, netexReferential);

        // parse day types (in service calendar frame)
        Parser dayTypeParser = ParserFactory.create(DayTypeParser.class.getName());
        dayTypeParser.parse(context);

        List<ServiceFrame> serviceFrames = getFrames(ServiceFrame.class, compositeFrameOrCommonFrame);
        for (ServiceFrame serviceFrame : serviceFrames) {
            parseServiceFrame(context, referential, lineData, commonData, serviceFrame);
        }
        List<TimetableFrame> timetableFrames = getFrames(TimetableFrame.class, compositeFrameOrCommonFrame);
        for (TimetableFrame timetableFrame : timetableFrames) {
            parseTimetableFrame(context, referential, lineData, commonData, timetableFrame);
        }
    }

/*
    private void parseResourceFrames(Context context, NetexReferential referential) throws Exception {
        Collection<ResourceFrame> resourceFrames = referential.getResourceFrames().values();
        if (!isCollectionEmpty(resourceFrames)) {
            for (ResourceFrame resourceFrame : resourceFrames) {
            }
        }
    }
*/

    private void parseSiteFrames(Context context, NetexReferential referential) throws Exception {
        Collection<SiteFrame> siteFrames = referential.getSiteFrames().values();
        if (!isCollectionEmpty(siteFrames)) {
            for (SiteFrame siteFrame : siteFrames) {
                // TODO retrieve stop places from referential instead
                StopPlacesInFrame_RelStructure stopPlacesStruct = siteFrame.getStopPlaces();
                List<StopPlace> stopPlaces = stopPlacesStruct.getStopPlace();
                for (StopPlace stopPlace : stopPlaces) {
/*
                    StopArea stopArea = ObjectFactory.getStopArea(referential, stopPlace.getId());
                    stopArea.setName(stopPlace.getName().getValue());
                    stopArea.setRegistrationNumber(stopPlace.getShortName().getValue());
                    stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
                    stopArea.setFilled(true);
*/

                    // TODO: add support for boarding positions, and connect to StopArea as parent
                }
            }
        }
    }

    private void parseTimetableFrame(Context context, Referential referential, PublicationDeliveryStructure lineData,
                                     List<PublicationDeliveryStructure> commonData, TimetableFrame frame) throws Exception {
        context.put(NETEX_LINE_DATA_CONTEXT, frame);
        Parser timetableParser = ParserFactory.create(TimetableParser.class.getName());
        timetableParser.parse(context);
    }

    private void parseServiceCalendarFrame(Context context, Referential referential, PublicationDeliveryStructure lineData,
                                           List<PublicationDeliveryStructure> commonData, ServiceCalendarFrame serviceCalendarFrame) throws Exception {
        DayTypesInFrame_RelStructure dayTypesStruct = serviceCalendarFrame.getDayTypes();
        context.put(NETEX_LINE_DATA_CONTEXT, dayTypesStruct);
        Parser dayTypeParser = ParserFactory.create(DayTypeParser.class.getName());
        dayTypeParser.parse(context);
    }

    private void parseServiceFrame(Context context, Referential referential, PublicationDeliveryStructure lineData,
                                   List<PublicationDeliveryStructure> commonData, ServiceFrame serviceFrame) throws Exception {
        // TODO: consider as method argument instead
        Map<String, Object> cachedNetexData = (Map<String, Object>) context.get(NETEX_LINE_DATA_ID_CONTEXT);

        Network network = serviceFrame.getNetwork();
        mobi.chouette.model.Network ptNetwork = ObjectFactory.getPTNetwork(referential, network.getId());
        ptNetwork.setName(network.getName().getValue());

        RoutePointsInFrame_RelStructure routePointsStructure = serviceFrame.getRoutePoints();
        List<RoutePoint> routePoints = routePointsStructure.getRoutePoint();

        for (RoutePoint routePoint : routePoints) {
            cachedNetexData.put(routePoint.getId(), routePoint);
        }

        RoutesInFrame_RelStructure routesStructure = serviceFrame.getRoutes();
        context.put(NETEX_LINE_DATA_CONTEXT, routesStructure);
        Parser routeParser = ParserFactory.create(RouteParser.class.getName());
        routeParser.parse(context);

        LinesInFrame_RelStructure linesStructure = serviceFrame.getLines();
        context.put(NETEX_LINE_DATA_CONTEXT, linesStructure);
        Parser lineParser = ParserFactory.create(LineParser.class.getName());
        lineParser.parse(context);

        StopAssignmentsInFrame_RelStructure stopAssignmentsStructure = serviceFrame.getStopAssignments();
        List<JAXBElement<? extends StopAssignment_VersionStructure>> stopAssignmentElements = stopAssignmentsStructure.getStopAssignment();
        for (JAXBElement<? extends StopAssignment_VersionStructure> stopAssignmentElement : stopAssignmentElements) {
            PassengerStopAssignment passengerStopAssignment = (PassengerStopAssignment) stopAssignmentElement.getValue();

            ScheduledStopPointRefStructure scheduledStopPointRef = passengerStopAssignment.getScheduledStopPointRef();
            StopPlaceRefStructure stopPlaceRef = passengerStopAssignment.getStopPlaceRef();

            if (scheduledStopPointRef != null && StringUtils.isNotEmpty(scheduledStopPointRef.getRef()) &&
                    stopPlaceRef != null && StringUtils.isNotEmpty(stopPlaceRef.getRef())) {
                cachedNetexData.put(scheduledStopPointRef.getRef(), stopPlaceRef.getRef());
            }
        }

        ScheduledStopPointsInFrame_RelStructure scheduledStopPointsStructure = serviceFrame.getScheduledStopPoints();
        context.put(NETEX_LINE_DATA_CONTEXT, scheduledStopPointsStructure);
        Parser scheduledStopPointsParser = ParserFactory.create(ScheduledStopPointParser.class.getName());
        scheduledStopPointsParser.parse(context);

        JourneyPatternsInFrame_RelStructure journeyPatternsStructure = serviceFrame.getJourneyPatterns();
        context.put(NETEX_LINE_DATA_CONTEXT, journeyPatternsStructure);
        Parser journeyPatternParser = ParserFactory.create(JourneyPatternParser.class.getName());
        journeyPatternParser.parse(context);
    }

    private boolean isCollectionEmpty (Collection < ? > collection){
        return collection == null || collection.isEmpty();
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
