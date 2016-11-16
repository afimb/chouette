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
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.ScheduledStopPointsInFrame_RelStructure;

import java.util.Collection;
import java.util.List;

@Log4j
public class ScheduledStopPointParser implements NetexParser {

    private static final String BOARDING_POSITION_ID_SUFFIX = "01";

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

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        Collection<ScheduledStopPoint> scheduledStopPoints = netexReferential.getScheduledStopPoints().values();

        // TODO consider how to retrieve the stop points in the correct order from referential, maybe need for sorting first, also to be able to get the first stop in order
        int index = 1;
        for (ScheduledStopPoint scheduledStopPoint : scheduledStopPoints) {
            String scheduledStopPointId = scheduledStopPoint.getId();
            String chouetteStopPointId =  scheduledStopPointId + "-" + index;

            StopPoint stopPoint = ObjectFactory.getStopPoint(referential, chouetteStopPointId);
            stopPoint.setPosition(index);

            // TODO find out how to get a reference to the correct route to be set on this stop point, probably we need to cache the route id in this parser in init referential command, like we do for validators
            // the scheduled stop point ids are accessible from a route through the route points, for now using the only single route present
            for (Route route : referential.getRoutes().values()) {
                if (route.isFilled()) {
                    stopPoint.setRoute(route);
                }
            }

            // TODO find out if this stop area must be the same as the actual stop place's stop area created in PublicationDeliveryParser when parsing StopPlace's
            // TODO looks like this is searching for an existing stop area, and not creating a new if necessary, setting static value for now to test
            //String chouetteStopAreaId = scheduledStopPointId + "-" + BOARDING_POSITION_ID_SUFFIX;
            String chouetteStopAreaId = "NSR:StopPlace:0301152" + index + "-" + BOARDING_POSITION_ID_SUFFIX;
            StopArea stopArea = referential.getSharedStopAreas().get(chouetteStopAreaId);

            if(stopArea != null) {
                stopPoint.setContainedInStopArea(stopArea);

                // TODO find out if we need to set the line number or scheduled stop point name as a comment
                // Warn: Using comment field as temporary storage for line pointer. Used for lookup when parsing passing times
                //stopPoint.setComment(lineNumber);

                // optional
/*
                MultilingualString scheduledStopPointName = scheduledStopPoint.getName();
                if (scheduledStopPointName != null) {
                    String scheduledStopPointNameValue = scheduledStopPointName.getValue();
                    if (StringUtils.isNotEmpty(scheduledStopPointNameValue)) {
                        stopPoint.setComment(scheduledStopPointNameValue);
                    }
                }
*/

                // TODO find out how to make this stop point available to JourneyPatternParser and RouteParser
                // Add stop point to journey pattern AND route (for now)
                //journeyPattern.addStopPoint(stopPoint);
                //route.getStopPoints().add(stopPoint);
            } else {
                log.warn("StopArea with id " + chouetteStopAreaId + " not found ");
            }

            stopPoint.setFilled(true);
            index++;
        }
    }

    static {
        ParserFactory.register(ScheduledStopPointParser.class.getName(), new ParserFactory() {
            private ScheduledStopPointParser instance = new ScheduledStopPointParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
