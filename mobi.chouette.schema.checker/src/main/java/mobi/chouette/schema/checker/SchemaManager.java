package mobi.chouette.schema.checker;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import mobi.chouette.common.ContenerChecker;
import mobi.chouette.dao.SchemaDAO;

@Stateless(name = ContenerChecker.NAME)
public class SchemaManager implements ContenerChecker {

	@EJB
	SchemaDAO schemaDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean validateContener(String contenerName) {
		return schemaDAO.getSchemaListing().contains(contenerName);
	}

	@Override
	public String getContext() {
		return "iev";
	}

}
