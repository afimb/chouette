package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.LineValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.RoutePointValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import org.rutebanken.netex.model.*;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

@Log4j
/**
 * The best is probably to have references to all parsers in this class where we have som kind of init-method.
 * We use this parser method to initialize all ids from all objects. Look at the example in ChouettePTNetworkParser
 * and ChouetteLineDescriptionParser.
 *
 * Each specific parser should implement both a init- and a parse method. The init method should be defined in an
 * abstract super class (AbstractParser) which implements the Parser interface.
 *
 * The contents of this class should be moved to the PublicationDeliveryParser class instead.
 */
public class NetexParser extends AbstractParser implements Parser {

    @Override
    public void initializeReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        if (referential == null) {
            referential = new NetexReferential();
            context.put(NETEX_REFERENTIAL, referential);
        } else {
            referential.clear();
        }
        PublicationDeliveryStructure publicationDelivery = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
        List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame = publicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame();
        parseResourceFrames(context, referential, topLevelFrame);
        parseSiteFrames(referential, topLevelFrame);
        parseServiceCalendarFrames(referential, topLevelFrame);
        parseServiceFrames(context, referential, topLevelFrame);
        parseTimetableFrames(context, referential, topLevelFrame);
    }

    // TODO rename/refactor to something more appropriate
    private void parseResourceFrames(Context context, NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) throws Exception {
        List<ResourceFrame> resourceFrames = getFrames(ResourceFrame.class, topLevelFrame);
        for (ResourceFrame resourceFrame : resourceFrames) {
            NetexObjectUtil.addResourceFrameReference(referential, resourceFrame.getId(), resourceFrame);

            // 1. initialize organisations
            OrganisationsInFrame_RelStructure organisationsInFrameStruct = resourceFrame.getOrganisations();
            context.put(NETEX_LINE_DATA_CONTEXT, organisationsInFrameStruct);
            OrganisationParser organisationParser = (OrganisationParser) ParserFactory.create(OrganisationParser.class.getName());
            organisationParser.initializeReferentials(context);
        }
    }

    // TODO rename/refactor to something more appropriate
    private void parseSiteFrames(NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) {
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

    // TODO rename/refactor to something more appropriate
    private void parseServiceCalendarFrames(NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) {
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

    // TODO rename/refactor to something more appropriate
    private void parseServiceFrames(Context context, NetexReferential referential,
            List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) throws Exception {
        List<ServiceFrame> serviceFrames = getFrames(ServiceFrame.class, topLevelFrame);
        for (ServiceFrame serviceFrame : serviceFrames) {
            NetexObjectUtil.addServiceFrameReference(referential, serviceFrame.getId(), serviceFrame);

            // 1. parse networks
            Network network = serviceFrame.getNetwork();
            context.put(NETEX_LINE_DATA_CONTEXT, network);
            NetworkParser networkParser = (NetworkParser) ParserFactory.create(NetworkParser.class.getName());
            networkParser.initializeReferentials(context);

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
            routeParser.initializeReferentials(context);

            // 4. parse lines
            LinesInFrame_RelStructure linesInFrameStruct = serviceFrame.getLines();
            context.put(NETEX_LINE_DATA_CONTEXT, linesInFrameStruct);
            LineParser lineParser = (LineParser) ParserFactory.create(LineParser.class.getName());
            lineParser.initializeReferentials(context);

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
            scheduledStopPointParser.initializeReferentials(context);

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

    // TODO rename/refactor to something more appropriate
    private void parseTimetableFrames(Context context, NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) {
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
        // TODO implement
    }

    static {
        ParserFactory.register(NetexParser.class.getName(), new ParserFactory() {
            private NetexParser instance = new NetexParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
