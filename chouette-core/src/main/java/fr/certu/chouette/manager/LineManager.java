/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.core.CoreException;
import fr.certu.chouette.core.CoreExceptionCode;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

/**
 * 
 */
@SuppressWarnings("unchecked")
public class LineManager extends AbstractNeptuneManager<Line>
{
   private static final Logger logger = Logger.getLogger(LineManager.class);

   public LineManager()
   {
      super(Line.class, Line.LINE_KEY);
   }

   @Override
   protected void propagateValidation(User user, List<Line> beans,
         JSONObject parameters, PhaseReportItem report, Map<String, Object> validationContext, boolean propagate)
         throws ChouetteException
   {

      Set<PTNetwork> networks = new HashSet<PTNetwork>();
      Set<Company> companies = new HashSet<Company>();
      Set<GroupOfLine> groupOfLines = new HashSet<GroupOfLine>();
      Set<ConnectionLink> connectionLinks = new HashSet<ConnectionLink>();
      Set<StopArea> stopAreas = new HashSet<StopArea>();
      Set<Timetable> timetables = new HashSet<Timetable>();
      Set<TimeSlot> timeSlots = new HashSet<TimeSlot>();
      Set<AccessLink> accessLinks = new HashSet<AccessLink>();
      Set<AccessPoint> accessPoints = new HashSet<AccessPoint>();
      Set<Facility> facilities = new HashSet<Facility>();

      List<PTNetwork> networkList = new ArrayList<PTNetwork>();
      List<Company> companyList = new ArrayList<Company>();
      List<GroupOfLine> groupOfLineList = new ArrayList<GroupOfLine>();
      List<ConnectionLink> connectionLinkList = new ArrayList<ConnectionLink>();
      List<StopArea> stopAreaList = new ArrayList<StopArea>();
      List<Timetable> timetableList = new ArrayList<Timetable>();
      List<TimeSlot> timeSlotList = new ArrayList<TimeSlot>();
      List<AccessLink> accessLinkList = new ArrayList<AccessLink>();
      List<AccessPoint> accessPointList = new ArrayList<AccessPoint>();
      List<Facility> facilityList = new ArrayList<Facility>();

      for (Line line : beans)
      {
         line.complete();
         if (line.getPtNetwork() != null)
            networks.add(line.getPtNetwork());
         if (line.getCompany() != null)
            companies.add(line.getCompany());
         if (line.getGroupOfLines() != null)
            groupOfLines.addAll(line.getGroupOfLines());
         if (line.getConnectionLinks() != null)
            connectionLinks.addAll(line.getConnectionLinks());
         if (line.getStopAreas() != null)
            stopAreas.addAll(line.getStopAreas());
         if (line.getTimetables() != null)
            timetables.addAll(line.getTimetables());
         if (line.getAccessLinks() != null)
            accessLinks.addAll(line.getAccessLinks());
         if (line.getAccessPoints() != null)
            accessPoints.addAll(line.getAccessPoints());
      }
      networkList.addAll(networks);
      companyList.addAll(companies);
      groupOfLineList.addAll(groupOfLines);
      connectionLinkList.addAll(connectionLinks);
      stopAreaList.addAll(stopAreas);
      timetableList.addAll(timetables);
      timeSlotList.addAll(timeSlots);
      accessLinkList.addAll(accessLinks);
      accessPointList.addAll(accessPoints);

      // propagate validation on networks
      if (networks.size() > 0)
      {
         AbstractNeptuneManager<PTNetwork> manager = (AbstractNeptuneManager<PTNetwork>) getManager(PTNetwork.class);
         validateReport(user, manager, networkList, parameters, report,validationContext,
               propagate);
      }

      // propagate validation on companies
      if (companies.size() > 0)
      {
         AbstractNeptuneManager<Company> manager = (AbstractNeptuneManager<Company>) getManager(Company.class);
         validateReport(user, manager, companyList, parameters, report,validationContext,
               propagate);
      }

      // propagate validation on connectionLinks
      if (!connectionLinks.isEmpty())
      {
         AbstractNeptuneManager<ConnectionLink> manager = (AbstractNeptuneManager<ConnectionLink>) getManager(ConnectionLink.class);
         validateReport(user, manager, connectionLinkList, parameters, report,validationContext,
               propagate);
      }

      // propagate validation on stopAreas
      if (!stopAreas.isEmpty())
      {
         AbstractNeptuneManager<StopArea> manager = (AbstractNeptuneManager<StopArea>) getManager(StopArea.class);
         validateReport(user, manager, stopAreaList, parameters, report,validationContext,
               propagate);
      }

      // propagate validation on timetables
      if (!timetables.isEmpty())
      {
         AbstractNeptuneManager<Timetable> manager = (AbstractNeptuneManager<Timetable>) getManager(Timetable.class);
         validateReport(user, manager, timetableList, parameters, report, validationContext,
               propagate);
      }

      // propagate validation on timeSlots
      if (!timeSlots.isEmpty())
      {
         AbstractNeptuneManager<TimeSlot> manager = (AbstractNeptuneManager<TimeSlot>) getManager(TimeSlot.class);
         validateReport(user, manager, timeSlotList, parameters, report, validationContext,
               propagate);
      }

      // propagate validation on accessLinks
      if (!accessLinks.isEmpty())
      {
         AbstractNeptuneManager<AccessLink> manager = (AbstractNeptuneManager<AccessLink>) getManager(AccessLink.class);
         validateReport(user, manager, accessLinkList, parameters, report, validationContext,
               propagate);
      }

      // propagate validation on accessPoints
      if (!accessPoints.isEmpty())
      {
         AbstractNeptuneManager<AccessPoint> manager = (AbstractNeptuneManager<AccessPoint>) getManager(AccessPoint.class);
         validateReport(user, manager, accessPointList, parameters, report, validationContext,
               propagate);
      }

      // propagate validation on facilities
      if (!facilities.isEmpty())
      {
         AbstractNeptuneManager<Facility> manager = (AbstractNeptuneManager<Facility>) getManager(Facility.class);
         validateReport(user, manager, facilityList, parameters, report, validationContext,
               propagate);
      }

      // propagate validation on groupOfLines
      if (!groupOfLines.isEmpty())
      {
         AbstractNeptuneManager<GroupOfLine> manager = (AbstractNeptuneManager<GroupOfLine>) getManager(GroupOfLine.class);
         validateReport(user, manager, groupOfLineList, parameters, report, validationContext,
               propagate);
      }

      return;
   }

   /**
    * Used in propagate validation process
    * 
    * @param <T>
    * @param user
    * @param manager
    * @param list
    * @param parameters
    * @param propagate
    * @return
    * @throws ChouetteException
    */
   private <T extends NeptuneIdentifiedObject> void validateReport(User user,
         AbstractNeptuneManager<T> manager, List<T> list,
         JSONObject parameters, PhaseReportItem report, Map<String, Object> validationContext, boolean propagate)
         throws ChouetteException
   {
      if (!list.isEmpty())
      {
         if (manager.canValidate())
         {
            manager.validate(user, list, parameters, report,validationContext, propagate);
         } else if (propagate)
         {
            manager.propagateValidation(user, list, parameters, report,validationContext,
                  propagate);
         }
      }
   }

   @Override
   protected Logger getLogger()
   {
      return logger;
   }

   @Override
   public void completeObject(User user, Line line) throws ChouetteException
   {
      line.complete();
   }

   @Transactional
   @Override
   public void saveAll(User user, List<Line> lines, boolean propagate,
         boolean fast) throws ChouetteException
   {
      logger.debug("start saving line collection");
      if (propagate)
      {
         INeptuneManager<Route> routeManager = (INeptuneManager<Route>) getManager(Route.class);
         INeptuneManager<Company> companyManager = (INeptuneManager<Company>) getManager(Company.class);
         INeptuneManager<PTNetwork> networkManager = (INeptuneManager<PTNetwork>) getManager(PTNetwork.class);
         INeptuneManager<GroupOfLine> groupOfLineManager = (INeptuneManager<GroupOfLine>) getManager(GroupOfLine.class);
         INeptuneManager<Facility> facilityManager = (INeptuneManager<Facility>) getManager(Facility.class);
         INeptuneManager<StopArea> stopAreaManager = (INeptuneManager<StopArea>) getManager(StopArea.class);

         List<PTNetwork> networks = new ArrayList<PTNetwork>();
         List<Company> companies = new ArrayList<Company>();
         List<GroupOfLine> groupOfLines = new ArrayList<GroupOfLine>();
         List<Route> routes = new ArrayList<Route>();
         List<Facility> facilities = new ArrayList<Facility>();
         List<StopArea> routingConstraints = new ArrayList<StopArea>();

         for (Line line : lines)
         {
            addIfMissingInCollection(companies, line.getCompany());
            mergeCollection(groupOfLines, line.getGroupOfLines());
            addIfMissingInCollection(networks, line.getPtNetwork());
            mergeCollection(routes, line.getRoutes());
            // mergeCollection(facilities,line.getFacilities());
            mergeCollection(routingConstraints, line.getRoutingConstraints());
         }
         if (!companies.isEmpty())
            companyManager.saveAll(user, companies, propagate, fast);
         if (!groupOfLines.isEmpty())
            groupOfLineManager.saveAll(user, groupOfLines, propagate, fast);
         if (!networks.isEmpty())
            networkManager.saveAll(user, networks, propagate, fast);

         super.saveAll(user, lines, propagate, fast);

         if (!routes.isEmpty())
            routeManager.saveAll(user, routes, propagate, fast);
         if (!facilities.isEmpty())
            facilityManager.saveAll(user, facilities, propagate, fast);
         if (!routingConstraints.isEmpty())
            stopAreaManager.saveAll(user, routingConstraints, propagate, fast);
      } else
      {
         super.saveAll(user, lines, propagate, fast);
      }
      logger.debug("end saving line collection");
   }

   @Transactional
   @Override
   public int removeAll(User user, Filter filter) throws ChouetteException
   {
      if (getDao() == null)
         throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,
               "unavailable resource");
      if (filter.getType().equals(Filter.Type.EQUALS))
      {
         // INeptuneManager<Route> routeManager = (INeptuneManager<Route>)
         // getManager(Route.class);
         INeptuneManager<Facility> facilityManager = (INeptuneManager<Facility>) getManager(Facility.class);
         // INeptuneManager<RestrictionConstraint>
         // restrictionConstraintManager =
         // (INeptuneManager<RestrictionConstraint>)
         // getManager(RestrictionConstraint.class);
         Filter dependentFilter = Filter.getNewEqualsFilter(
               "line." + filter.getAttribute(), filter.getFirstValue());
         // routeManager.removeAll(user, dependentFilter);
         facilityManager.removeAll(user, dependentFilter);
         // restrictionConstraintManager.removeAll(user, dependentFilter);
      } else
      {
         throw new CoreException(CoreExceptionCode.DELETE_IMPOSSIBLE,
               "unvalid filter");
      }
      int ret = getDao().removeAll(filter);
      logger.debug("" + ret + " lines deleted");
      return ret;

   }

}
