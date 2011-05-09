package fr.certu.chouette.modele;

import java.util.Date;

import chouette.schema.JourneyPattern;
import chouette.schema.Registration;

public class Mission extends BaseObjet
{
	private JourneyPattern journeyPattern;

	public Mission() {
		super();
		journeyPattern = new JourneyPattern();
		journeyPattern.setRegistration( new Registration());
	}

	public JourneyPattern getJourneyPattern() {
		return journeyPattern;
	}

	public void setJourneyPattern(JourneyPattern journeyPattern) {
		if ( journeyPattern==null)
		{
			this.journeyPattern = new JourneyPattern();
			this.journeyPattern.setRegistration( new Registration());
		}
		else
		{
			this.journeyPattern = journeyPattern;
			if ( journeyPattern.getRegistration()==null)
			{
				this.journeyPattern.setRegistration( new Registration());
			}
		}
	}

	public String getRegistrationNumber() {
		return journeyPattern.getRegistration().getRegistrationNumber();
	}

	public void setRegistrationNumber(String registrationNumber) {
		journeyPattern.getRegistration().setRegistrationNumber(registrationNumber);
	}

	public String getComment() {
		return journeyPattern.getComment();
	}

	public Date getCreationTime() {
		return journeyPattern.getCreationTime();
	}

	public String getCreatorId() {
		return journeyPattern.getCreatorId();
	}

	public String getName() {
		return journeyPattern.getName();
	}

	public String getObjectId() {
		return journeyPattern.getObjectId();
	}

	public int getObjectVersion() {
            setObjectVersion((int)journeyPattern.getObjectVersion());
            return (int)journeyPattern.getObjectVersion();
	}

	public String getPublishedName() {
		return journeyPattern.getPublishedName();
	}

	public String getRouteId() {
		return journeyPattern.getRouteId();
	}

	public void setRouteId(String routeId) {
		journeyPattern.setRouteId(routeId);
	}

	public void setComment(String comment) {
		journeyPattern.setComment(comment);
	}

	public void setCreationTime(Date creationTime) {
		journeyPattern.setCreationTime(creationTime);
	}

	public void setCreatorId(String creatorId) {
		journeyPattern.setCreatorId(creatorId);
	}

	public void setName(String name) {
		journeyPattern.setName(name);
	}

	public void setObjectId(String objectId) {
		journeyPattern.setObjectId(objectId);
	}

	public void setObjectVersion(int objectVersion) {
            if (objectVersion >= 1)
		journeyPattern.setObjectVersion(objectVersion);
            else
                journeyPattern.setObjectVersion(1);
	}

	public void setPublishedName(String publishedName) {
		journeyPattern.setPublishedName(publishedName);
	}
	
	

}
