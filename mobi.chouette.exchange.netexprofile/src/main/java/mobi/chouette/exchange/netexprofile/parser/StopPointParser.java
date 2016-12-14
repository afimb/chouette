package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.ScheduledStopPointValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.*;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@Log4j
public class StopPointParser extends AbstractParser {

    public static final String LOCAL_CONTEXT = "StopPointContext";
    public static final String STOP_POINT_ID = "stopPointId";
    public static final String ROUTE_ID = "routeId";

    @Override
    public void initReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        ScheduledStopPointValidator validator = (ScheduledStopPointValidator) ValidatorFactory.create(ScheduledStopPointValidator.class.getName(), context);

        ScheduledStopPointsInFrame_RelStructure scheduledStopPointsStruct = (ScheduledStopPointsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<ScheduledStopPoint> scheduledStopPoints = scheduledStopPointsStruct.getScheduledStopPoint();
        for (ScheduledStopPoint scheduledStopPoint : scheduledStopPoints) {
            String objectId = scheduledStopPoint.getId();
            NetexObjectUtil.addScheduledStopPointReference(referential, objectId, scheduledStopPoint);
            validator.addObjectReference(context, scheduledStopPoint);
        }
    }

    public void addRouteIdRef(Context context, String objectId, String routeId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(ROUTE_ID, routeId);
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context localContext = (Context) parsingContext.get(LOCAL_CONTEXT);
        Context stopPlaceContext = (Context) parsingContext.get(StopPlaceParser.LOCAL_CONTEXT);

        // TODO consider how to retrieve the stop points in the correct order from referential, maybe need for sorting first, also to be able to get the first stop in order
        // TODO remember the connection from a netex ScheduledStopPoint to a netex StopPlace goes through/via stopAssignments -> PassengerStopAssignment, where we have references to both
        // TODO this means we must parse the stop assignments before we are parsing ScheduledStopPoints, because chouette StopPoints must be able to connect to a containedInStopArea, and also position (order)
        // TODO it is possible that we must not add stop points to the global netex referential, because we then might lose order, and only the collection of stop points for a given stop area, route and line.
        // TODO what happens now is that we iterate over all stop points, in all frames, all too general. We must preserve the original order and collection of stop points connected to a route and line. How?
        // TODO it might be a candidate for iterating over the service frame, and not the referential

        Collection<ScheduledStopPoint> scheduledStopPoints = netexReferential.getScheduledStopPoints().values();

        for (ScheduledStopPoint netexStopPoint : scheduledStopPoints) {
            String netexStopPointId = netexStopPoint.getId();
            Context objectContext = (Context) localContext.get(netexStopPointId);

            String scheduledStopPointId = netexStopPoint.getId();

            // TODO generate in object id creator
            String chouetteStopPointId = netexStopPoint.getId();
            StopPoint chouetteStopPoint = ObjectFactory.getStopPoint(chouetteReferential, chouetteStopPointId);
            addStopPointIdRef(context, netexStopPointId, chouetteStopPointId);

            // TODO we must fix the issue with parsing stop points from referential instead of per frame, this will cause errors with the actual order of stop points
            // TODO find out what's best: 1. to set the position during parsing of journey patterns 2. to set the position during parsing of stop points
            // TODO find out the best way to retrieve these objects, from referential or not?
            Collection<StopPointInJourneyPattern> stopPointsInJourneyPattern = netexReferential.getStopPointsInJourneyPattern().values();
            BigInteger stopPointOrder = null;

            for (StopPointInJourneyPattern stopPointInJourneyPattern : stopPointsInJourneyPattern) {
                ScheduledStopPointRefStructure stopPointRefStruct = stopPointInJourneyPattern.getScheduledStopPointRef().getValue();

                if (chouetteStopPointId.equals(stopPointRefStruct.getRef())) {
                    stopPointOrder = stopPointInJourneyPattern.getOrder();
                }
            }

            chouetteStopPoint.setPosition(stopPointOrder.intValue());

            // TODO map stop point ids to stop areas through passenger stop assignments in netex referential, an alternative is to add references during stop place parsing, to map ids
            // TODO also consider if we should get this from the currently processed frame instead, now we are getting all
            Collection<PassengerStopAssignment> stopAssignments = netexReferential.getPassengerStopAssignments().values();
            String stopPlaceIdRef = null;

            for (PassengerStopAssignment stopAssignment : stopAssignments) {
                ScheduledStopPointRefStructure stopPointRefStruct = stopAssignment.getScheduledStopPointRef();
                StopPlaceRefStructure stopPlaceRef = stopAssignment.getStopPlaceRef();

                if (chouetteStopPointId.equals(stopPointRefStruct.getRef())) {
                    stopPlaceIdRef = stopPlaceRef.getRef();
                }
            }

            Context stopPlaceObjectContext = (Context) stopPlaceContext.get(stopPlaceIdRef);
            String chouetteStopAreaId = (String) stopPlaceObjectContext.get(StopPlaceParser.STOP_AREA_ID);

            // TODO find out which is best of these 2 alternatives, for now defaulting to ObjectFactory

            StopArea stopArea = ObjectFactory.getStopArea(chouetteReferential, chouetteStopAreaId);
            chouetteStopPoint.setContainedInStopArea(stopArea);

            // StopArea stopArea = chouetteReferential.getSharedStopAreas().get(chouetteStopAreaId);
            // chouetteStopPoint.setContainedInStopArea(stopArea);

            // TODO find out if we need to set the line number or scheduled stop point name as a comment, for now setting name
            chouetteStopPoint.setComment(netexStopPoint.getName().getValue());

            // mandatory
            String chouetteRouteId = (String) objectContext.get(ROUTE_ID);
            Route route = ObjectFactory.getRoute(chouetteReferential, chouetteRouteId);
            chouetteStopPoint.setRoute(route);
            
            chouetteStopPoint.setFilled(true);
        }
    }

    private void addStopPointIdRef(Context context, String objectId, String stopPointId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(STOP_POINT_ID, stopPointId);
    }

    static {
        ParserFactory.register(StopPointParser.class.getName(), new ParserFactory() {
            private StopPointParser instance = new StopPointParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
