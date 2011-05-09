package fr.certu.chouette.modele;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import chouette.schema.StopPoint;

public class ArretItineraire extends BaseObjet
{
	private StopPoint stopPoint;
	private Long idItineraire;
	private Long idPhysique;
	private int position;
	private boolean isModifie = false;

	public ArretItineraire() {
		super();
		
		stopPoint = new StopPoint();
	}
	
	public ArretItineraire(ArretItineraire arret) {
//		this.position = arret.getPosition();
//		this.isModifie = arret.isModifie;
	
//		idPhysique = Long.valueOf(arret.getIdPhysique());
//		idItineraire = Long.valueOf(arret.getIdItineraire());
		stopPoint = new StopPoint();
		
		try { PropertyUtils.copyProperties(this, arret); }
		catch( Exception e) {
			throw new RuntimeException( e);
		}
		
//		StringWriter chaine = new StringWriter();
//		Marshaller marshaller = new Marshaller( Writer);
//		
//		Unmarshaller unmarshaller = new Unmarshaller();
//		marshaller.
	}	
	
	public static List<ArretItineraire> dupliquer(List<ArretItineraire> arrets)
	{
		if(arrets == null) return null;
		
		List<ArretItineraire> arretsDupliques = new ArrayList<ArretItineraire>(arrets.size());
		
		for (ArretItineraire arret : arrets)
		{
			arretsDupliques.add(new ArretItineraire(arret));
		}
		
		return arretsDupliques;	
	}

	public StopPoint getStopPoint() {
		return stopPoint;
	}

	public void setStopPoint( final StopPoint stopPoint) {
		this.stopPoint = stopPoint;
	}

	public Long getIdItineraire() {
		return idItineraire;
	}

	public void setIdItineraire( final Long idItineraire) {
		this.idItineraire = idItineraire;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition( final int position) {
		this.position = position;
	}

	public Long getIdPhysique() {
		return idPhysique;
	}

	public void setIdPhysique( final Long idPhysique) {
		this.idPhysique = idPhysique;
	}

	public boolean isModifie() {
		return isModifie;
	}

	public void setModifie( final boolean isModifie) {
		this.isModifie = isModifie;
	}

	public Date getCreationTime() {
		return stopPoint.getCreationTime();
	}

	public String getCreatorId() {
		return stopPoint.getCreatorId();
	}

	public String getName() {
		return stopPoint.getName();
	}

	public String getObjectId() {
		return stopPoint.getObjectId();
	}

	public int getObjectVersion() {
            setObjectVersion((int)stopPoint.getObjectVersion());
            return (int)stopPoint.getObjectVersion();
	}

	public void setCreationTime(Date creationTime) {
		stopPoint.setCreationTime(creationTime);
	}

	public void setCreatorId(String creatorId) {
		stopPoint.setCreatorId(creatorId);
	}

	public void setName(String name) {
		stopPoint.setName(name);
	}

	public void setObjectId(String objectId) {
		stopPoint.setObjectId(objectId);
	}

	public void setObjectVersion(int objectVersion) {
            if (objectVersion >= 1)
		stopPoint.setObjectVersion(objectVersion);
            else
                stopPoint.setObjectVersion(1);
	}

	public String getContainedIn() {
		return stopPoint.getContainedIn();
	}

	public void setContainedIn(String containedIn) {
		stopPoint.setContainedIn(containedIn);
	}
	
	
}
