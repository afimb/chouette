package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.model.GtfsBean;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public abstract class AbstractProducer<T extends GtfsBean,N extends NeptuneIdentifiedObject>
implements IGtfsProducer<T, N>
{
   public abstract T produce(N neptuneObject,GtfsReport report);

   public abstract List<T> produceAll(N neptuneObject,GtfsReport report);

   public List<T> produceAll(Collection<N> neptuneObjects,GtfsReport report)
   {
      List<T> objects = new ArrayList<T>();
      for (N object : neptuneObjects)
      {
         T gtfsObject = produce(object,report);
         if (gtfsObject != null)
            objects.add(produce(object,report));
      }
      return objects;
   }


}
