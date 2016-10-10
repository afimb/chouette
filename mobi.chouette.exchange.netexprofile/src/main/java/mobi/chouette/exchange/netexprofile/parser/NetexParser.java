package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import no.rutebanken.netex.model.*;

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

    private void parseResourceFrames(Context context, NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) throws Exception {
        List<ResourceFrame> resourceFrames = getFrames(ResourceFrame.class, topLevelFrame);
        for (ResourceFrame resourceFrame : resourceFrames) {

            // 1. initialize organisations
            OrganisationsInFrame_RelStructure organisationsInFrameStruct = resourceFrame.getOrganisations();
            context.put(NETEX_LINE_DATA_CONTEXT, organisationsInFrameStruct);
            OrganisationParser organisationParser = (OrganisationParser) ParserFactory.create(OrganisationParser.class.getName());
            organisationParser.initializeReferentials(context);
        }
    }

    private void parseSiteFrames(NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) {
        List<SiteFrame> siteFrames = getFrames(SiteFrame.class, topLevelFrame);
        for (SiteFrame siteFrame : siteFrames) {
            StopPlacesInFrame_RelStructure stopPlacesStruct = siteFrame.getStopPlaces();
            List<StopPlace> stopPlaces = stopPlacesStruct.getStopPlace();
            for (StopPlace stopPlace : stopPlaces) {
                // TODO implement
            }
        }
    }

    private void parseServiceCalendarFrames(NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) {
        List<ServiceCalendarFrame> serviceCalendarFrames = getFrames(ServiceCalendarFrame.class, topLevelFrame);
        for (ServiceCalendarFrame serviceCalendarFrame : serviceCalendarFrames) {
            DayTypesInFrame_RelStructure dayTypeStruct = serviceCalendarFrame.getDayTypes();
            List<JAXBElement<? extends DataManagedObjectStructure>> dayTypeStructElements = dayTypeStruct.getDayType_();
            for (JAXBElement<? extends DataManagedObjectStructure> dayTypeStructElement : dayTypeStructElements) {
                DayType dayType = (DayType) dayTypeStructElement.getValue();
                // TODO consider generating a more sophisticated id
                NetexObjectUtil.addDayTypeReference(referential, dayType.getId(), dayType);
            }
        }
    }

    private void parseServiceFrames(Context context, NetexReferential referential,
            List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) throws Exception {
        List<ServiceFrame> serviceFrames = getFrames(ServiceFrame.class, topLevelFrame);
        for (ServiceFrame serviceFrame : serviceFrames) {

            // 1. parse networks
            Network network = serviceFrame.getNetwork();

            // 2. parse route points
            RoutePointsInFrame_RelStructure routePointsStructure = serviceFrame.getRoutePoints();
            List<RoutePoint> routePoints = routePointsStructure.getRoutePoint();
            for (RoutePoint routePoint : routePoints) {
                // TODO implement
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
            List<ScheduledStopPoint> scheduledStopPoints = scheduledStopPointStruct.getScheduledStopPoint();
            for (ScheduledStopPoint scheduledStopPoint : scheduledStopPoints) {
                // TODO consider generating a more sophisticated id
                NetexObjectUtil.addScheduledStopPointReference(referential, scheduledStopPoint.getId(), scheduledStopPoint);
            }

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

    private void parseTimetableFrames(Context context, NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) {
        List<TimetableFrame> timetableFrames = getFrames(TimetableFrame.class, topLevelFrame);
        for (TimetableFrame timetableFrame : timetableFrames) {
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
