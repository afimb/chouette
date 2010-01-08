package fr.certu.chouette.service.importateur.multilignes.hastus;

import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.Transporteur;
import java.util.Map;

public interface ILecteurLigne extends ILecteurSpecifique {
	
	public Transporteur getTransporteur();
	public Reseau getReseau();
	public Map<String, Ligne> getLigneParRegistration();
}
