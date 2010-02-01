package fr.certu.chouette.service.importateur.multilignes.hastus;

import java.util.List;
import java.util.Map;

public interface ILecteurOrdre extends ILecteurSpecifique {
	
	public Map<String, List<String>> getListeOrdonneeArretsParItineraireName();
}
