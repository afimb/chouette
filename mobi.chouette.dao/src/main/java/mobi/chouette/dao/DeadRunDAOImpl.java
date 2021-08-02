package mobi.chouette.dao;

import mobi.chouette.model.DeadRun;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class DeadRunDAOImpl extends GenericDAOImpl<DeadRun> implements DeadRunDAO {

    public DeadRunDAOImpl() {
        super(DeadRun.class);
    }

    @PersistenceContext(unitName = "referential")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

}
