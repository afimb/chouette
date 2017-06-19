package mobi.chouette.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ReferentialDAOImpl implements ReferentialDAO {

    public static final String SQL = "SELECT SLUG FROM PUBLIC.REFERENTIALS";

    @PersistenceContext(unitName = "public")
    private EntityManager em;

    @Override
    public List<String> getReferentials() {
        Query query = em.createNativeQuery(SQL);
        return query.getResultList();
    }
}