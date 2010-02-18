package fr.certu.chouette.service.fichier.formatinterne;

public interface IFournisseurId {
	
	public long getNouvelId(String objectId1, String objectId2);
	public long getNouvelId(String objectId);
}
