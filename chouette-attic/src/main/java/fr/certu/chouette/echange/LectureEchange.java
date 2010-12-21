package fr.certu.chouette.echange;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.InterdictionTraficLocal;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

public class LectureEchange implements ILectureEchange
{
    private static final Logger logger = Logger.getLogger( LectureEchange.class);
    
	private Transporteur transporteur;
	private Reseau reseau;
	private Ligne ligne;
	private List<Itineraire> itineraires;
	private List<TableauMarche> tableauxMarche;
	private List<ArretItineraire> arrets;
	private List<PositionGeographique> arretsPhysiques;
	private List<Course> courses;
	private List<Horaire> horaires;
	private List<PositionGeographique> zonesCommerciales = new ArrayList<PositionGeographique>();
	private List<PositionGeographique> zonesPlaces = new ArrayList<PositionGeographique>();
	private List<Correspondance> correspondances = new ArrayList<Correspondance>();
	private List<Mission> missions = new ArrayList<Mission>();
	private List<String> objectIdZonesGeneriques;
	private List<InterdictionTraficLocal> interdictionsTraficLocal = new ArrayList<InterdictionTraficLocal>();
	
	private Map<String, List<String>> physiquesParITLId;
	private Map<String, String> itineraireParArret;
	private Map<String, String> zoneParenteParObjectId = new Hashtable<String, String>();

	@Override
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		
		// chargement de la partie dynamique
		List<PositionGeographique> arretsPhysiques = getArretsPhysiques();
		for (PositionGeographique physique : arretsPhysiques) {
			buffer.append( physique.getName()+" "+physique.getObjectId());
			buffer.append( "\n");
		}
		
		List<Course> courses = getCourses();
		buffer.append( "total courses = "+courses.size());
		buffer.append( "\n");
		for (Course course : courses) {
			//logger.debug( "course.getComment() = "+course.getComment());
		}
		
		List<Itineraire> itineraires = getItineraires();
		buffer.append( "total itineraires = "+itineraires.size());
		buffer.append( "\n");
		for (Itineraire itineraire : itineraires) {
			//logger.debug( itineraire.getName()+" "+itineraire.getNumber()+" "+itineraire.getPublishedName());
		}
		
		List<TableauMarche> tableauxMarches = getTableauxMarche();
		for (TableauMarche marche : tableauxMarches) 
		{
			//logger.debug( marche.getComment()+" "+marche.getObjectId());
		}
		
		
		int totalListeHoraires = horaires.size();
		buffer.append( "total courses (obtenu des horaires) = "+totalListeHoraires);
		buffer.append( "\n");
		for (int i = 0; i < totalListeHoraires; i++) 
		{
			Horaire horairePassage = horaires.get( i);
			buffer.append( horairePassage.getStopPointId()+" "+horairePassage.getArrivalTime()+" "+horairePassage.getDepartureTime());
			buffer.append( "\n");
		}
		
		List<ArretItineraire> arretsLogiques = getArrets();
		for (ArretItineraire arret : arretsLogiques) {
			buffer.append( arret.getPosition()+" "+arret.getObjectId()+" phys="+arret.getContainedIn());
			buffer.append( "\n");
		}
		
		return buffer.toString();
	}
	
	public String getLigneObjectId()
	{
		return getLigne().getObjectId();
	}
	
	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}
	public void setArrets(List<ArretItineraire> arrets) {
		this.arrets = arrets;
	}

	public void setItineraireParArret(Map<String, String> itineraireParArret) {
		this.itineraireParArret = itineraireParArret;
	}

	public void setItineraires(List<Itineraire> itineraires) {
		this.itineraires = itineraires;
	}

	public void setLigne(Ligne ligne) {
		this.ligne = ligne;
	}

	public void setReseau(Reseau reseau) {
		this.reseau = reseau;
	}

	public void setTableauxMarche(List<TableauMarche> tableauxMarche) {
		this.tableauxMarche = tableauxMarche;
	}

	public void setTransporteur(Transporteur transporteur) {
		this.transporteur = transporteur;
	}

	
	public List<Mission> getMissions() {
		return missions;
	}

	public void setMissions(List<Mission> missions) {
		this.missions = missions;
	}

	public String getItineraireArret( String arretObjectId)
	{
		if ( !itineraireParArret.containsKey( arretObjectId))
		{
			throw new ServiceException( CodeIncident.IDENTIFIANT_INCONNU, CodeDetailIncident.DEFAULT,arretObjectId);
		}
		return itineraireParArret.get( arretObjectId);
	}
	
	public String getZoneParente( String zoneObjectId)
	{
		//logger.debug( zoneParenteParObjectId);
		return zoneParenteParObjectId.get( zoneObjectId);
	}
	
	public List<Course> getCourses() {
		return courses;
	}
	public List<ArretItineraire> getArrets()
	{
		return arrets;
	}
	
	
	
	public List<String> getObjectIdZonesGeneriques() {
		return objectIdZonesGeneriques;
	}
	public String getLigneRegistration()
	{
		return ligne.getRegistrationNumber();
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.xml.ILectureEchange#getTransporteur()
	 */
	public Transporteur getTransporteur()
	{
		return transporteur;
	}
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.xml.ILectureEchange#getReseau()
	 */
	public Reseau getReseau()
	{
		return reseau;
	}
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.xml.ILectureEchange#getLigne()
	 */
	public Ligne getLigne()
	{
		return ligne;
	}
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.xml.ILectureEchange#getItineraires()
	 */
	public List<Itineraire> getItineraires()
	{
		return itineraires;
	}

	public List<TableauMarche> getTableauxMarche() {
		return tableauxMarche;
	}
	public List<Horaire> getHoraires() {
		return horaires;
	}
	public void setHoraires(List<Horaire> horaires) {
		this.horaires = horaires;
	}
	public List<PositionGeographique> getArretsPhysiques() {
		return arretsPhysiques;
	}
	public void setArretsPhysiques(List<PositionGeographique> arretsPhysiques) {
		this.arretsPhysiques = arretsPhysiques;
	}
	public void setObjectIdZonesGeneriques(List<String> objectIdZonesGeneriques) {
		this.objectIdZonesGeneriques = objectIdZonesGeneriques;
	}

	public List<PositionGeographique> getZonesCommerciales() {
		return zonesCommerciales;
	}

	public void setZonesCommerciales(List<PositionGeographique> zonesCommerciales) {
		this.zonesCommerciales = zonesCommerciales;
	}
	
	public List<PositionGeographique> getPositionsGeographiques()
	{
		List<PositionGeographique> positions = new ArrayList<PositionGeographique>();
		positions.addAll( arretsPhysiques);
		positions.addAll( zonesCommerciales);
		positions.addAll( zonesPlaces);
		
		return positions;
	}

	public List<PositionGeographique> getZonesPlaces() {
		return zonesPlaces;
	}

	public void setZonesPlaces(List<PositionGeographique> zonesPlaces) {
		this.zonesPlaces = zonesPlaces;
	}

	public void setZoneParenteParObjectId(Map<String, String> zoneParenteParObjectId) {
		this.zoneParenteParObjectId = zoneParenteParObjectId;
	}

	public List<Correspondance> getCorrespondances() {
		return correspondances;
	}

	public void setCorrespondances(List<Correspondance> correspondances) {
		this.correspondances = correspondances;
	}

	public void setInterdictionTraficLocal(List<InterdictionTraficLocal> interdictionsTraficLocal) {
		this.interdictionsTraficLocal = interdictionsTraficLocal;
	}
	
	public List<InterdictionTraficLocal> getInterdictionTraficLocal() {
		return interdictionsTraficLocal;
	}

	public List<String> getPhysiqueObjectIds( String itl) {
		return physiquesParITLId.get( itl);
	}

	public void setPhysiquesParITLId(Map<String, List<String>> physiquesParITLId) {
		this.physiquesParITLId = physiquesParITLId;
	}
	
	// AJOUT ZAKARIA
	public Map<String, String> getZoneParenteParObjectId() {
		return zoneParenteParObjectId;
	}
}
