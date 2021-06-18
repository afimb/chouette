package mobi.chouette.dao;

import lombok.extern.log4j.Log4j;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Stateless(name = "ReferentialLastUpdateDAO")
@Log4j
public class ReferentialLastUpdateDAOImpl implements ReferentialLastUpdateDAO {

    private static final String SQL_SELECT_LAST_UPDATE_TIMESTAMP = "SELECT last_update_timestamp FROM referential_last_update";
    private static final String SQL_UPDATE_LAST_UPDATE_TIMESTAMP = "UPDATE referential_last_update SET last_update_timestamp=:last_update_timestamp";

    @PersistenceContext(unitName = "referential")
    private EntityManager em;


    @Override
    public LocalDateTime getLastUpdateTimestamp() {

        Query lastUpdateQuery = em.createNativeQuery(SQL_SELECT_LAST_UPDATE_TIMESTAMP);
        Timestamp timestamp = (Timestamp) lastUpdateQuery.getSingleResult();
        return timestamp.toLocalDateTime();

    }

    @Override
    public void setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
        Query updateLastUpdateQuery = em.createNativeQuery(SQL_UPDATE_LAST_UPDATE_TIMESTAMP);
        updateLastUpdateQuery.setParameter("last_update_timestamp", Timestamp.valueOf(lastUpdateTimestamp));
        updateLastUpdateQuery.executeUpdate();
    }

}
