package fr.certu.chouette.service.fichier.formatinterne.modele;

import java.util.List;
import java.util.Set;

public interface IEtatDifference {
	
	public Long getIdLigneConnue();
	public boolean isLigneConnue();
	public Long getIdReseauConnu();
	public boolean isReseauConnu();
	public Long getIdZoneGeneriqueConnue(final String objectId);
	public boolean isObjectIdZoneGeneriqueConnue(final String objectId);
	public boolean containsZoneGeneriqueInconnue();
	public List<String> getObjectIdToutesZonesGeneriquesConnues();
	public Long getIdTableauMarcheConnu(final String objectId);
	public boolean isObjectIdTableauMarcheConnu(final String objectId);
	public boolean containsTMInconnu();
	public Set<String> getTousObjectIdTMConnus();
	public List<String> getTousObjectIdTMInconnus();
	public Long getIdTransporteurConnu();
	public boolean isTransporteurConnu();
	public Long getIdCorrespondanceConnue(final String objectId);
	public boolean isObjectIdCorrespondanceConnue(final String objectId);
	public boolean containsCorrespondanceInconnue();
	public boolean isObjectIdItineraireConnu(String objectId);
	public Long getIdItineraireConnu(String objectId);
	public boolean isObjectIdArretConnu(String objectId);
	public Long getIdArretConnu(String objectId);
	public boolean isObjectIdMissionConnue(String objectId);
	public Long getIdMissionConnue(String objectId);
	public boolean isObjectIdCourseConnue(String objectId);
	public Long getIdCourseConnue(String objectId);
	public boolean containsItineraireInconnu();
	public boolean containsArretInconnu();
	public boolean containsMissionInconnue();
	public boolean containsCourseInconnue();
}
