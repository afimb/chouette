package fr.certu.chouette.service.xml;

import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ChouetteRemoveLineTypeType;
import java.io.File;

public interface ILecteurFichierXML {
	
	public ChouettePTNetworkTypeType lire(String fileName);
	public ChouettePTNetworkTypeType lire(String fileName, boolean withValidation);
	public void ecrire(ChouettePTNetworkTypeType chouette, File file);
	public void ecrire(ChouetteRemoveLineTypeType chouette, File file);
}
