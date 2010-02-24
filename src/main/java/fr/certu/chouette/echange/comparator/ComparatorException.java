/**
 * 
 */
package fr.certu.chouette.echange.comparator;

import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author michel
 *
 */
public class ComparatorException extends ServiceException
{
	public static enum TYPE {DuplicateKey, UnvailableResource, UnbuildResource};

	private static final long serialVersionUID = -443407083926147948L;

	public ComparatorException(TYPE type, String arg0)
	{
		super(toCode(type), CodeDetailIncident.DEFAULT, arg0);
	}

	private static CodeIncident toCode(TYPE type) 
	{
		switch (type) 
		{
		case DuplicateKey:
			return CodeIncident.COMPARATOR_DUPLICATED_KEY;
		case UnvailableResource:
			return CodeIncident.COMPARATOR_UNVAILABLE_RESOURCE; 
		}
		return CodeIncident.COMPARATOR_UNBUILD_RESOURCE;
	}

}