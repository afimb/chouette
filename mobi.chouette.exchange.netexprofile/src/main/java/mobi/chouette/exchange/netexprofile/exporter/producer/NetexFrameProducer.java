package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import org.rutebanken.netex.model.Common_VersionFrameStructure;

public interface NetexFrameProducer<T extends Common_VersionFrameStructure> {

    T produce(ExportableData data);
}
