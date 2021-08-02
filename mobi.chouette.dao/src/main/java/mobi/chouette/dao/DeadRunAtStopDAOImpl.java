package mobi.chouette.dao;

import mobi.chouette.model.DeadRunAtStop;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class DeadRunAtStopDAOImpl extends GenericDAOImpl<DeadRunAtStop> implements DeadRunAtStopDAO {

    public DeadRunAtStopDAOImpl() {
        super(DeadRunAtStop.class);
    }

    @PersistenceContext(unitName = "referential")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

}
