package fr.certu.chouette.service.fichier.formatinterne.impl;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import java.util.Map;

public interface IProducteurSpecifique {
	
	public Map<String, Long> produire(final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId);
	public Map<String, Long> produire(final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId, boolean incremantal);
}
