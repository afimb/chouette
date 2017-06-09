package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.StopPoint;

import java.util.List;

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
}
