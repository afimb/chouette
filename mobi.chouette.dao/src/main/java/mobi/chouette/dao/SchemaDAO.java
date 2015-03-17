package mobi.chouette.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class SchemaDAO {

	public static final String SQL = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA";

	@PersistenceContext(unitName = "public")
	EntityManager em;

	@SuppressWarnings("unchecked")
	public List<String> getSchemaListing() {
		Query query = em.createNativeQuery(SQL);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public String getCurrentSchema() {
		Query query = em.createNativeQuery("SHOW search_path");
		List<String> list = query.getResultList();
		return (list != null && !list.isEmpty()) ? list.get(0) : "";
	}
	
	public void setCurrentSchema(String identifier) {
		Query query = em.createNativeQuery("SET SCHEMA '" + identifier + "'");
		query.executeUpdate();
	}
}
