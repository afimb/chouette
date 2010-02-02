package fr.certu.chouette.service.importateur.multilignes.hastus;

import fr.certu.chouette.modele.PositionGeographique;
import java.util.Map;

public interface ILecteurZone extends ILecteurSpecifique {
	
	public Map<String, PositionGeographique> getZones();
	public Map<String, PositionGeographique> getZonesParObjectId();
}
