package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import fr.certu.chouette.modele.Ligne;

public interface ILecteurSpecifiqueLigne {

    public void lire(String[] ligneCSV, Ligne ligne);
    public boolean isTitreReconnu(String[] ligneCSV);
    public void validerCompletude();
    public void reinit();
}
