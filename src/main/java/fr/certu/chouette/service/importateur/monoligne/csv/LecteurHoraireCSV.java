package fr.certu.chouette.service.importateur.monoligne.csv;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class LecteurHoraireCSV {
	
	private static final Logger           logger = Logger.getLogger(LecteurHoraireCSV.class);
	
	private IIdentificationManager        identificationManager;
	
	private static final SimpleDateFormat sdfCourt = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat sdfLong = new SimpleDateFormat("HH:mm:ss");
	private List<List<Horaire>>           listeHoraires;
	private int                           positionArret;
	private int                           total;
	
	public LecteurHoraireCSV() {
		super();
		listeHoraires = new ArrayList<List<Horaire>>();
	}
	
	public void initialiser(int total) {
		listeHoraires.clear();
		positionArret = 0;
		// placer les listes dans listeHoraires
		this.total = total;
		for (int i = 0; i < total; i++) 
			listeHoraires.add(new ArrayList<Horaire>());
	}
	
	public void ajouter(String titre, List<String> contenu) {
		assert contenu.size()==total*2: "total attendu "+(total*2)+", total obtenu "+contenu.size();
		for (int i = 0; i < total; i++) {
			String chaineArrivee = contenu.get(i*2);
			String chaineDepart = contenu.get(i*2+1);
			if (!chaineArrivee.isEmpty()) {
				Horaire horaire = new Horaire();
				try {
					horaire.setArrivalTime(lireHoraire( chaineArrivee));
					horaire.setDepartureTime(lireHoraire( chaineDepart));
				}
				catch(ParseException e) {
					throw new ServiceException( CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.STOPPOINT_MALFORMEDPASSINGTIME,i,chaineDepart,chaineArrivee);
				}
				listeHoraires.get(i).add( horaire);
				horaire.setStopPointId(identificationManager.getIdFonctionnel("StopArea", String.valueOf( positionArret)));
				horaire.setVehicleJourneyId(identificationManager.getIdFonctionnel("VehicleJourney", String.valueOf( i*2)));
			}
		}
		//arretsPhysiques.add( lireArretPhysique(contenu, positionArret));
		positionArret++;
	}
	
	public List<List<Horaire>>  lire() {
		return listeHoraires;
	}
	
	private Date lireHoraire( String chaineHoraire) throws ParseException {
		if (chaineHoraire == null)
			return null;
		return sdfLong.parse( chaineHoraire);
	}
	
	public void setIdentificationManager(IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
	
	public IIdentificationManager getIdentificationManager() {
		return identificationManager;
	}
}
