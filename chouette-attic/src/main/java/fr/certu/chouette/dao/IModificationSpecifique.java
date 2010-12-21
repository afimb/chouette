package fr.certu.chouette.dao;

import java.util.Collection;
import java.util.List;

public interface IModificationSpecifique {
	
	void supprimerArretItineraire(Long idArret);
	void supprimerCourse(Long idCourse);
	void supprimerHorairesItineraire(Long idItineraire);
	void supprimerItineraire(Long idItineraire);
	void supprimerItineraire(Long idItineraire, boolean detruireAvecTMs, boolean detruireAvecArrets);
	void supprimerLigne(Long idLigne);
	void supprimerLigne(Long idLigne, boolean detruireAvecTMs, boolean detruireAvecArrets, boolean detruireAvecTransporteur, boolean detruireAvecReseau);
	void supprimerReseau(Long idReseau);
	void supprimerTransporteur(Long idTransporteur);
	void supprimerGeoPosition(Long idPosition);
	void supprimerGeoPositions(Collection<Long> idPhysiques);
	void supprimerArretsItineraire(Collection<Long> idLogiques);
	void deplacerArrets(List<Long> arretsOrdreInitial, List<Long> arretsOrdreNouveau, List<Integer> nouvellesPositions);
	void associerCourseTableauxMarche(Long idCourse, List<Long> idTMs);
	void associerTableauMarcheCourses(Long idTM, List<Long> idCourses);
	void echangerPositions(List<Long> arretsOrdreNouveau, List<Integer> nouvellesPositions);
	void echangerHoraires(List<Long> arretsOrdreInitial, List<Long> arretsOrdreNouveau);
	void associerGeoPositions(Long idContenant, Long idContenue);
	void dissocierGeoPositionParente(Long idContenue);
	void dissocierGeoPositionsContenues(Long idContenant);
	void dissocierGeoPosition(Long idGeoPosition);
	void associerItineraire(Long idRoute1, Long idRoute2);
	void dissocierItineraire(Long idRoute1);
	void referencerDepartsCourses(Long idItineraire);
	void dissocierITLGeoPosition(Collection<Long> idGeoPositions);
	void affecterMission(Long idMission, Collection<Long> idCourses);
	void supprimerMissionSansCourse(final Collection<Long> idMissions);
	void fusionnerMissions(Long idMission, Long idMissionPrincipale);
	void substituerArretPhysiqueDansArretsItineraireAssocies(Long idAncienArretPhysique, Long idNouveauArretPhysique);
	void substituerArretPhysiqueDansITLsAssocies(Long idAncienArretPhysique, Long idNouveauArretPhysique);
}
