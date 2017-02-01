package mobi.chouette.exchange.netexprofile.exporter.producer;

import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.RelationshipStructure;

public abstract class AbstractJaxbNetexProducer<T extends RelationshipStructure> {

    public static ObjectFactory netexFactory = null;

    static {
        try {
            netexFactory = new ObjectFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
