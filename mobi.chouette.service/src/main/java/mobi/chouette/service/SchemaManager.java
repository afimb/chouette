package mobi.chouette.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import mobi.chouette.dao.SchemaDAO;

@Stateless(name = SchemaManager.COMMAND)

public class SchemaManager
{

	public static final String COMMAND = "SchemaManager";

	@EJB
	SchemaDAO schemaDAO;

	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean validateReferential(String referential) {
		return schemaDAO.getSchemaListing().contains(referential);
		}
	

}
