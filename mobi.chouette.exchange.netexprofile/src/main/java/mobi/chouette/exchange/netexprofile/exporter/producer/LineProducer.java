package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.Route;
import mobi.chouette.model.type.TransportModeNameEnum;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import java.util.Collection;

public class LineProducer extends AbstractJaxbNetexProducer<org.rutebanken.netex.model.Line, mobi.chouette.model.Line> {

    //@Override
    public Line produce(mobi.chouette.model.Line chouetteLine, Collection<Route> exportableRoutes, boolean addExtension) {
        org.rutebanken.netex.model.Line netexLine = netexFactory.createLine();
        populateFromModel(netexLine, chouetteLine);

        // TODO null checks where necessary
        netexLine.setName(getMultilingualString(chouetteLine.getName()));
        netexLine.setPublicCode(chouetteLine.getNumber());

        if (StringUtils.isNotEmpty(chouetteLine.getPublishedName())) {
            netexLine.setShortName(getMultilingualString(chouetteLine.getPublishedName()));
        }

        TransportModeNameEnum transportMode = chouetteLine.getTransportModeName();
        if (transportMode != null) {
            AllVehicleModesOfTransportEnumeration vehicleModeOfTransport = toVehicleModeOfTransportEnum(transportMode.name());
            netexLine.setTransportMode(vehicleModeOfTransport);
        }

        OperatorRefStructure operatorRefStruct = netexFactory.createOperatorRefStructure();
        operatorRefStruct.setRef(chouetteLine.getCompany().getObjectId());

        // TODO handle version attribute differently, false when in separate export (common file), true if in same export
        //line.setOperatorRef(isFrequentOperator ? netexObjectFactory.createOperatorRefStructure(operatorId, Boolean.FALSE) : netexObjectFactory.createOperatorRefStructure(operatorId, Boolean.TRUE));
        //withRefValidation ? operatorRefStruct.withVersion(VERSION_ONE) : operatorRefStruct;

        netexLine.setOperatorRef(operatorRefStruct);

        RouteRefs_RelStructure routeRefsStruct = netexFactory.createRouteRefs_RelStructure();
        for (Route route : chouetteLine.getRoutes()) {
            if (exportableRoutes.contains(route)) {
                RouteRefStructure routeRefStruct = netexFactory.createRouteRefStructure();
                routeRefStruct.setVersion(route.getObjectVersion() != null ? String.valueOf(route.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
                routeRefStruct.setRef(route.getObjectId());
                routeRefsStruct.getRouteRef().add(routeRefStruct);
            }
        }
        netexLine.setRoutes(routeRefsStruct);

        return netexLine;
    }

}
