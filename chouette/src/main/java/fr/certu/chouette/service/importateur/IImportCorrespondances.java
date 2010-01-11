package fr.certu.chouette.service.importateur;

import java.util.List;

import fr.certu.chouette.service.database.ICorrespondanceManager;

public interface IImportCorrespondances {
	
	public List<String> lire(String canonicalPath);
	public ICorrespondanceManager getCorrespondanceManager();
}
