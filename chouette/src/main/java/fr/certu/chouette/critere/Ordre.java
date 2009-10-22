package fr.certu.chouette.critere;


public class Ordre {
	private String propriete;
	private boolean croissant;

	public Ordre( String propriete, boolean croissant)
	{
		this.propriete = propriete;
		this.croissant = croissant;
	}

	public String getPropriete() {
		return propriete;
	}

	public boolean isCroissant() {
		return croissant;
	}
}
