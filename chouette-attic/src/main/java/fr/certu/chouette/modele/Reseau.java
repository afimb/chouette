package fr.certu.chouette.modele;

import java.util.Date;

import chouette.schema.PTNetwork;
import chouette.schema.Registration;
import chouette.schema.types.SourceTypeType;

public class Reseau extends BaseObjet
{
	private PTNetwork ptNetwork;

	public Reseau() {
		super();
		
		ptNetwork = new PTNetwork();
		ptNetwork.setRegistration( new Registration());
	}

	public PTNetwork getPtNetwork() {
		return ptNetwork;
	}

	public void setPtNetwork(final PTNetwork ptNetwork) 
	{
		if ( ptNetwork==null)
		{
			this.ptNetwork = new PTNetwork();
			this.ptNetwork.setRegistration( new Registration());
		}
		else
		{
			this.ptNetwork = ptNetwork;
			if ( ptNetwork.getRegistration()==null)
			{
				this.ptNetwork.setRegistration( new Registration());
			}
		}
	}

	public String getComment() {
		return ptNetwork.getComment();
	}

	public Date getCreationTime() {
		return ptNetwork.getCreationTime();
	}

	public String getCreatorId() {
		return ptNetwork.getCreatorId();
	}

	public String getDescription() {
		return ptNetwork.getDescription();
	}

	public String getName() {
		return ptNetwork.getName();
	}

	public String getObjectId() {
		return ptNetwork.getObjectId();
	}

	public int getObjectVersion() {
            setObjectVersion((int)ptNetwork.getObjectVersion());
		return (int)ptNetwork.getObjectVersion();
	}

	public String getSourceIdentifier() {
		return ptNetwork.getSourceIdentifier();
	}

	public String getSourceName() {
		return ptNetwork.getSourceName();
	}

	public SourceTypeType getSourceType() {
		return ptNetwork.getSourceType();
	}

	public Date getVersionDate() {
		if ( ptNetwork.getVersionDate()==null) return null;
		return ptNetwork.getVersionDate().toDate();
	}

	public String getRegistrationNumber() {
		return getPtNetwork().getRegistration().getRegistrationNumber();
	}

	public void setRegistrationNumber(String registrationNumber) {
		getPtNetwork().getRegistration().setRegistrationNumber(registrationNumber);
	}

	public void setComment(String comment) {
		ptNetwork.setComment(comment);
	}

	public void setCreationTime(Date creationTime) {
		ptNetwork.setCreationTime(creationTime);
	}

	public void setCreatorId(String creatorId) {
		ptNetwork.setCreatorId(creatorId);
	}

	public void setDescription(String description) {
		ptNetwork.setDescription(description);
	}

	public void setName(String name) {
		ptNetwork.setName(name);
	}

	public void setObjectId(String objectId) {
		ptNetwork.setObjectId(objectId);
	}

	public void setObjectVersion(int objectVersion) {
            if (objectVersion >= 1)
		ptNetwork.setObjectVersion(objectVersion);
            else
                ptNetwork.setObjectVersion(1);
	}

	public void setSourceIdentifier(String sourceIdentifier) {
		ptNetwork.setSourceIdentifier(sourceIdentifier);
	}

	public void setSourceName(String sourceName) {
		ptNetwork.setSourceName(sourceName);
	}

	public void setSourceType(SourceTypeType sourceType) {
		ptNetwork.setSourceType(sourceType);
	}

	public void setVersionDate( Date versionDate) {
		if ( versionDate==null) 
			ptNetwork.setVersionDate( null);
		else
			ptNetwork.setVersionDate( new org.exolab.castor.types.Date( versionDate.getTime()));
	}


}
