package fr.certu.chouette.service.importateur.monoligne.csv;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouetteRoute;
import chouette.schema.PtLink;
import chouette.schema.StopPoint;
import chouette.schema.VehicleJourney;
import chouette.schema.types.ChouetteAreaType;
import chouette.schema.types.LongLatTypeType;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class LecteurArretPhysiqueCSV {
	
	private static final Logger        logger = Logger.getLogger(LecteurArretPhysiqueCSV.class);
	
	private int                        indiceCodePostal;              // 0
	private int                        indiceCommentaire;             // 1
	private int                        indiceX;                       // 2
	private int                        indiceY;                       // 3
	private int                        latitude;                      // 4
	private int                        longitude;                     // 5
	private int                        indiceNom;                     // 7
	private IIdentificationManager     identificationManager;
	
	private List<PositionGeographique> arretsPhysiques;
	private int                        positionArret;
	
	public LecteurArretPhysiqueCSV() {
		super();
		arretsPhysiques = new ArrayList<PositionGeographique>();
	}
	
	public void initialiser() {
		arretsPhysiques.clear();
		positionArret = 0;
	}
	
	public void ajouter(String titre, List<String> contenu) {
		assert contenu.size()>=getMaxIndice(): "total mini de cellules attendu "+getMaxIndice()+", total obtenu "+contenu.size();
		arretsPhysiques.add(lireArretPhysique(contenu, positionArret));
		positionArret++;
	}
	
	public List<PositionGeographique> lire() {
		return arretsPhysiques;
	}
	
	private PositionGeographique lireArretPhysique(List<String> contenuArret, int position) {
		PositionGeographique arretPhysique = new PositionGeographique();
		if (contenuArret.get(indiceNom).isEmpty())
			throw new ServiceException( CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.BOARDINGPOSITION_NAME);
		arretPhysique.setName(contenuArret.get(indiceNom));
		arretPhysique.setAreaType(ChouetteAreaType.BOARDINGPOSITION);
		arretPhysique.setObjectId(identificationManager.getIdFonctionnel("StopArea", String.valueOf( position)));
		arretPhysique.setObjectVersion(1);
		if (!contenuArret.get(indiceCodePostal).isEmpty()) 
			arretPhysique.setCountryCode(contenuArret.get(indiceCodePostal));
		if (!contenuArret.get(indiceCommentaire).isEmpty()) 
			arretPhysique.setComment(contenuArret.get(indiceCommentaire));
		if (!contenuArret.get(indiceX).isEmpty())
			arretPhysique.setX(lireBigDecimal(contenuArret.get(indiceX)));
		if (!contenuArret.get(indiceY).isEmpty()) 
			arretPhysique.setY(lireBigDecimal(contenuArret.get(indiceY)));
		if (!contenuArret.get(latitude).isEmpty()) 
			arretPhysique.setLatitude(lireBigDecimal(contenuArret.get(latitude)));
		if (!contenuArret.get(longitude).isEmpty()) 
			arretPhysique.setLongitude(lireBigDecimal(contenuArret.get(longitude)));
		if (!contenuArret.get(latitude).isEmpty() || !contenuArret.get(longitude).isEmpty())
			arretPhysique.setLongLatType(LongLatTypeType.WGS84);
		arretPhysique.setCreationTime(new Date());
		return arretPhysique;
	}
	
	private BigDecimal lireBigDecimal(String valeur) {
		Float floatVal = new Float(valeur);
		return new BigDecimal(floatVal.toString());
	}
	
	private int getMaxIndice() {
		int maxIndice = 0;
		maxIndice = Math.max( maxIndice, indiceCodePostal);
		maxIndice = Math.max( maxIndice, indiceCommentaire);
		maxIndice = Math.max( maxIndice, indiceX);
		maxIndice = Math.max( maxIndice, indiceY);
		maxIndice = Math.max( maxIndice, indiceNom);
		return maxIndice;
	}
	
	public void setIndiceCodePostal(int indiceCodePostal) {
		this.indiceCodePostal = indiceCodePostal;
	}
	
	public int getIndiceCodePostal() {
		return indiceCodePostal;
	}
	
	public void setIndiceCommentaire(int indiceCommentaire) {
		this.indiceCommentaire = indiceCommentaire;
	}
	
	public int getIndiceCommentaire() {
		return indiceCommentaire;
	}
	
	public void setIndiceX(int indiceX) {
		this.indiceX = indiceX;
	}
	
	public int getIndiceX() {
		return indiceX;
	}
	
	public void setIndiceY(int indiceY) {
		this.indiceY = indiceY;
	}
	
	public int getIndiceY() {
		return indiceY;
	}
	
	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}
	
	public int getLatitude() {
		return latitude;
	}
	
	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}
	
	public int getLongitude() {
		return longitude;
	}
	
	public void setIndiceNom(int indiceNom) {
		this.indiceNom = indiceNom;
	}
	
	public int getIndiceNom() {
		return indiceNom;
	}
	
	public void setIdentificationManager(IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
	
	public IIdentificationManager getIdentificationManager() {
		return identificationManager;
	}
	
	public String[] ecrire(int length, int colonneTitrePartieFixe) {
		String[] donneesArret = new String[length];
		donneesArret[0] = "Code Postal";
		donneesArret[1] = "Commentaire";
		donneesArret[2] = "X";
		donneesArret[3] = "Y";
		donneesArret[4] = "Latitude";
		donneesArret[5] = "Longitude";
		donneesArret[6] = "Distance";
		donneesArret[colonneTitrePartieFixe] = "Liste des arrets";
		for (int i = colonneTitrePartieFixe+1; i < length; i++)
			if (((i-(colonneTitrePartieFixe+1)) % 2) == 0)
				donneesArret[i] = "Heure arrivee";
			else
				donneesArret[i] = "Heure depart";
		return donneesArret;
	}
	
	public Collection<List<String[]>> ecrire(ChouetteLineDescription chouetteLineDescription, int length, int colonneTitrePartieFixe) {
		Collection<List<String[]>> values = null;
		Map<ChouetteRoute, List<String[]>> listsByRoute = new HashMap<ChouetteRoute, List<String[]>>();
		List<String[]> donneesArrets = null;
		for (int i = 0; i < chouetteLineDescription.getVehicleJourneyCount(); i++) {
			VehicleJourney vehicleJourney = chouetteLineDescription.getVehicleJourney(i);
			ChouetteRoute chouetteRoute = null;
			for (int j = 0; j < chouetteLineDescription.getChouetteRouteCount(); j++)
				if (vehicleJourney.getRouteId().equals(chouetteLineDescription.getChouetteRoute(j).getObjectId())) {
					chouetteRoute = chouetteLineDescription.getChouetteRoute(j);
					break;
				}
			if (chouetteRoute == null)
				continue;
			if (listsByRoute.get(chouetteRoute) == null) {
				donneesArrets = new ArrayList<String[]>();
				for (int j = 0; j < chouetteRoute.getPtLinkIdCount(); j++) {
					PtLink ptLink = null;
					for (int k = 0; k < chouetteLineDescription.getPtLinkCount(); k++)
						if (chouetteRoute.getPtLinkId(j).equals(chouetteLineDescription.getPtLink(k).getObjectId())) {
							ptLink = chouetteLineDescription.getPtLink(k);
							break;
						}
					if (ptLink == null)
						continue;
					if (j == 0) {
						StopPoint stopPoint2 = null;
						for (int k = 0; k < chouetteLineDescription.getStopPointCount(); k++)
							if (ptLink.getStartOfLink().equals(chouetteLineDescription.getStopPoint(k).getObjectId())) {
								stopPoint2 = chouetteLineDescription.getStopPoint(k);
								break;
							}
						if (stopPoint2 != null) {
							String[] donneesArret2 = new String[length];
							if (stopPoint2.getAddress() != null)
								if (stopPoint2.getAddress().getCountryCode() != null)
									donneesArret2[0] =  stopPoint2.getAddress().getCountryCode();
							if (stopPoint2.getComment() != null)
								donneesArret2[1] = stopPoint2.getComment();
							if (stopPoint2.getProjectedPoint() != null) {
								if (stopPoint2.getProjectedPoint().getX() != null)
									donneesArret2[2] = stopPoint2.getProjectedPoint().getX().toString();
								if (stopPoint2.getProjectedPoint().getY() != null)
									donneesArret2[3] = stopPoint2.getProjectedPoint().getY().toString();
							}
							if (stopPoint2.getLatitude() != null)
								donneesArret2[4] = stopPoint2.getLatitude().toString();
							if (stopPoint2.getLongitude() != null)
								donneesArret2[5] = stopPoint2.getLongitude().toString();
							donneesArret2[colonneTitrePartieFixe] = stopPoint2.getName();
							donneesArrets.add(donneesArret2);
						}
					}
					StopPoint stopPoint = null;
					for (int k = 0; k < chouetteLineDescription.getStopPointCount(); k++)
						if (ptLink.getEndOfLink().equals(chouetteLineDescription.getStopPoint(k).getObjectId())) {
							stopPoint = chouetteLineDescription.getStopPoint(k);
							break;
						}
					if (stopPoint != null) {
						String[] donneesArret2 = null;
						for (int k = 0; k < donneesArrets.size(); k++)
							if (donneesArrets.get(k)[colonneTitrePartieFixe].equals(stopPoint.getName()))
								donneesArret2 = donneesArrets.get(k);
						if (donneesArret2 == null) {
							donneesArret2 = new String[length];
							if (stopPoint.getAddress() != null)
								if (stopPoint.getAddress().getCountryCode() != null)
									donneesArret2[0] =  stopPoint.getAddress().getCountryCode();
							if (stopPoint.getComment() != null)
								donneesArret2[1] = stopPoint.getComment();
							if (stopPoint.getProjectedPoint() != null) {
								if (stopPoint.getProjectedPoint().getX() != null)
									donneesArret2[2] = stopPoint.getProjectedPoint().getX().toString();
								if (stopPoint.getProjectedPoint().getY() != null)
									donneesArret2[3] = stopPoint.getProjectedPoint().getY().toString();
							}
							if (stopPoint.getLatitude() != null)
								donneesArret2[4] = stopPoint.getLatitude().toString();
							if (stopPoint.getLongitude() != null)
								donneesArret2[5] = stopPoint.getLongitude().toString();
							donneesArret2[colonneTitrePartieFixe] = stopPoint.getName();
							donneesArrets.add(donneesArret2);
						}
					}
				}
				listsByRoute.put(chouetteRoute, donneesArrets);
			}
			else
				donneesArrets = listsByRoute.get(chouetteRoute);
			
			for (int j = 0; j < chouetteRoute.getPtLinkIdCount(); j++) {
				PtLink ptLink = null;
				for (int k = 0; k < chouetteLineDescription.getPtLinkCount(); k++)
					if (chouetteRoute.getPtLinkId(j).equals(chouetteLineDescription.getPtLink(k).getObjectId())) {
						ptLink = chouetteLineDescription.getPtLink(k);
						break;
					}
				if (ptLink == null)
					continue;
				if (j == 0) {
					StopPoint stopPoint = null;
					for (int k = 0; k < chouetteLineDescription.getStopPointCount(); k++)
						if (ptLink.getStartOfLink().equals(chouetteLineDescription.getStopPoint(k).getObjectId())) {
							stopPoint = chouetteLineDescription.getStopPoint(k);
							break;
						}
					if (stopPoint != null) {
						String[] donneesArret2 = null;
						for (int k = 0; k < donneesArrets.size(); k++)
							if (donneesArrets.get(k)[colonneTitrePartieFixe].equals(stopPoint.getName()))
								donneesArret2 = donneesArrets.get(k);
						for (int k = 0; k < vehicleJourney.getVehicleJourneyAtStopCount(); k++)
							if (stopPoint.getObjectId().equals(vehicleJourney.getVehicleJourneyAtStop(k).getStopPointId())) {
								if (vehicleJourney.getVehicleJourneyAtStop(k).getVehicleJourneyAtStopTypeChoice() != null)
									if (vehicleJourney.getVehicleJourneyAtStop(k).getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence() != null) {
										if (vehicleJourney.getVehicleJourneyAtStop(k).getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getArrivalTime() != null) {
											donneesArret2[colonneTitrePartieFixe+1+2*i] = vehicleJourney.getVehicleJourneyAtStop(k).getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getArrivalTime().toString();
											donneesArret2[colonneTitrePartieFixe+2+2*i] = vehicleJourney.getVehicleJourneyAtStop(k).getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getArrivalTime().toString();
										}
										if (vehicleJourney.getVehicleJourneyAtStop(k).getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getDepartureTime() != null) {
											donneesArret2[colonneTitrePartieFixe+2+2*i] = vehicleJourney.getVehicleJourneyAtStop(k).getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getDepartureTime().toString();
											if (donneesArret2[colonneTitrePartieFixe+1+2*i] == null)
												donneesArret2[colonneTitrePartieFixe+1+2*i] = donneesArret2[colonneTitrePartieFixe+2+2*i];
										}
									}
							}
					}
				}
				StopPoint stopPoint2 = null;
				for (int k = 0; k < chouetteLineDescription.getStopPointCount(); k++)
					if (ptLink.getEndOfLink().equals(chouetteLineDescription.getStopPoint(k).getObjectId())) {
						stopPoint2 = chouetteLineDescription.getStopPoint(k);
						break;
					}
				if (stopPoint2 == null)
					continue;
				String[] donneesArret3 = null;
				for (int k = 0; k < donneesArrets.size(); k++)
					if (donneesArrets.get(k)[colonneTitrePartieFixe].equals(stopPoint2.getName()))
						donneesArret3 = donneesArrets.get(k);
				for (int k = 0; k < vehicleJourney.getVehicleJourneyAtStopCount(); k++)
					if (stopPoint2.getObjectId().equals(vehicleJourney.getVehicleJourneyAtStop(k).getStopPointId())) {
						if (vehicleJourney.getVehicleJourneyAtStop(k).getVehicleJourneyAtStopTypeChoice() != null)
							if (vehicleJourney.getVehicleJourneyAtStop(k).getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence() != null) {
								if (vehicleJourney.getVehicleJourneyAtStop(k).getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getArrivalTime() != null) {
									donneesArret3[colonneTitrePartieFixe+1+2*i] = vehicleJourney.getVehicleJourneyAtStop(k).getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getArrivalTime().toString();
									donneesArret3[colonneTitrePartieFixe+2+2*i] = vehicleJourney.getVehicleJourneyAtStop(k).getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getArrivalTime().toString();
								}
								if (vehicleJourney.getVehicleJourneyAtStop(k).getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getDepartureTime() != null) {
									donneesArret3[colonneTitrePartieFixe+2+2*i] = vehicleJourney.getVehicleJourneyAtStop(k).getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getDepartureTime().toString();
									if (donneesArret3[colonneTitrePartieFixe+1+2*i] == null)
										donneesArret3[colonneTitrePartieFixe+1+2*i] = donneesArret3[colonneTitrePartieFixe+2+2*i];
								}
							}
					}
			}
		}
		values = listsByRoute.values();
		return values;
	}
}
