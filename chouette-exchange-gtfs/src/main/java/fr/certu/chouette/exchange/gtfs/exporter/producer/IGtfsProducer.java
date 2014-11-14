package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.util.Collection;
import java.util.List;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsObject;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public interface IGtfsProducer<T extends GtfsObject, N extends NeptuneIdentifiedObject>
{

   T produce(N neptuneObject, GtfsReport report, String prefix);

   T produce(Collection<N> neptuneObjects, GtfsReport report, String prefix);
   
   List<T> produceAll(N neptuneObject, GtfsReport report, String prefix);

}
