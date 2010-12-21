package fr.certu.chouette.service.importateur;

import java.util.List;


public interface IImportCorrespondances {
	
	public List<String> lire(String canonicalPath);
}
