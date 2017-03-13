package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.Route;
import org.rutebanken.netex.model.*;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.LINE_KEY;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.ROUTE_KEY;

public class LineProducer extends NetexProducer implements NetexEntityProducer<org.rutebanken.netex.model.Line, mobi.chouette.model.Line> {

    @Override
    public org.rutebanken.netex.model.Line produce(mobi.chouette.model.Line neptuneLine) {
        org.rutebanken.netex.model.Line netexLine = netexFactory.createLine();

        netexLine.setVersion(neptuneLine.getObjectVersion() > 0 ? String.valueOf(neptuneLine.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        String lineId = netexId(neptuneLine.objectIdPrefix(), LINE_KEY, neptuneLine.objectIdSuffix());
        netexLine.setId(lineId);

        if (isSet(neptuneLine.getName())) {
            netexLine.setName(getMultilingualString(neptuneLine.getName()));
        }

        if (isSet(neptuneLine.getPublishedName())) {
            netexLine.setShortName(getMultilingualString(neptuneLine.getPublishedName()));
        }

        if (isSet(neptuneLine.getComment())) {
            netexLine.setDescription(getMultilingualString(neptuneLine.getComment()));
        }

        if (isSet(neptuneLine.getTransportModeName())) {
            AllVehicleModesOfTransportEnumeration vehicleModeOfTransport = NetexProducerUtils.toVehicleModeOfTransportEnum(neptuneLine.getTransportModeName().name());
            netexLine.setTransportMode(vehicleModeOfTransport);
        }

        if (isSet(neptuneLine .getNumber())) {
            netexLine.setPublicCode(neptuneLine.getNumber());
        }

        if (isSet(neptuneLine.getRegistrationNumber())) {
            PrivateCodeStructure privateCodeStruct = netexFactory.createPrivateCodeStructure();
            privateCodeStruct.setValue(neptuneLine.getRegistrationNumber());
            netexLine.setPrivateCode(privateCodeStruct);
        }

        OperatorRefStructure operatorRefStruct = netexFactory.createOperatorRefStructure();
        operatorRefStruct.setRef(neptuneLine.getCompany().getObjectId());

        // TODO handle version attribute differently, false when in separate export (common file), true if in same export, for now only supporting single line files
        //line.setOperatorRef(isFrequentOperator ? netexObjectFactory.createOperatorRefStructure(operatorId, Boolean.FALSE) : netexObjectFactory.createOperatorRefStructure(operatorId, Boolean.TRUE));
        //withRefValidation ? operatorRefStruct.withVersion(VERSION_ONE) : operatorRefStruct;
        operatorRefStruct.setVersion(String.valueOf(neptuneLine.getCompany().getObjectVersion()));
        operatorRefStruct.setVersion(neptuneLine.getCompany().getObjectVersion() != null ? String.valueOf(neptuneLine.getCompany().getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        netexLine.setOperatorRef(operatorRefStruct);

        RouteRefs_RelStructure routeRefsStruct = netexFactory.createRouteRefs_RelStructure();
        for (Route route : neptuneLine.getRoutes()) {
                RouteRefStructure routeRefStruct = netexFactory.createRouteRefStructure();
                routeRefStruct.setVersion(route.getObjectVersion() != null ? String.valueOf(route.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
                String routeIdRef = netexId(route.objectIdPrefix(), ROUTE_KEY, route.objectIdSuffix());
                routeRefStruct.setRef(routeIdRef);
                routeRefsStruct.getRouteRef().add(routeRefStruct);
        }
        netexLine.setRoutes(routeRefsStruct);

        return netexLine;
    }

}
