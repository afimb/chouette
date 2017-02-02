package mobi.chouette.exchange.netexprofile.exporter.producer;

public class NetworkProducer extends AbstractJaxbNetexProducer<org.rutebanken.netex.model.Network, mobi.chouette.model.Network> {

    //@Override
    public org.rutebanken.netex.model.Network produce(mobi.chouette.model.Network ptNetwork, boolean addExtension) {
        org.rutebanken.netex.model.Network netexNetwork = netexFactory.createNetwork();

        populateFromModel(netexNetwork, ptNetwork);
        netexNetwork.setName(getMultilingualString(ptNetwork.getName()));

        return netexNetwork;
    }
}
