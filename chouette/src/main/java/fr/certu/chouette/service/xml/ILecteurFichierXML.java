package fr.certu.chouette.service.xml;

import chouette.schema.ChouettePTNetworkType;
import chouette.schema.ChouetteRemoveLineType;
import java.io.File;

public interface ILecteurFichierXML {
	
	public ChouettePTNetworkType lire(String fileName);
	public ChouettePTNetworkType lire(String fileName, boolean withValidation);
	public void ecrire(ChouettePTNetworkType chouette, File file);
	public void ecrire(ChouetteRemoveLineType chouette, File file);
}
