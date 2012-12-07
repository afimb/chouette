/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.model.neptune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;

/**
 *
 */
@SuppressWarnings("serial")
public class DbVehicleJourney extends fr.certu.chouette.model.neptune.VehicleJourney
{


   private List<VehicleJourneyAtStop> buffer = new ArrayList<VehicleJourneyAtStop>();

   @Getter @Setter private DbVehicleJourneyFactory factory;


   protected DbVehicleJourney(DbVehicleJourneyFactory factory)
   {
      this.factory = factory;
   }

   @Override 
   public List<VehicleJourneyAtStop> getVehicleJourneyAtStops()
   {
      if (!buffer.isEmpty()) return buffer; 
      return factory.getVehicleJourneyAtStops(this);
   }

   @Override 
   public void setVehicleJourneyAtStops(List<VehicleJourneyAtStop> beans)
   {
      throw new UnsupportedOperationException("just use add ") ;
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.VehicleJourney#addVehicleJourneyAtStop(fr.certu.chouette.model.neptune.VehicleJourneyAtStop)
    */
   @Override
   public void addVehicleJourneyAtStop(VehicleJourneyAtStop vehicleJourneyAtStop)
   {
      buffer.add(vehicleJourneyAtStop);
   }

   public void flush()
   {
      if (buffer.isEmpty()) return;
      Collections.sort(buffer, new VehicleJourneyAtStopComparator());
      int last = buffer.size() - 1;
      for (int i = 0; i < buffer.size(); i++)
      {
         VehicleJourneyAtStop bean = buffer.get(i);
         bean.setDeparture(i == 0);
         bean.setOrder(i + 1);
         bean.setArrival(i == last);
      }
      factory.addToBatch(this, buffer);
      buffer.clear();
   }



   /* (non-Javadoc)
    * @see java.lang.Object#finalize()
    */
   @Override
   protected void finalize() throws Throwable
   {
      super.finalize();
      factory.deleteVehicleJourney(this);
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.VehicleJourney#addVehicleJourneyAtStops(java.util.Collection)
    */
   @Override
   public void addVehicleJourneyAtStops(Collection<VehicleJourneyAtStop> vehicleJourneyAtStopCollection)
   {
      buffer.addAll(vehicleJourneyAtStopCollection);
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.VehicleJourney#removeStopPoint(fr.certu.chouette.model.neptune.StopPoint)
    */
   @Override
   public void removeStopPoint(StopPoint stopPoint)
   {
      throw new UnsupportedOperationException("forbidden ") ;
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.VehicleJourney#sortVehicleJourneyAtStops()
    */
   @Override
   public void sortVehicleJourneyAtStops()
   {
   }



}
