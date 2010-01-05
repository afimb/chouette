package fr.certu.chouette.service.identification;

public class ObjectIdLecteur {
	
	public static String lirePartieSysteme(String objectId) {
		String[] tblObjectId = objectId.split(":");
		if (tblObjectId == null || tblObjectId.length != 3)
			throw new IllegalArgumentException("Identifiant "+objectId+" non valide.");
		return tblObjectId[0];
	}
	
	public static String lirePartieCode(String objectId) {
		String[] tblObjectId = objectId.split(":");
		if (tblObjectId == null || tblObjectId.length != 3)
			throw new IllegalArgumentException("Identifiant "+objectId+" non valide.");
		return tblObjectId[2];
	}
	
	public static String getNouveauId(String objectId, String typeDonne){
		return lirePartieSysteme(objectId)+":"+typeDonne+":"+lirePartieCode(objectId);
	}
	
	public static String lirePartieSysteme2(String objectId) {
		String[] tblObjectId = objectId.split(":");
		if (tblObjectId == null || tblObjectId.length < 3)
			throw new IllegalArgumentException("Identifiant "+objectId+" non valide.");
		String partieSysteme = tblObjectId[0];
		for (int i = 0; i < tblObjectId.length - 3; i++)
			partieSysteme += ":" + tblObjectId[i];
		return partieSysteme;
	}
	
	public static String lirePartieCode2(String objectId) {
		String[] tblObjectId = objectId.split(":");
		if (tblObjectId == null || tblObjectId.length < 3)
			throw new IllegalArgumentException("Identifiant "+objectId+" non valide.");
		return tblObjectId[tblObjectId.length-1];
	}
	
	public static String lireTypeDonnee(String objectId) {
		String[] tblObjectId = objectId.split(":");
		if (tblObjectId == null || tblObjectId.length < 3)
			throw new IllegalArgumentException("Identifiant "+objectId+" non valide.");
		return tblObjectId[tblObjectId.length-2];
	}
}
