package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import chouette.schema.Registration;
import chouette.schema.TridentObjectTypeType;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public abstract class AbstractCastorNeptuneProducer<T extends TridentObjectTypeType, U extends NeptuneIdentifiedObject> implements ICastorNeptuneProducer<T, U>
{

	public void populateFromModel(T target,U source)
	{
		// ObjectId : maybe null but not empty
		// TODO : Mandatory ?
		target.setObjectId(source.getObjectId());

		// ObjectVersion
		target.setObjectVersion(source.getObjectVersion());

		// CreationTime : maybe null
		target.setCreationTime(source.getCreationTime());

		// CreatorId : maybe null but not empty
		target.setCreatorId(source.getCreatorId());

	}
	
	protected Registration getRegistration(String registrationNumber) 
	{
		if (registrationNumber == null) return null;
		Registration registration = new Registration();
		registration.setRegistrationNumber(registrationNumber);
		return registration;
	}
	
	protected String getNonEmptyObjectId(NeptuneIdentifiedObject object) 
	{
		if (object == null) return null;
		return object.getObjectId();
	}
	
	public abstract T produce(U o);
}
