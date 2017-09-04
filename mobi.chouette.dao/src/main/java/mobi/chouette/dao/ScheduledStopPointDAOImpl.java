package mobi.chouette.dao;

import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.ScheduledStopPoint;
@Stateless
public class ScheduledStopPointDAOImpl extends GenericDAOImpl<ScheduledStopPoint> implements ScheduledStopPointDAO {

	public ScheduledStopPointDAOImpl() {
		super(ScheduledStopPoint.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public List<ScheduledStopPoint> getScheduledStopPointsContainedInStopArea(String stopAreaObjectId) {
		return em.createQuery("select ssp from ScheduledStopPoint ssp where ssp.containedInStopAreaObjectId=:stopAreaObjectId", ScheduledStopPoint.class).setParameter("stopAreaObjectId", stopAreaObjectId).getResultList();
	}

	@Override
	public int replaceContainedInStopAreaReferences(Set<String> oldStopAreaIds, String newStopAreaId) {
		if (oldStopAreaIds != null && oldStopAreaIds.size() > 0) {
			return em.createQuery("update ScheduledStopPoint ssp set ssp.containedInStopAreaObjectId=:newStopAreaId where " +
					"ssp.containedInStopAreaObjectId in (:oldStopAreaIds)").setParameter("oldStopAreaIds", oldStopAreaIds).setParameter("newStopAreaId", newStopAreaId).executeUpdate();
		}
		return 0;
	}

	/**
	 * Get in separate transactions in order to be able to iterate over all referentials
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Override
	public List<String> getAllStopAreaObjectIds() {
		return em.createQuery("select distinct(ssp.containedInStopAreaObjectId) from ScheduledStopPoint ssp where ssp.containedInStopAreaObjectId is not null", String.class).getResultList();
	}
}
