package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import java.util.Map;
import fr.certu.chouette.modele.TableauMarche;

public interface ILecteurCalendrier extends ILecteurSpecifique {
	
	public Map<String, TableauMarche> getTableauxMarchesParRef();
}
