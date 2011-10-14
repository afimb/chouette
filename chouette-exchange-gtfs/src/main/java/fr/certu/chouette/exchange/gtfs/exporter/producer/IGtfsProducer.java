package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.util.Collection;
import java.util.List;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.model.GtfsBean;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public interface IGtfsProducer<T extends GtfsBean,N extends NeptuneIdentifiedObject>
{

   List<T> produceAll(Collection<N> neptuneObjects,GtfsReport report);
   
   
}
