package fr.certu.chouette.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.core.CoreException;
import fr.certu.chouette.core.CoreExceptionCode;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.plugin.validation.ValidationReport;

@SuppressWarnings("unchecked")
public class StopPointManager extends AbstractNeptuneManager<StopPoint> 
{
   private static final Logger logger = Logger.getLogger(StopPointManager.class);

   public StopPointManager() 
   {
      super(StopPoint.class,StopPoint.STOPPOINT_KEY);
   }

   @Override
   protected Report propagateValidation(User user, List<StopPoint> beans,
         ValidationParameters parameters,boolean propagate) 
   throws ChouetteException 
   {
      Report globalReport = new ValidationReport();

      // aggregate dependent objects for validation
      Set<StopArea> areas = new HashSet<StopArea>();
      for (StopPoint bean : beans) 
      {
         if (bean.getContainedInStopArea() != null)
         {
            addParentHierarchy(areas,bean.getContainedInStopArea());
         }

      }

      // propagate validation on StopArea
      if (areas.size() > 0)
      {
         Report report = null;
         AbstractNeptuneManager<StopArea> manager = (AbstractNeptuneManager<StopArea>) getManager(StopArea.class);
         if (manager.canValidate())
         {
            report = manager.validate(user, Arrays.asList(areas.toArray(new StopArea[0])), parameters,propagate);
         }
         else
         {
            report = manager.propagateValidation(user, Arrays.asList(areas.toArray(new StopArea[0])), parameters,propagate);
         }
         if (report != null)
         {
            globalReport.addAll(report.getItems());
            globalReport.updateStatus(report.getStatus());
         }
      }


      return globalReport;
   }

   private void addParentHierarchy(Set<StopArea> areas,StopArea area) 
   {
      if (area == null) return;
      if (areas.contains(area)) return;
      areas.add(area);
      if (area.getParents() != null)
      {
         for (StopArea parent : area.getParents())
         {
            addParentHierarchy(areas,parent);
         }
      }

      if (area.getConnectionLinks() != null)
      {
         for (ConnectionLink link : area.getConnectionLinks())
         {
            StopArea start = link.getStartOfLink();
            StopArea end = link.getEndOfLink();
            if (start != null && !areas.contains(start))
            {
               areas.add(start);
            }
            if (end != null && !areas.contains(end))
            {
               areas.add(end);
            }
         }
      }
      return ;
   }
   @Transactional
   @Override
   public void remove(User user,StopPoint stopPoint,boolean propagate) throws ChouetteException
   {
      INeptuneManager<PTLink> ptLinkManager  = (INeptuneManager<PTLink>) getManager(PTLink.class);
      INeptuneManager<VehicleJourney> vjManager = (INeptuneManager<VehicleJourney>) getManager(VehicleJourney.class);
      INeptuneManager<Facility> facilityManager = (INeptuneManager<Facility>) getManager(Facility.class);

      StopPoint next = get(user, Filter.getNewEqualsFilter("position", stopPoint.getPosition() +1));
      List<PTLink> ptLinks = ptLinkManager.getAll(user, Filter.getNewOrFilter(
            Filter.getNewEqualsFilter("startOfLink.id", stopPoint.getId()),
            Filter.getNewEqualsFilter("endOfLink.id", stopPoint.getId()))); 
      if(ptLinks != null && !ptLinks.isEmpty())
      {
         int size = ptLinks.size(); 
         if(size > 1){
            for (PTLink ptLink : ptLinks) 
            {
               if(ptLink.getEndOfLink().getId().equals(stopPoint.getId())){
                  ptLink.setEndOfLink(next);
                  ptLinkManager.update(user, ptLink);
               }
               else
                  ptLinkManager.remove(user, ptLink,propagate);
            }
         }else if(size == 1)
            ptLinkManager.remove(user, ptLinks.get(0),propagate);
      }
      Facility facility = facilityManager.get(user, Filter.getNewEqualsFilter("stopPoint.id", stopPoint.getId()));
      if(facility != null)
         facilityManager.remove(user, facility,propagate);
      List<VehicleJourney> vjs = vjManager.getAll(user, Filter.getNewEqualsFilter("route.id",
            stopPoint.getRoute().getId()));

      for (VehicleJourney vehicleJourney : vjs) 
      {
         List<VehicleJourneyAtStop> vAtStops = vehicleJourney.getVehicleJourneyAtStops();
         for (int i=0;i< vAtStops.size();i++) 
         {
            VehicleJourneyAtStop vAtStop = vAtStops.get(i);
            if(vAtStop.getStopPoint().equals(stopPoint)) 
            {
               if(vAtStop.isDeparture())
               {
                  VehicleJourneyAtStop nextAStop =  i< vAtStops.size() ? vAtStops.get(i +1) : vAtStop;
                  nextAStop.setDeparture(true);
               }
               vAtStops.remove(vAtStop);
            }
         }

         vjManager.update(null, vehicleJourney);
      }
      List<StopPoint> stopPoints4Route = getAll(user, Filter.getNewAndFilter(
            Filter.getNewEqualsFilter("route.id", stopPoint.getRoute().getId()),
            Filter.getNewGreaterFilter("position", stopPoint.getPosition())));
      for (StopPoint  sp : stopPoints4Route) 
      {
         sp.setPosition(sp.getPosition() - 1);
         update(user, sp);
      }
      super.remove(user, stopPoint,propagate);
   }

   @Override
   protected Logger getLogger() {
      return logger;
   }

   @Override
   public void completeObject(User user, StopPoint stopPoint) throws ChouetteException 
   {
      stopPoint.complete();
   }
   @Transactional
   @Override
   public void saveAll(User user, List<StopPoint> stopPoints, boolean propagate,boolean fast) throws ChouetteException 
   {
      getLogger().debug("try to save "+stopPoints.size()+" StopPoints");
      if(propagate)
      {
         INeptuneManager<StopArea> stopAreaManager = (INeptuneManager<StopArea>) getManager(StopArea.class);
         INeptuneManager<Facility> facilityManager = (INeptuneManager<Facility>) getManager(Facility.class);

         List<StopArea> stopAreas = new ArrayList<StopArea>();
         List<Facility> facilities = new ArrayList<Facility>();

         for (StopPoint stopPoint : stopPoints) 
         {
            addIfMissingInCollection(stopAreas, stopPoint.getContainedInStopArea());
            mergeCollection(facilities,stopPoint.getFacilities());
         }

         if(!stopAreas.isEmpty())
            stopAreaManager.saveAll(user, stopAreas, propagate,fast);

         super.saveAll(user, stopPoints, propagate,fast);

         if(!facilities.isEmpty())
            facilityManager.saveAll(user, facilities, propagate,fast);
      }
      else
      {
         super.saveAll(user, stopPoints,propagate,fast);	
      }
   }
   @Transactional
   @Override
   public int removeAll(User user, Filter filter) throws ChouetteException 
   {
      if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
      if (filter.getType().equals(Filter.Type.EQUALS))
      {
         //			INeptuneManager<PTLink> ptlinkManager = (INeptuneManager<PTLink>) getManager(PTLink.class);
         //			INeptuneManager<JourneyPattern> jpManager = (INeptuneManager<JourneyPattern>) getManager(JourneyPattern.class);
         //			INeptuneManager<StopPoint> stopPointManager = (INeptuneManager<StopPoint>) getManager(StopPoint.class);
         //	        Filter dependentFilter = Filter.getNewEqualsFilter("stopPoint."+filter.getAttribute(), filter.getFirstValue());
         //	        ptlinkManager.removeAll(user, dependentFilter);
         //	        jpManager.removeAll(user, dependentFilter);
         //	        stopPointManager.removeAll(user, dependentFilter);
      }
      else
      {
         throw new CoreException(CoreExceptionCode.DELETE_IMPOSSIBLE,"unvalid filter");
      }
      int ret =  getDao().removeAll(filter);
      logger.debug(""+ret+" stopPoints deleted");
      return ret;

   }

}
