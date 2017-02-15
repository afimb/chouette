package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.Route;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.RouteRefStructure;

import java.util.Collection;

public class JourneyPatternProducer extends AbstractNetexProducer<JourneyPattern, mobi.chouette.model.JourneyPattern> {

    //@Override
    public org.rutebanken.netex.model.JourneyPattern produce(mobi.chouette.model.JourneyPattern chouetteJourneyPattern, Collection<Route> exportableRoutes, boolean addExtension) {
        org.rutebanken.netex.model.JourneyPattern netexJourneyPattern = netexFactory.createJourneyPattern();
        populateFromModel(netexJourneyPattern, chouetteJourneyPattern);

        if (StringUtils.isNotEmpty(chouetteJourneyPattern.getName())) {
            netexJourneyPattern.setName(getMultilingualString(chouetteJourneyPattern.getName()));
        }
        if (StringUtils.isNotEmpty(chouetteJourneyPattern.getPublishedName())) {
            netexJourneyPattern.setShortName(getMultilingualString(chouetteJourneyPattern.getPublishedName()));
        }

        Route chouetteRoute = chouetteJourneyPattern.getRoute();
        RouteRefStructure routeRefStruct = netexFactory.createRouteRefStructure();
        routeRefStruct.setVersion(chouetteRoute.getObjectVersion() != null ? String.valueOf(chouetteRoute.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        routeRefStruct.setRef(chouetteRoute.getObjectId());
        netexJourneyPattern.setRouteRef(routeRefStruct);

        // TODO add points in sequence
        //netexJourneyPattern.setPointsInSequence();

        return netexJourneyPattern;
    }

}
