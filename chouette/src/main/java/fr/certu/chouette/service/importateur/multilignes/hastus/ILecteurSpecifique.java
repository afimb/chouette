package fr.certu.chouette.service.importateur.multilignes.hastus;

public interface ILecteurSpecifique {
	
	public boolean isTitreReconnu(String[] ligneCSV);
	public void reinit();
	public void lire(String[] ligneCSV);
	public int getCounter();
	public void setCounter(int counter);
}
