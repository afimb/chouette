package fr.certu.chouette.service.database.impl.modele;

import fr.certu.chouette.modele.Horaire;
import java.util.Date;

public class EtatMajHoraire {
	
	private Horaire horaire;
	private EnumMaj enumMaj;
	
	public static EtatMajHoraire getCreation (Long idArretItineraire, Long idCourse, Date heureDepart, Date heureArrivee) {
		EtatMajHoraire instance = new EtatMajHoraire();
		Horaire horaire = new Horaire();
		horaire.setIdArret(idArretItineraire);
		horaire.setIdCourse(idCourse);
		horaire.setModifie(false);
		horaire.setDepartureTime(heureDepart);
		horaire.setArrivalTime(heureArrivee);
		instance.horaire = horaire;
		instance.enumMaj = EnumMaj.CREER;
		return instance;
	}
	
	public static EtatMajHoraire getCreation (Long idArretItineraire, Long idCourse, Date heure) {
		return getCreation(idArretItineraire, idCourse, heure, heure);
	}
	
	public static EtatMajHoraire getSuppression(Horaire horaire) {
		EtatMajHoraire instance = new EtatMajHoraire();
		instance.horaire = horaire;
		instance.enumMaj = EnumMaj.SUPPRIMER;
		return instance;
	}
	
	public static EtatMajHoraire getModification(Horaire horaire) {
		EtatMajHoraire instance = new EtatMajHoraire();
		Date arrivalTime = (Date)horaire.getDepartureTime().clone();
		horaire.setArrivalTime(arrivalTime);
		instance.horaire = horaire;
		instance.enumMaj = EnumMaj.DEPLACER;
		return instance;
	}
	
	public Horaire getHoraire() {
		return horaire;
	}
	
	public EnumMaj getEnumMaj() {
		return enumMaj;
	}
	
	@Override
	public String toString() {
		if (horaire == null)
			return "null";
		StringBuffer buffer = new StringBuffer();
		buffer.append("horaire.getId():");
		buffer.append(horaire.getId());
		buffer.append(" , ");
		buffer.append("idArretItineraire:");
		buffer.append(horaire.getIdArret());
		buffer.append(" , ");
		buffer.append("idCourse:");
		buffer.append(horaire.getIdCourse());
		buffer.append(" , ");
		buffer.append("heureDepart:");
		buffer.append(horaire.getDepartureTime());
		buffer.append(" , ");
		buffer.append("heureArrivee:");
		buffer.append(horaire.getArrivalTime());
		buffer.append(" , ");
		buffer.append("maj:");
		buffer.append(enumMaj);
		return buffer.toString();
	}
}
