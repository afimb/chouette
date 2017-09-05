package mobi.chouette.exchange.netexprofile.exporter.producer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;

import java.time.OffsetDateTime;

import org.rutebanken.netex.model.AuthorityRefStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.PrivateCodeStructure;

import mobi.chouette.common.Context;
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.netexprofile.ConversionUtil;

public class NetworkProducer extends NetexProducer implements NetexEntityProducer<org.rutebanken.netex.model.Network, mobi.chouette.model.Network> {

    @Override
    public org.rutebanken.netex.model.Network produce(Context context, mobi.chouette.model.Network neptuneNetwork) {
        org.rutebanken.netex.model.Network netexNetwork = netexFactory.createNetwork();
        
        NetexProducerUtils.populateId(neptuneNetwork, netexNetwork);

        if (isSet(neptuneNetwork.getVersionDate())) {
            OffsetDateTime changedDateTime = TimeUtil.toOffsetDateTime(neptuneNetwork.getVersionDate());
            netexNetwork.setChanged(changedDateTime);
        }

        if (isSet(neptuneNetwork.getComment())) {
            KeyValueStructure keyValueStruct = netexFactory.createKeyValueStructure()
                    .withKey("Comment")
                    .withValue(neptuneNetwork.getComment());
            netexNetwork.setKeyList(netexFactory.createKeyListStructure().withKeyValue(keyValueStruct));
        }

        netexNetwork.setName(ConversionUtil.getMultiLingualString(neptuneNetwork.getName()));
        netexNetwork.setDescription(ConversionUtil.getMultiLingualString(neptuneNetwork.getDescription()));

        if(neptuneNetwork.getCompany() != null) {
            AuthorityRefStructure authorityRefStruct = netexFactory.createAuthorityRefStructure();
        	NetexProducerUtils.populateReference(neptuneNetwork.getCompany(), authorityRefStruct, true);
            netexNetwork.setTransportOrganisationRef(netexFactory.createAuthorityRef(authorityRefStruct));
        }

        if (isSet(neptuneNetwork.getRegistrationNumber())) {
            PrivateCodeStructure privateCodeStruct = netexFactory.createPrivateCodeStructure();
            privateCodeStruct.setValue(neptuneNetwork.getRegistrationNumber());
            netexNetwork.setPrivateCode(privateCodeStruct);
        }

        return netexNetwork;
    }
}
