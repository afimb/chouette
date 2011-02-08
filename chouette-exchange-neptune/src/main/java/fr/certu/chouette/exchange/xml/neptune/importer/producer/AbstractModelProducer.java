package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.Date;

import chouette.schema.Registration;
import chouette.schema.TridentObjectTypeType;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public abstract class AbstractModelProducer<T extends NeptuneIdentifiedObject, U extends TridentObjectTypeType> implements IModelProducer<T, U>
{

	public void populateTridentObject(T target,U source)
	{
		// ObjectId : maybe null but not empty
		// TODO : Mandatory ?
		target.setObjectId(getNonEmptyTrimedString(source.getObjectId()));

		// ObjectVersion
		if (source.hasObjectVersion()) 
		{
			int castorObjectVersion = (int)source.getObjectVersion();
			target.setObjectVersion(castorObjectVersion);
		}

		// CreationTime : maybe null
		target.setCreationTime(source.getCreationTime());

		// CreatorId : maybe null but not empty
		target.setCreatorId(getNonEmptyTrimedString(source.getCreatorId()));

	}

	protected String getNonEmptyTrimedString(String source)
	{
		if (source == null) return null;
		String target = source.trim();
		return (target.length() ==0? null: target);
	}

	protected String getRegistrationNumber(Registration registration) 
	{
		if (registration == null) return null;
		String number = registration.getRegistrationNumber();
		if (number == null) return null;
		number=number.trim();
		return (number.length() == 0?null:number);
	}

	protected Date getDate(org.exolab.castor.types.Date castorDate) {
		if(castorDate == null) return null;
		Date date = castorDate.toDate();
		return date;
	}
	
	protected Date getTime(org.exolab.castor.types.Time castorTime) {
		if(castorTime == null) return null;
		Date date = castorTime.toDate();
		return date;
	}
	
	public abstract T produce(U o);
}
