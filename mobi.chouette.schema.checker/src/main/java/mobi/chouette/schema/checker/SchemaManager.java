package mobi.chouette.schema.checker;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import mobi.chouette.dao.SchemaDAO;
import mobi.chouette.service.ContenerCheckerInterface;

@Stateless(name = ContenerCheckerInterface.NAME)

public class SchemaManager implements ContenerCheckerInterface
{

	@EJB
	SchemaDAO schemaDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean validateContener(String contenerName) {
		return schemaDAO.getSchemaListing().contains(contenerName);
		}
	

}
