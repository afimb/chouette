package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.StopPoint;

import java.util.List;
import java.util.Set;

@Stateless
public class StopPointDAOImpl extends GenericDAOImpl<StopPoint> implements StopPointDAO {

    public StopPointDAOImpl() {
        super(StopPoint.class);
    }

    @PersistenceContext(unitName = "referential")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<StopPoint> getStopPointsContainedInStopArea(String stopAreaObjectId) {
        return em.createQuery("select sp from StopPoint sp where sp.containedInStopAreaObjectId=:stopAreaObjectId", StopPoint.class).setParameter("stopAreaObjectId", stopAreaObjectId).getResultList();
    }

    @Override
    public void replaceContainedInStopAreaReferences(Set<String> oldStopAreaIds, String newStopAreaId) {
        if (oldStopAreaIds != null && oldStopAreaIds.size() > 0) {
            em.createQuery("update StopPoint sp set sp.containedInStopAreaObjectId=:newStopAreaId where " +
                    "sp.containedInStopAreaObjectId in (:oldStopAreaIds)").setParameter("oldStopAreaIds", oldStopAreaIds).setParameter("newStopAreaId", newStopAreaId).executeUpdate();
        }
    }
}
