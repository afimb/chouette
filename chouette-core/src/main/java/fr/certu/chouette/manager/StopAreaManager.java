/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.core.CoreException;
import fr.certu.chouette.core.CoreExceptionCode;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.plugin.validation.ValidationReport;

/**
 * @author michel
 *
 */
@SuppressWarnings("unchecked")
public class StopAreaManager extends AbstractNeptuneManager<StopArea> 
{
   private static final Logger logger = Logger.getLogger(StopAreaManager.class);

   public StopAreaManager() 
   {
      super(StopArea.class,StopArea.STOPAREA_KEY);
   }

   @Override
   protected Report propagateValidation(User user, List<StopArea> beans,
         ValidationParameters parameters,boolean propagate) 
   throws ChouetteException 
   {
      Report globalReport = new ValidationReport();

      // aggregate dependent objects for validation
      Set<ConnectionLink> links = new HashSet<ConnectionLink>();
      for (StopArea bean : beans) 
      {
         if (bean.getConnectionLinks() != null)
         {
            links.addAll(bean.getConnectionLinks());
         }

      }

      // propagate validation on ConnectionLink
      if (links.size() > 0)
      {
         Report report = null;
         AbstractNeptuneManager<ConnectionLink> manager = (AbstractNeptuneManager<ConnectionLink>) getManager(ConnectionLink.class);
         if (manager.canValidate())
         {
            report = manager.validate(user, Arrays.asList(links.toArray(new ConnectionLink[0])), parameters,propagate);
         }
         else
         {
            report = manager.propagateValidation(user, Arrays.asList(links.toArray(new ConnectionLink[0])), parameters,propagate);
         }
         if (report != null)
         {
            globalReport.addAll(report.getItems());
            globalReport.updateStatus(report.getStatus());
         }
      }


      return globalReport;
   }
   @Transactional
   @Override
   public void remove(User user,StopArea stopArea,boolean propagate) throws ChouetteException
   {
      List<StopPoint> stopPoints = stopArea.getContainedStopPoints();
      if(stopPoints != null && !stopPoints.isEmpty())
         throw new CoreException(CoreExceptionCode.DELETE_IMPOSSIBLE,"can't be deleted because it has a stopPoints");

      super.remove(user, stopArea,propagate);		
   }

   @Override
   protected Logger getLogger() 
   {
      return logger;
   }	
   @Transactional
   @Override
   public void saveAll(User user, List<StopArea> stopAreas, boolean propagate,boolean fast) throws ChouetteException 
   {
      getLogger().debug("try to save "+stopAreas.size()+" StopAreas");

      List<StopArea> completeStopAreas = new ArrayList<StopArea>();
      List<AccessLink> accessLinks = new ArrayList<AccessLink>();
      List<ConnectionLink> connectionLinks = new ArrayList<ConnectionLink>();
      List<Facility> facilities = new ArrayList<Facility>();
      if (propagate)
      {
         saveParents(user,stopAreas,propagate,fast,accessLinks,connectionLinks,facilities);
         mergeCollection(completeStopAreas,stopAreas);

         for (StopArea stopArea : completeStopAreas) 
         {
            mergeCollection(accessLinks, stopArea.getAccessLinks());
            mergeCollection(connectionLinks, stopArea.getConnectionLinks());
            mergeCollection(facilities, stopArea.getFacilities());
         }

         // add targetConnectionLink if not present
         List<StopArea> connected = new ArrayList<StopArea>();
         for (Iterator<ConnectionLink> iterator = connectionLinks.iterator(); iterator.hasNext();) 
         {
            ConnectionLink connectionLink = iterator.next();
            if (connectionLink.getStartOfLink() != null)  
            {
               if (!completeStopAreas.contains(connectionLink.getStartOfLink()))
                  addIfMissingInCollection(connected, connectionLink.getStartOfLink());
            }
            if (connectionLink.getEndOfLink() != null)
            {
               if (!completeStopAreas.contains(connectionLink.getEndOfLink()))
                  addIfMissingInCollection(connected,connectionLink.getEndOfLink());
            }
         }
         saveParents(user,connected,propagate,fast,accessLinks,connectionLinks,facilities);
         mergeCollection(completeStopAreas, connected);
      }
      else
      {
         completeStopAreas = stopAreas;
      }

      super.saveAll(user, completeStopAreas,propagate,fast);

      if(propagate)
      {
         INeptuneManager<AccessLink> accessLinkManager = (INeptuneManager<AccessLink>) getManager(AccessLink.class);
         INeptuneManager<ConnectionLink> connectionLinkManager = (INeptuneManager<ConnectionLink>) getManager(ConnectionLink.class);
         //			INeptuneManager<RestrictionConstraint> constraintManager = (INeptuneManager<RestrictionConstraint>) getManager(RestrictionConstraint.class);
         INeptuneManager<Facility> facilityManager = (INeptuneManager<Facility>) getManager(Facility.class);

         if(!accessLinks.isEmpty())
         {
            accessLinkManager.saveAll(user, accessLinks, propagate,fast);
         }
         if(!connectionLinks.isEmpty())
         {
            connectionLinkManager.saveAll(user, connectionLinks, propagate,fast);
         }
         //			if(!constraints.isEmpty())
         //				constraintManager.saveAll(user, constraints, propagate,fast);	
         if(!facilities.isEmpty())
         {
            facilityManager.saveAll(user, facilities, propagate,fast);
         }
      }
   }

   /**
    * @param stopAreas
    * @param facilities 
    * @param connectionLinks 
    * @param accessLinks 
    * @return
    * @throws ChouetteException 
    */
   private void saveParents(User user,List<StopArea> stopAreas,boolean propagate,boolean fast, List<AccessLink> accessLinks, List<ConnectionLink> connectionLinks, List<Facility> facilities) throws ChouetteException 
   {
      List<StopArea> parents = new ArrayList<StopArea>();
      if (stopAreas != null)
      {
         for (StopArea stopArea : stopAreas) 
         {
            if (stopArea.getParents() != null)
            {
               for (StopArea parent : stopArea.getParents())
               {
                  addIfMissingInCollection(parents,parent);
               }

            }
         }
         if (!parents.isEmpty())
         {
            saveParents(user, parents, propagate, fast,accessLinks,connectionLinks,facilities);
            for (StopArea stopArea : parents) 
            {
               mergeCollection(accessLinks, stopArea.getAccessLinks());
               mergeCollection(connectionLinks, stopArea.getConnectionLinks());
               mergeCollection(facilities, stopArea.getFacilities());
            }

            super.saveAll(user, parents, propagate, fast);
            getLogger().debug("saving "+parents.size()+" parents");
         }
      }
      return;
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.manager.AbstractNeptuneManager#completeObject(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneIdentifiedObject)
    */
   @Override
   public void completeObject(User user, StopArea stopArea)
   throws ChouetteException 
   {
      stopArea.complete();
   }


}
