package fr.certu.chouette.service.identification;

import java.util.Map;

public interface IDictionaryObjectId {
	
	public void addObjectIdParReference(String ref, String objectId);
	public void addObjectIdParOldObjectId(String oldObjectId, String newObjectId);
	public void init();
	public Map<String, String> getTableauxMarcheObjectIdParRef();
	public Map<String, String> getPositionsGeographiquesObjectIdParRef();
	public Map<String, String> getObjectIdParOldObjectId();
	public void completion();
}
