package fr.certu.chouette.service.importateur.multilignes.hastus;

import fr.certu.chouette.modele.PositionGeographique;
import java.util.Map;

public interface ILecteurArret extends ILecteurSpecifique {
	
	public void setZones(Map<String, PositionGeographique> zones);
	public Map<String, PositionGeographique> getArretsPhysiques();
	public Map<String, PositionGeographique> getArretsPhysiquesParObjectId();
	public Map<String, String> getObjectIdParParentObjectId();
}
