package fr.certu.chouette.service.fichier.formatinterne.impl;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import fr.certu.chouette.service.fichier.formatinterne.IGestionSequence;
import fr.certu.chouette.service.fichier.formatinterne.IProducteurFichier;
import fr.certu.chouette.service.fichier.formatinterne.impl.producteur.ProducteurArretItineraire;
import fr.certu.chouette.service.fichier.formatinterne.impl.producteur.ProducteurCorrespondance;
import fr.certu.chouette.service.fichier.formatinterne.impl.producteur.ProducteurCourse;
import fr.certu.chouette.service.fichier.formatinterne.impl.producteur.ProducteurHoraire;
import fr.certu.chouette.service.fichier.formatinterne.impl.producteur.ProducteurInterdictionTraficLocal;
import fr.certu.chouette.service.fichier.formatinterne.impl.producteur.ProducteurItineraire;
import fr.certu.chouette.service.fichier.formatinterne.impl.producteur.ProducteurMission;
import fr.certu.chouette.service.fichier.formatinterne.impl.producteur.ProducteurPositonGeographique;
import fr.certu.chouette.service.fichier.formatinterne.impl.producteur.ProducteurLigne;
import fr.certu.chouette.service.fichier.formatinterne.impl.producteur.ProducteurReseau;
import fr.certu.chouette.service.fichier.formatinterne.impl.producteur.ProducteurTableauMarche;
import fr.certu.chouette.service.fichier.formatinterne.impl.producteur.ProducteurTransport;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Map;
import org.apache.log4j.Logger;

public class ProducteurFichier implements IProducteurFichier {
	
    private static final Logger                 logger                            = Logger.getLogger(ProducteurFichier.class);
	private              IGestionFichier        gestionFichier;
	private              IGestionSequence       gestionSequence;
	private              IIdentificationManager identificationManager;
	private              IProducteurSpecifique  producteurTransport;
	private              IProducteurSpecifique  producteurReseau;
	private              IProducteurSpecifique  producteurLigne;
	private              IProducteurSpecifique  producteurItineraire;
	private              IProducteurSpecifique  producteurArretItineraire;
	private              IProducteurSpecifique  producteurPositionGeographique;
	private              IProducteurSpecifique  producteurCorrespondance;
	private              IProducteurSpecifique  producteurCourse;
	private              IProducteurSpecifique  producteurMission;
	private              IProducteurSpecifique  producteurHoraire;
	private              IProducteurSpecifique  producteurTableauMarche;
	private              IProducteurSpecifique  producteurInterdictionTraficLocal;
	private              Map<String, Long>      idParObjectId                     = new Hashtable<String, Long>();
	
	public void initialiser() {
		producteurTransport = new ProducteurTransport(identificationManager, gestionSequence, gestionFichier);
		producteurReseau = new ProducteurReseau(identificationManager, gestionSequence, gestionFichier);
		producteurLigne = new ProducteurLigne(identificationManager, gestionSequence, gestionFichier);
		producteurItineraire = new ProducteurItineraire(identificationManager, gestionSequence, gestionFichier);
		producteurArretItineraire = new ProducteurArretItineraire(identificationManager, gestionSequence, gestionFichier);
		producteurPositionGeographique = new ProducteurPositonGeographique(identificationManager, gestionSequence, gestionFichier);
		producteurCorrespondance = new ProducteurCorrespondance(identificationManager, gestionSequence, gestionFichier);
		producteurCourse = new ProducteurCourse(identificationManager, gestionSequence, gestionFichier);
		producteurMission = new ProducteurMission(identificationManager, gestionSequence, gestionFichier);
		producteurHoraire = new ProducteurHoraire(identificationManager, gestionSequence, gestionFichier);
		producteurTableauMarche =  new ProducteurTableauMarche(identificationManager, gestionSequence, gestionFichier);
		producteurInterdictionTraficLocal = new ProducteurInterdictionTraficLocal(identificationManager, gestionSequence, gestionFichier);
	}
	
	private void produire(boolean majIdentification, ILectureEchange echange, IEtatDifference etatDifference, boolean incremental) {
		Map<String, Long> idTransporteurParObjectId = producteurTransport.produire(majIdentification, echange, etatDifference, null, incremental);
		logger.debug("analyseurEtatInitial.isTransporeurConnu() ? "+etatDifference.isTransporteurConnu());
		logger.debug("analyseurEtatInitial.getIdTransporteurConnu() = "+etatDifference.getIdTransporteurConnu());
		Map<String, Long> idReseauParObjectId = producteurReseau.produire(majIdentification, echange, etatDifference, null, incremental);
		Ligne ligne = echange.getLigne();
		ligne.setIdTransporteur(idTransporteurParObjectId.values().iterator().next());
		ligne.setIdReseau(idReseauParObjectId.values().iterator().next());
		logger.debug("produire ligne: idTrsp="+ligne.getIdTransporteur()+ ", name="+ligne.getName()+ ", objectid="+ligne.getObjectId());
		Map<String, Long> idLigneParObjectId = producteurLigne.produire(majIdentification, echange, etatDifference, null, incremental);
		Map<String, Long> idItineraireParObjectId = producteurItineraire.produire(majIdentification, echange, etatDifference, idLigneParObjectId, incremental);
		Map<String, Long> idZoneGeneriqueParObjectId = producteurPositionGeographique.produire(majIdentification, echange, etatDifference, null, incremental);
		producteurCorrespondance.produire(majIdentification, echange, etatDifference, idZoneGeneriqueParObjectId);
		Map<String, Long> idPhysiqueItineraireParObjectId = new Hashtable<String, Long>(idZoneGeneriqueParObjectId);
		idPhysiqueItineraireParObjectId.putAll(idItineraireParObjectId);
		Map<String, Long> idArretParObjectId = producteurArretItineraire.produire(majIdentification, echange, etatDifference, idPhysiqueItineraireParObjectId, incremental);
		Map<String, Long> idMissionParObjectId = producteurMission.produire(majIdentification, echange, etatDifference, null, incremental);
		Map<String, Long> idMissionItineraireParObjectId = new Hashtable<String, Long>();
		idMissionItineraireParObjectId.putAll(idMissionParObjectId);
		idMissionItineraireParObjectId.putAll(idItineraireParObjectId);
		Map<String, Long> idCourseParObjectId = producteurCourse.produire(majIdentification, echange, etatDifference, idMissionItineraireParObjectId, incremental);
		Map<String, Long> idCourseArretParObjectId = new Hashtable<String, Long>(idCourseParObjectId);
		idCourseArretParObjectId.putAll(idArretParObjectId);
		producteurHoraire.produire(majIdentification, echange, etatDifference, idCourseArretParObjectId, incremental);
		producteurTableauMarche.produire(majIdentification, echange, etatDifference, idCourseParObjectId, incremental);
		producteurInterdictionTraficLocal.produire(majIdentification, echange, etatDifference, idZoneGeneriqueParObjectId, incremental);
		idParObjectId = new Hashtable<String, Long>(idZoneGeneriqueParObjectId);
	}
	
	public void produire(boolean majIdentification, ILectureEchange echange, IEtatDifference etatDifference, Connection connexion) {
		produire(majIdentification, echange, etatDifference, connexion, false);
	}
	
	public void produire(boolean majIdentification, ILectureEchange echange, IEtatDifference etatDifference, Connection connexion, boolean incremental) {
		gestionSequence.setConnexion(connexion);
		gestionSequence.initialiser();
		produire(majIdentification, echange, etatDifference, incremental);
		gestionSequence.actualiser();
	}
	
	public void setGestionFichier(IGestionFichier gestionFichier) {
		this.gestionFichier = gestionFichier;
	}
	
	public void setGestionSequence(IGestionSequence gestionSequence) {
		this.gestionSequence = gestionSequence;
	}
	
	public void setIdentificationManager(
			IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
	
	public Map<String, Long> getIdParObjectId() {
		return idParObjectId;
	}
}
