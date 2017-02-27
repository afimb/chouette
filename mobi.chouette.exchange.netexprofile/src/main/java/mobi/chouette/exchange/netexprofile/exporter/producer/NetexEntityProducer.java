package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.NeptuneIdentifiedObject;
import org.rutebanken.netex.model.DataManagedObjectStructure;

public interface NetexEntityProducer<T extends DataManagedObjectStructure, S extends NeptuneIdentifiedObject> {

    T produce(S originalObject);
}
