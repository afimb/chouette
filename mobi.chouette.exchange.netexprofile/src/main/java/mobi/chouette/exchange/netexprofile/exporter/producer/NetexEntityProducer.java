package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.model.NeptuneIdentifiedObject;
import org.rutebanken.netex.model.DataManagedObjectStructure;

public interface NetexEntityProducer<T extends DataManagedObjectStructure, S extends NeptuneIdentifiedObject> {

    T produce(Context context, S originalObject);
}
