package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import java.util.ResourceBundle;

public interface ILecteurSpecifique {
    
    public void lire(String[] ligneCSV, String _lineNumber);
    public boolean isTitreReconnu(String[] ligneCSV);
    public void validerCompletude();
    public void reinit(ResourceBundle bundle);
}
