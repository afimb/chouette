package mobi.chouette.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.extern.log4j.Log4j;
import mobi.chouette.dao.SchemaDAO;

@Stateless(name = SchemaManager.COMMAND)
@Log4j
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
