package fr.certu.chouette.dao.hibernate;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.dao.hibernate.exception.HibernateDaoExceptionCode;
import fr.certu.chouette.dao.hibernate.exception.HibernateDaoRuntimeException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.filter.FilterOrder;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.NeptuneObject;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.plugin.model.CompilanceCheckTask;
import fr.certu.chouette.plugin.model.ExportLogMessage;
import fr.certu.chouette.plugin.model.GuiExport;
import fr.certu.chouette.plugin.model.ImportTask;
import fr.certu.chouette.plugin.model.Organisation;
import fr.certu.chouette.plugin.model.Referential;

public class HibernateDaoTemplate<T extends NeptuneObject> implements
      IDaoTemplate<T>
{
   private static final Logger logger = Logger.getLogger(HibernateDaoTemplate.class);

   private Class<T> type;

   private HibernateDaoTemplate(Class<T> type)
   {
      this.type = type;
   }

   public static HibernateDaoTemplate<AccessLink> createAccessLinkDao()
   {
      return new HibernateDaoTemplate<AccessLink>(AccessLink.class);
   }

   public static HibernateDaoTemplate<AccessPoint> createAccessPointDao()
   {
      return new HibernateDaoTemplate<AccessPoint>(AccessPoint.class);
   }

   public static HibernateDaoTemplate<Company> createCompanyDao()
   {
      return new HibernateDaoTemplate<Company>(Company.class);
   }

   public static HibernateDaoTemplate<ConnectionLink> createConnectionLinkDao()
   {
      return new HibernateDaoTemplate<ConnectionLink>(ConnectionLink.class);
   }

   public static HibernateDaoTemplate<Facility> createFacilityDao()
   {
      return new HibernateDaoTemplate<Facility>(Facility.class);
   }

   public static HibernateDaoTemplate<GroupOfLine> createGroupOfLineDao()
   {
      return new HibernateDaoTemplate<GroupOfLine>(GroupOfLine.class);
   }

   public static HibernateDaoTemplate<JourneyPattern> createJourneyPatternDao()
   {
      return new HibernateDaoTemplate<JourneyPattern>(JourneyPattern.class);
   }

   public static HibernateDaoTemplate<Line> createLineDao()
   {
      return new HibernateDaoTemplate<Line>(Line.class);
   }

   public static HibernateDaoTemplate<PTLink> createPTLinkDao()
   {
      return new HibernateDaoTemplate<PTLink>(PTLink.class);
   }

   public static HibernateDaoTemplate<PTNetwork> createPTNetworkDao()
   {
      return new HibernateDaoTemplate<PTNetwork>(PTNetwork.class);
   }

   public static HibernateDaoTemplate<Route> createRouteDao()
   {
      return new HibernateDaoTemplate<Route>(Route.class);
   }

   public static HibernateDaoTemplate<StopArea> createStopAreaDao()
   {
      return new HibernateDaoTemplate<StopArea>(StopArea.class);
   }

   public static HibernateDaoTemplate<StopPoint> createStopPointDao()
   {
      return new HibernateDaoTemplate<StopPoint>(StopPoint.class);
   }

   public static HibernateDaoTemplate<Timetable> createTimetableDao()
   {
      return new HibernateDaoTemplate<Timetable>(Timetable.class);
   }

   public static HibernateDaoTemplate<TimeSlot> createTimeSlotDao()
   {
      return new HibernateDaoTemplate<TimeSlot>(TimeSlot.class);
   }

   public static HibernateDaoTemplate<VehicleJourney> createVehicleJourneyDao()
   {
      return new HibernateDaoTemplate<VehicleJourney>(VehicleJourney.class);
   }

   public static HibernateDaoTemplate<Organisation> createOrganisationDao()
   {
      return new HibernateDaoTemplate<Organisation>(
            Organisation.class);
   }

   public static HibernateDaoTemplate<Referential> createReferentialDao()
   {
      return new HibernateDaoTemplate<Referential>(
            Referential.class);
   }

   public static HibernateDaoTemplate<ImportTask> createImportDao()
   {
      return new HibernateDaoTemplate<ImportTask>(
            ImportTask.class);
   }

   public static HibernateDaoTemplate<CompilanceCheckTask> createValidationDao()
   {
      return new HibernateDaoTemplate<CompilanceCheckTask>(
            CompilanceCheckTask.class);
   }

   public static HibernateDaoTemplate<GuiExport> createExportDao()
   {
      return new HibernateDaoTemplate<GuiExport>(GuiExport.class);
   }

   public static HibernateDaoTemplate<ExportLogMessage> createExportLogMessageDao()
   {
      return new HibernateDaoTemplate<ExportLogMessage>(
            ExportLogMessage.class);
   }

   @Getter
   @Setter
   private EntityManagerFactory entityManagerFactory;

   private EntityManager getEntityManager()
   {
      return EntityManagerFactoryUtils.getTransactionalEntityManager(getEntityManagerFactory());
   }

   @Override
   public T get(Long id)
   {
      if (id == null)
         return null;

      T object = getEntityManager().find(type, id);
      if (object == null)
      {
         return null;
      }
      return object;
   }

   @Override
   public List<T> select(final Filter filter)
   {
      List<T> result = null;

      logger.debug("invoke select on " + type.getSimpleName());

      EntityManager em = getEntityManager();
      CriteriaBuilder builder = em.getCriteriaBuilder();
      CriteriaQuery<T> criteria = builder.createQuery(type);
      Root<T> root = criteria.from(type);
      criteria.distinct(true);

      if (!filter.isEmpty())
      {
         FilterToHibernateClauseTranslator<T> translator = new FilterToHibernateClauseTranslator<T>();
         Predicate predicate = translator.translate(filter, builder, root);
         criteria.where(predicate);
      }

      if (filter.getOrderList() != null)
      {
         for (FilterOrder order : filter.getOrderList())
         {
            switch (order.getType())
            {
            case ASC:
               criteria.orderBy(builder.asc(root.get(order.getAttribute())));
               break;
            case DESC:
               criteria.orderBy(builder.desc(root.get(order.getAttribute())));
               break;

            default:
               break;
            }
         }
      }
      TypedQuery<T> query = em.createQuery(criteria);

      if (filter.getLimit() > 0 || filter.getStart() > 0)
      {
         logger.debug("call with start and/or limit");
         query.setMaxResults(filter.getLimit());
         query.setFirstResult(filter.getStart());
         result = query.getResultList();
      }
      else
      {
         result = query.getResultList();
      }
      logger.debug(type.getSimpleName() + " founds = " + result.size());

      return result;

   }

   public List<T> select(final String hql, final List<Object> values)
   {
      List<T> result = null;
      EntityManager em = getEntityManager();

      if (values.isEmpty())
      {
         TypedQuery<T> query = em.createQuery(hql, type);
         result = query.getResultList();
      }
      else
      {
         TypedQuery<T> query = em.createQuery(hql, type);
         int pos = 0;
         for (Object value : values)
         {
            query.setParameter(pos++, value);
         }
         result = query.getResultList();
      }
      return result;
   }

   @Override
   public T getByObjectId(final String objectId)
   {
      logger.debug("invoke getByObjectId on " + type.getSimpleName());
      if (objectId == null || objectId.isEmpty())
         return null;

      Filter filter = Filter.getNewEqualsFilter("objectId", objectId);
      List<T> list = select(filter);

      return list.get(0);
   }

   @Override
   public List<T> getAll()
   {
      logger.debug("invoke getAll on " + type.getSimpleName());
      Filter f = Filter.getNewEmptyFilter();
      return select(f);
   }

   @Override
   public void remove(Long id)
   {
      logger.debug("invoke remove on " + type.getSimpleName());
      getEntityManager().remove(get(id));
      getEntityManager().flush();
   }

   @Override
   public T save(T entity)
   {
      T result = null;

      logger.debug("invoke save on " + type.getSimpleName());

      EntityManager em = getEntityManager();
      try
      {
         em.persist(entity);
         result = entity;
      }
      catch (EntityExistsException e)
      {
         result = em.merge(entity);
      }
      return result;
   }

   @Override
   public T update(T entity)
   {
      T result = null;

      logger.debug("invoke update on " + type.getSimpleName());

      EntityManager em = getEntityManager();
      result = em.merge(entity);
      return result;
   }

   @Override
   public boolean exists(Long id)
   {
      try
      {
         return (get(id) != null);
      }
      catch (Exception e)
      {
         return false;
      }
   }

   @Override
   public boolean exists(String objectId)
   {
      try
      {
         return (getByObjectId(objectId) != null);
      }
      catch (Exception e)
      {
         return false;
      }
   }

   @Override
   public void removeAll(List<T> objects)
   {
      logger.debug("invoke removeAll on " + type.getSimpleName());

      EntityManager em = getEntityManager();
      for (T entity : objects)
      {
         em.remove(entity);
      }
      em.flush();
   }

   @Override
   public int removeAll(Filter clause)
   {
      int result = 0;

      logger.debug("invoke removeAll on " + type.getSimpleName());

      EntityManager em = getEntityManager();
      CriteriaBuilder builder = em.getCriteriaBuilder();
      CriteriaDelete<T> criteria = builder.createCriteriaDelete(type);
      Root<T> root = criteria.from(type);
      FilterToHibernateClauseTranslator<T> translator = new FilterToHibernateClauseTranslator<T>();
      Predicate predicate = translator.translate(clause, builder, root);
      criteria.where(predicate);
      Query query = em.createQuery(criteria);
      result = query.executeUpdate();

      return result;
   }

   @Override
   public void detach(List<T> beans)
   {
      for (T bean : beans)
      {
         detach(bean);
      }
   }

   @Override
   public void detach(T entity)
   {
      EntityManager em = getEntityManager();
      if (em.contains(entity))
      {
         em.detach(entity);
      }
   }

   @Override
   public int purge()
   {
      throw new HibernateDaoRuntimeException(HibernateDaoExceptionCode.NOT_YET_IMPLEMENTED, "purge");
   }

   @Override
   public long count(Filter clause)
   {
      long result = 0;
      // TODO [DSU] ?????????????????
      EntityManager em = getEntityManager();
      CriteriaBuilder builder = em.getCriteriaBuilder();
      CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
      Root<T> root = criteria.from(type);
      criteria.select(builder.count(root));
      if (clause != null)
      {
         FilterToHibernateClauseTranslator<T> translator = new FilterToHibernateClauseTranslator<T>();
         Predicate predicate = translator.translate(clause, builder, root);
         criteria.where(predicate);
      }

      result = em.createQuery(criteria).getSingleResult();

      return result;
   }

   public void saveOrUpdateAll(List<T> list)
   {
      logger.debug("invoke saveOrUpdateAll on " + type.getSimpleName());
      EntityManager em = getEntityManager();

      for (T item : list)
      {
         if (item instanceof NeptuneIdentifiedObject)
         {
            NeptuneIdentifiedObject object = (NeptuneIdentifiedObject) item;
            T entity = getByObjectId(object.getObjectId());
            if (entity != null)
            {
               logger.debug("update object :" + object.getObjectId());
               em.detach(entity);
               item.setId(entity.getId());
               em.merge(item);
            }
            else
            {
               logger.debug("save object :" + object.getObjectId());
               em.persist(entity);
            }
         }
      }

      em.flush();
   }

}
