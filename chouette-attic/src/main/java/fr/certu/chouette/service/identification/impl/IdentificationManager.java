package fr.certu.chouette.service.identification.impl;

import fr.certu.chouette.modele.BaseObjet;
import fr.certu.chouette.service.identification.IDictionaryObjectId;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class IdentificationManager implements IIdentificationManager {
	
	public String systemId;
	public IDictionaryObjectId dictionaryObjectId;

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IIdentificationManager#getSystemId()
	 */
	public String getIdFonctionnel( final String nom, final BaseObjet baseObjet) 
	{
		return getIdFonctionnel( systemId, nom, baseObjet);
	}

	public String getIdFonctionnel( final String specificSystemId, final String nom, final BaseObjet baseObjet) 
	{
		return getIdFonctionnel( specificSystemId, nom, baseObjet.getId().toString());
	}

	public String getIdFonctionnel( final String nom, final String idValue) 
	{
		return getIdFonctionnel( systemId, nom, idValue);
	}

	public String getIdFonctionnel( final String specificSystemId, final String nom, final String idValue) {
		StringBuffer buffer = new StringBuffer( specificSystemId);
		buffer.append( ":");
		buffer.append( nom);
		buffer.append( ":");
		buffer.append( idValue);
		return buffer.toString();
	}	
	public void setSystemId(final String systemId) {
		this.systemId = systemId;
	}

	public IDictionaryObjectId getDictionaryObjectId() {
		return dictionaryObjectId;
	}

	public void setDictionaryObjectId(IDictionaryObjectId dictionaryObjectId) {
		this.dictionaryObjectId = dictionaryObjectId;
	}
}
