package mobi.chouette.dao.iev;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ReferentialDAO {

    public static final String SQL = "SELECT SLUG FROM REFERENTIALS";

    @PersistenceContext(unitName = "public")
    EntityManager em;

    @SuppressWarnings("unchecked")
    public List<String> getReferentials() {
        Query query = em.createNativeQuery(SQL);
        return query.getResultList();
    }

}
