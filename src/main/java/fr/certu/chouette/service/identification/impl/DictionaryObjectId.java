package fr.certu.chouette.service.identification.impl;

import java.util.HashMap;
import java.util.Map;
import fr.certu.chouette.service.identification.IDictionaryObjectId;

public class DictionaryObjectId implements IDictionaryObjectId {

	public Map<String, String> positionsGeographiquesObjectIdParRef;
	public Map<String, String> tableauxMarcheObjectIdParRef;
	public Map<String, String> objectIdParOldObjectId;
	
	public DictionaryObjectId() {
		init();
	}
	
	public void addObjectIdParOldObjectId(String oldObjectId, String newObjectId) {
		objectIdParOldObjectId.put(oldObjectId, newObjectId);
	}
	
	public void addObjectIdParReference(String ref, String objectId) {
		if (objectId.toLowerCase().indexOf("timetable") > 0)
			tableauxMarcheObjectIdParRef.put(ref, objectId);
		else if (objectId.toLowerCase().indexOf("stoparea") > 0)
			positionsGeographiquesObjectIdParRef.put(ref, objectId);
	}
	
	public void completion() {
	    positionsGeographiquesObjectIdParRef.clear();
		tableauxMarcheObjectIdParRef.clear();
		objectIdParOldObjectId.clear();
	}
	
	public Map<String, String> getPositionsGeographiquesObjectIdParRef() {
		return positionsGeographiquesObjectIdParRef;
	}
	
	public Map<String, String> getTableauxMarcheObjectIdParRef() {
		return tableauxMarcheObjectIdParRef;
	}
	
	public Map<String, String> getObjectIdParOldObjectId() {
		return objectIdParOldObjectId;
	}
	
	public void init() {
		positionsGeographiquesObjectIdParRef = new HashMap<String, String>();
		tableauxMarcheObjectIdParRef = new HashMap<String, String>();
		objectIdParOldObjectId = new HashMap<String, String>();
	}
}
