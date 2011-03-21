package fr.certu.chouette.service.importateur.multilignes.genericcsv;

public interface ILecteurSpecifiqueMission {

    public boolean isTitreReconnu(String[] ligneCSV);
    public void validerCompletude();
    public void reinit();
}
