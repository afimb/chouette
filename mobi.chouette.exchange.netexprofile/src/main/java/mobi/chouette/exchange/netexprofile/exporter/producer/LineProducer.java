package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Route;
import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.*;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class LineProducer extends NetexProducer implements NetexEntityProducer<org.rutebanken.netex.model.Line, mobi.chouette.model.Line> {

    @Override
    public org.rutebanken.netex.model.Line produce(Context context, mobi.chouette.model.Line neptuneLine) {
        org.rutebanken.netex.model.Line netexLine = netexFactory.createLine();

        netexLine.setVersion(neptuneLine.getObjectVersion() > 0 ? String.valueOf(neptuneLine.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        String lineId = netexId(neptuneLine.objectIdPrefix(), LINE, neptuneLine.objectIdSuffix());
        netexLine.setId(lineId);

        if (isSet(neptuneLine.getName())) {
            netexLine.setName(getMultilingualString(neptuneLine.getName()));
        } else {
            if (isSet(neptuneLine.getPublishedName())) {
                netexLine.setName(getMultilingualString(neptuneLine.getPublishedName()));
            } else if (isSet(neptuneLine.getNumber())) {
                netexLine.setName(getMultilingualString(neptuneLine.getNumber()));
            } else {
                netexLine.setName(getMultilingualString(neptuneLine.objectIdSuffix()));
            }
        }

        if (isSet(neptuneLine.getPublishedName())) {
            netexLine.setShortName(getMultilingualString(neptuneLine.getPublishedName()));
        }

        if (isSet(neptuneLine.getComment())) {
            netexLine.setDescription(getMultilingualString(neptuneLine.getComment()));
        }

        if (isSet(neptuneLine.getTransportModeName())) {
            AllVehicleModesOfTransportEnumeration vehicleModeOfTransport = ConversionUtil.toVehicleModeOfTransportEnum(neptuneLine.getTransportModeName());
            netexLine.setTransportMode(vehicleModeOfTransport);
        }

        netexLine.setTransportSubmode(ConversionUtil.toTransportSubmodeStructure(neptuneLine.getTransportSubModeName()));

        if (isSet(neptuneLine.getNumber())) {
            netexLine.setPublicCode(neptuneLine.getNumber());
        }

        if (isSet(neptuneLine.getRegistrationNumber())) {
            PrivateCodeStructure privateCodeStruct = netexFactory.createPrivateCodeStructure();
            privateCodeStruct.setValue(neptuneLine.getRegistrationNumber());
            netexLine.setPrivateCode(privateCodeStruct);
        }

        OperatorRefStructure operatorRefStruct = netexFactory.createOperatorRefStructure();
        String operatorId = netexId(neptuneLine.getCompany().objectIdPrefix(), NetexObjectIdTypes.OPERATOR, neptuneLine.getCompany().objectIdSuffix());
        operatorRefStruct.setRef(operatorId);
        netexLine.setOperatorRef(operatorRefStruct);

        RouteRefs_RelStructure routeRefsStruct = netexFactory.createRouteRefs_RelStructure();
        for (Route route : neptuneLine.getRoutes()) {
            RouteRefStructure routeRefStruct = netexFactory.createRouteRefStructure();
            routeRefStruct.setVersion(route.getObjectVersion() != null ? String.valueOf(route.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
            String routeIdRef = netexId(route.objectIdPrefix(), ROUTE, route.objectIdSuffix());
            routeRefStruct.setRef(routeIdRef);
            routeRefsStruct.getRouteRef().add(routeRefStruct);
        }
        netexLine.setRoutes(routeRefsStruct);

        if (CollectionUtils.isNotEmpty(neptuneLine.getGroupOfLines())) {
            GroupOfLine groupOfLine = neptuneLine.getGroupOfLines().get(0);
            String groupOfLinesId = netexId(groupOfLine.objectIdPrefix(), GROUP_OF_LINES, groupOfLine.objectIdSuffix());
            GroupOfLinesRefStructure groupOfLinesRefStruct = netexFactory.createGroupOfLinesRefStructure().withRef(groupOfLinesId);
            netexLine.setRepresentedByGroupRef(groupOfLinesRefStruct);
        } else {
            mobi.chouette.model.Network neptuneNetwork = neptuneLine.getNetwork();
            String networkId = netexId(neptuneNetwork.objectIdPrefix(), NETWORK, neptuneNetwork.objectIdSuffix());
            GroupOfLinesRefStructure groupOfLinesRefStruct = netexFactory.createGroupOfLinesRefStructure().withRef(networkId);
            netexLine.setRepresentedByGroupRef(groupOfLinesRefStruct);
        }

        return netexLine;
    }

}
