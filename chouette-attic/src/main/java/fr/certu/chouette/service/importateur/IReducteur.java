package fr.certu.chouette.service.importateur;

public interface IReducteur {
	
	public String reduireCheminFichier(String chemin);
	public String reduire(String nom, boolean estCanonique);
}
