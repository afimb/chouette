package fr.certu.chouette.service.identification;

import fr.certu.chouette.modele.BaseObjet;

public interface IIdentificationManager {
	
	String getIdFonctionnel( final String nom, final BaseObjet baseObjet);
	String getIdFonctionnel( final String specificSystemId, final String nom, final BaseObjet baseObjet);
	String getIdFonctionnel( final String specificSystemId, final String nom, final String idValue);
	String getIdFonctionnel(final String nom, final String idValue);
	IDictionaryObjectId getDictionaryObjectId();
	void setDictionaryObjectId(IDictionaryObjectId dictionaryObjectId);
}