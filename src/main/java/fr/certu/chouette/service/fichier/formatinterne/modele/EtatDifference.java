package fr.certu.chouette.service.fichier.formatinterne.modele;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EtatDifference implements IEtatDifference {
	
	private Long              exLigne                       = null;
	private Long              exReseau                      = null;
	private Long              exTransporteur                = null;
	private Map<String, Long> exCorrespondanceIdParObjectId = new Hashtable<String, Long>();
	private Map<String, Long> exZoneGeneriqueIdParObjectId  = new Hashtable<String, Long>();
	private Map<String, Long> exTMIdParObjectId             = new Hashtable<String, Long>();
	private Map<String, Long> exItineraireIdParObjectId     = new Hashtable<String, Long>();
	private Map<String, Long> exMissionIdParObjectId        = new Hashtable<String, Long>();
	private Map<String, Long> exCourseIdParObjectId         = new Hashtable<String, Long>();
	private Map<String, Long> exArretIdParObjectId          = new Hashtable<String, Long>();
	private List<String>      nvObjectIdTM                  = new ArrayList<String>();
	private List<String>      nvObjectIdZoneGenerique       = new ArrayList<String>();
	private List<String>      nvObjectIdCorrespondance      = new ArrayList<String>();
	private List<String>      nvObjectIdItineraire          = new ArrayList<String>();
	private List<String>      nvObjectIdMission             = new ArrayList<String>();
	private List<String>      nvObjectIdCourse              = new ArrayList<String>();
	private List<String>      nvObjectIdArret              = new ArrayList<String>();
	
	public void setExLigne(final Long exLigne) {
		this.exLigne = exLigne;
	}
	
	public void setExReseau(final Long exReseau) {
		this.exReseau = exReseau;
	}
	
	public void setExTMIdParObjectId(final Map<String, Long> exTMIdParObjectId) {
		this.exTMIdParObjectId = exTMIdParObjectId;
	}
	
	public void setExTransporteur(final Long exTransporteur) {
		this.exTransporteur = exTransporteur;
	}
	
	public void setExZoneGeneriqueIdParObjectId(final Map<String, Long> exZoneGeneriqueIdParObjectId) {
		this.exZoneGeneriqueIdParObjectId = exZoneGeneriqueIdParObjectId;
	}
	
	public void setExCorrespondanceIdParObjectId(Map<String, Long> exCorrespondanceIdParObjectId) {
		this.exCorrespondanceIdParObjectId = exCorrespondanceIdParObjectId;
	}
	
	public void setExItineraireIdParObjectId(Map<String, Long> exItineraireIdParObjectId) {
		this.exItineraireIdParObjectId = exItineraireIdParObjectId;
	}
	
	public void setExArretIdParObjectId(Map<String, Long> exArretIdParObjectId) {
		this.exArretIdParObjectId = exArretIdParObjectId;
	}
	
	public void setExMissionIdParObjectId(Map<String, Long> exMissionIdParObjectId) {
		this.exMissionIdParObjectId = exMissionIdParObjectId;
	}
	
	public void setExCourseIdParObjectId(Map<String, Long> exCourseIdParObjectId) {
		this.exCourseIdParObjectId = exCourseIdParObjectId;
	}
	
	public void setNvObjectIdTM(final List<String> nvObjectIdTM) {
		this.nvObjectIdTM = nvObjectIdTM;
	}
	
	public void setNvObjectIdZoneGenerique(final List<String> nvObjectIdZoneGenerique) {
		this.nvObjectIdZoneGenerique = nvObjectIdZoneGenerique;
	}
	
	public void setNvObjectIdCorrespondance(List<String> nvObjectIdCorrespondance) {
		this.nvObjectIdCorrespondance = nvObjectIdCorrespondance;
	}
	
	public void setNvObjectIdItineraire(List<String> nvObjectIdItineraire) {
		this.nvObjectIdItineraire = nvObjectIdItineraire;
	}
	
	public void setNvObjectIdArret(List<String> nvObjectIdArret) {
		this.nvObjectIdArret = nvObjectIdArret;
	}
	
	public void setNvObjectIdMission(List<String> nvObjectIdMission) {
		this.nvObjectIdMission = nvObjectIdMission;
	}
	
	public void setNvObjectIdCourse(List<String> nvObjectIdCourse) {
		this.nvObjectIdCourse = nvObjectIdCourse;
	}
	
	public Long getIdLigneConnue() {
		return exLigne;
	}
	
	public boolean isLigneConnue() {
		return exLigne!=null;
	}
	
	public Long getIdReseauConnu() {
		return exReseau;
	}
	
	public boolean isReseauConnu() {
		return exReseau!=null;
	}
	
	public Long getIdZoneGeneriqueConnue( final String objectId) {
		return exZoneGeneriqueIdParObjectId.get( objectId);
	}
	
	public Long getIdCorrespondanceConnue( final String objectId) {
		return exCorrespondanceIdParObjectId.get( objectId);
	}
	
	public Long getIdTableauMarcheConnu( final String objectId) {
		return exTMIdParObjectId.get( objectId);
	}
	
	public Long getIdItineraireConnu(final String objectId) {
		return exItineraireIdParObjectId.get(objectId);
	}
	
	public Long getIdArretConnu(final String objectId) {
		return exArretIdParObjectId.get(objectId);
	}
	
	public Long getIdMissionConnue(final String objectId) {
		return exMissionIdParObjectId.get(objectId);
	}
	
	public Long getIdCourseConnue(final String objectId) {
		return exCourseIdParObjectId.get(objectId);
	}
	
	public boolean isObjectIdTableauMarcheConnu( final String objectId) {
		return exTMIdParObjectId.containsKey( objectId);
	}
	
	public boolean isObjectIdZoneGeneriqueConnue( final String objectId) {
		return exZoneGeneriqueIdParObjectId.containsKey( objectId);
	}
	
	public boolean isObjectIdCorrespondanceConnue( final String objectId) {
		return exCorrespondanceIdParObjectId.containsKey( objectId);
	}
	
	public boolean isObjectIdItineraireConnu(final String objectId) {
		return exItineraireIdParObjectId.containsKey(objectId);
	}
	
	public boolean isObjectIdArretConnu(final String objectId) {
		return exArretIdParObjectId.containsKey(objectId);
	}
	
	public boolean isObjectIdMissionConnue(final String objectId) {
		return exMissionIdParObjectId.containsKey(objectId);
	}
	
	public boolean isObjectIdCourseConnue(final String objectId) {
		return exCourseIdParObjectId.containsKey(objectId);
	}
	
	public Set<String> getTousObjectIdTMConnus() {
		return exTMIdParObjectId.keySet();
	}
	
	public List<String> getTousObjectIdTMInconnus() {
		return nvObjectIdTM;
	}
	
	public boolean containsTMInconnu() {
		return nvObjectIdTM!=null && !nvObjectIdTM.isEmpty();
	}	
	
	public boolean containsCorrespondanceInconnue() {
		return nvObjectIdCorrespondance!=null && !nvObjectIdCorrespondance.isEmpty();
	}
	
	public boolean containsItineraireInconnu() {
		return nvObjectIdItineraire!=null && !nvObjectIdItineraire.isEmpty();
	}
	
	public boolean containsArretInconnu() {
		return nvObjectIdArret!=null && !nvObjectIdArret.isEmpty();
	}
	
	public boolean containsMissionInconnue() {
		return nvObjectIdMission!=null && !nvObjectIdMission.isEmpty();
	}
	
	public boolean containsCourseInconnue() {
		return nvObjectIdCourse!=null && !nvObjectIdCourse.isEmpty();
	}
	
	public boolean containsZoneGeneriqueInconnue() {
		return nvObjectIdZoneGenerique!=null && !nvObjectIdZoneGenerique.isEmpty();
	}
	
	public List<String> getObjectIdToutesZonesGeneriquesConnues() {
		return nvObjectIdZoneGenerique;
	}
	
	public Long getIdTransporteurConnu() {
		return exTransporteur;
	}
	
	public boolean isTransporteurConnu() {
		return exTransporteur!=null;
	}
}
