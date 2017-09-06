package mobi.chouette.exchange.netexprofile.exporter.producer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.GROUP_OF_LINES;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.GroupOfLinesRefStructure;
import org.rutebanken.netex.model.OperatorRefStructure;
import org.rutebanken.netex.model.PrivateCodeStructure;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.model.GroupOfLine;

public class LineProducer extends NetexProducer implements NetexEntityProducer<org.rutebanken.netex.model.Line, mobi.chouette.model.Line> {

    @Override
    public org.rutebanken.netex.model.Line produce(Context context, mobi.chouette.model.Line neptuneLine) {
        org.rutebanken.netex.model.Line netexLine = netexFactory.createLine();

        NetexProducerUtils.populateId(neptuneLine, netexLine);

        if (isSet(neptuneLine.getName())) {
            netexLine.setName(ConversionUtil.getMultiLingualString(neptuneLine.getName()));
        } else {
            if (isSet(neptuneLine.getPublishedName())) {
                netexLine.setName(ConversionUtil.getMultiLingualString(neptuneLine.getPublishedName()));
            } else if (isSet(neptuneLine.getNumber())) {
                netexLine.setName(ConversionUtil.getMultiLingualString(neptuneLine.getNumber()));
            } else {
                netexLine.setName(ConversionUtil.getMultiLingualString(neptuneLine.objectIdSuffix()));
            }
        }

        if (isSet(neptuneLine.getPublishedName())) {
            netexLine.setShortName(ConversionUtil.getMultiLingualString(neptuneLine.getPublishedName()));
        }

        if (isSet(neptuneLine.getComment())) {
            netexLine.setDescription(ConversionUtil.getMultiLingualString(neptuneLine.getComment()));
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
        NetexProducerUtils.populateReference(neptuneLine.getCompany(), operatorRefStruct, false);
        netexLine.setOperatorRef(operatorRefStruct);

        if (CollectionUtils.isNotEmpty(neptuneLine.getGroupOfLines())) {
            GroupOfLine groupOfLine = neptuneLine.getGroupOfLines().get(0);
            String groupOfLinesId = netexId(groupOfLine.objectIdPrefix(), GROUP_OF_LINES, groupOfLine.objectIdSuffix());
            GroupOfLinesRefStructure groupOfLinesRefStruct = netexFactory.createGroupOfLinesRefStructure().withRef(groupOfLinesId);
            netexLine.setRepresentedByGroupRef(groupOfLinesRefStruct);
        } else {
            mobi.chouette.model.Network neptuneNetwork = neptuneLine.getNetwork();
            GroupOfLinesRefStructure groupOfLinesRefStruct = netexFactory.createGroupOfLinesRefStructure();
            NetexProducerUtils.populateReference(neptuneNetwork, groupOfLinesRefStruct, false);
            netexLine.setRepresentedByGroupRef(groupOfLinesRefStruct);
        }

        return netexLine;
    }

}
