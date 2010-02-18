package fr.certu.chouette.echange;

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
import java.util.List;
import java.util.Map;

public interface ILectureEchange extends IIdentifiantLigneEchange {
	
	public String getLigneRegistration();
	public Transporteur getTransporteur();
	public Reseau getReseau();
	public Ligne getLigne();
	public List<Itineraire> getItineraires();
	public List<TableauMarche> getTableauxMarche();
	public List<ArretItineraire> getArrets();
	public List<PositionGeographique> getArretsPhysiques();
	public String getItineraireArret(String arretObjectId);
	public List<Course> getCourses();
	public List<Horaire> getHoraires();
	public List<String> getObjectIdZonesGeneriques();
	public List<PositionGeographique> getZonesCommerciales();
	public List<PositionGeographique> getZonesPlaces();
	public String getZoneParente(String zoneObjectId);
	public List<PositionGeographique> getPositionsGeographiques();
	public List<Correspondance> getCorrespondances();
	public List<Mission> getMissions();
	public void setMissions(List<Mission> missions);
	public void setInterdictionTraficLocal(List<InterdictionTraficLocal> interdictionsTraficLocal);
	public List<InterdictionTraficLocal> getInterdictionTraficLocal();
	public List<String> getPhysiqueObjectIds( String itl);
	// AJOUT ZAKARIA
	public Map<String, String> getZoneParenteParObjectId();
	public void setZoneParenteParObjectId(Map<String, String> zoneParenteParObjectId);
	public void setHoraires(List<Horaire> horairesDeLigne);
	public void setZonesCommerciales(List<PositionGeographique> positionsGeographiques);
	public void setObjectIdZonesGeneriques(List<String> tmpObjectIdZonesGeneriques);
	public void setTableauxMarche(List<TableauMarche> tableauxMarche);
}
