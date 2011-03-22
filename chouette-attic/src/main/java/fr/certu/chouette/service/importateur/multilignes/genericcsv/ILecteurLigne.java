package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import java.util.List;
import fr.certu.chouette.modele.Ligne;

public interface ILecteurLigne extends ILecteurSpecifique {
    
    public List<Ligne> getLignes();
    public Ligne getLigneEnCours();
}
