package fr.certu.chouette.modele;

import java.util.Date;

import chouette.schema.Line;
import chouette.schema.Registration;
import chouette.schema.types.TransportModeNameType;

public class Ligne extends BaseObjet
{
	private Line line;

	private Long idReseau;
	private Long idTransporteur;

	public Ligne() {
		super();
		
		line = new Line();
		line.setRegistration( new Registration());
	}

	public Line getLine() {
		return line;
	}

	public void setLine(final Line line) {
		if ( line==null)
		{
			this.line = new Line();
			this.line.setRegistration( new Registration());
		}
		else 
		{
			this.line = line;
			if ( line.getRegistration()==null)
			{
				this.line.setRegistration( new Registration());
			}
		}
	}

	public Long getIdReseau() {
		return idReseau;
	}

	public void setIdReseau(final Long idReseau) {
		this.idReseau = idReseau;
	}

	public Long getIdTransporteur() {
		return idTransporteur;
	}

	public void setIdTransporteur(final Long idTransporteur) {
		this.idTransporteur = idTransporteur;
	}

	public String getComment() {
		return line.getComment();
	}

	public Date getCreationTime() {
		return line.getCreationTime();
	}

	public String getCreatorId() {
		return line.getCreatorId();
	}

	public String getName() {
		return line.getName();
	}

	public String getNumber() {
		return line.getNumber();
	}

	public String getObjectId() {
		return line.getObjectId();
	}

	public int getObjectVersion() {
            setObjectVersion((int)line.getObjectVersion());
		return (int)line.getObjectVersion();
	}

	public String getPublishedName() {
		return line.getPublishedName();
	}

	public TransportModeNameType getTransportModeName() {
		return line.getTransportModeName();
	}

	public String getRegistrationNumber() {
		return line.getRegistration().getRegistrationNumber();
	}

	public void setRegistrationNumber(String registrationNumber) {
		line.getRegistration().setRegistrationNumber(registrationNumber);
	}

	public void setComment(String comment) {
		line.setComment(comment);
	}

	public void setCreationTime(Date creationTime) {
		line.setCreationTime(creationTime);
	}

	public void setCreatorId(String creatorId) {
		line.setCreatorId(creatorId);
	}

	public void setName(String name) {
		line.setName(name);
	}

	public void setNumber(String number) {
		line.setNumber(number);
	}

	public void setObjectId(String objectId) {
		line.setObjectId(objectId);
	}

	public void setObjectVersion(int objectVersion) {
            if (objectVersion >= 1)
		line.setObjectVersion(objectVersion);
            else
                line.setObjectVersion(1);
	}

	public void setPublishedName(String publishedName) {
		line.setPublishedName(publishedName);
	}

	public void setTransportModeName(TransportModeNameType transportModeName) {
		line.setTransportModeName(transportModeName);
	}
	/*
	 * Return a string value containing the concatenation of the line name and the registration number
	 */
	public String getFullName() {
		String res = "";
		if (line.getRegistration() != null && line.getRegistration().getRegistrationNumber() != null)
			res = line.getName() + " " + line.getRegistration().getRegistrationNumber();		
		return res ;
	} 

}
