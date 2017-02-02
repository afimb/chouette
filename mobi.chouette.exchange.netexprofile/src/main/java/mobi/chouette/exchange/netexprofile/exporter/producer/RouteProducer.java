package mobi.chouette.exchange.netexprofile.exporter.producer;

import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.LineRefStructure;

import java.util.Collection;

public class RouteProducer extends AbstractJaxbNetexProducer<org.rutebanken.netex.model.Route, mobi.chouette.model.Route> {

    //@Override
    public org.rutebanken.netex.model.Route produce(mobi.chouette.model.Route chouetteRoute,
            Collection<mobi.chouette.model.Route> exportableRoutes, boolean addExtension) {

        org.rutebanken.netex.model.Route netexRoute = netexFactory.createRoute();
        populateFromModel(netexRoute, chouetteRoute);

        netexRoute.setName(getMultilingualString(chouetteRoute.getName()));

        if (StringUtils.isNotEmpty(chouetteRoute.getPublishedName())) {
            netexRoute.setShortName(getMultilingualString(chouetteRoute.getPublishedName()));
        }

        LineRefStructure lineRefStruct = netexFactory.createLineRefStructure();
        lineRefStruct.setVersion(chouetteRoute.getLine().getObjectVersion() != null ?
                String.valueOf(chouetteRoute.getLine().getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        lineRefStruct.setRef(chouetteRoute.getLine().getObjectId());
        netexRoute.setLineRef(netexFactory.createLineRef(lineRefStruct));

        // TODO add route points
        //netexRoute.setPointsInSequence();

        return netexRoute;
    }

}
