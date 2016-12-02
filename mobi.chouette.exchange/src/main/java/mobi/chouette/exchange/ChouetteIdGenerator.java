package mobi.chouette.exchange;

import java.util.List;

import mobi.chouette.common.ChouetteId;
import mobi.chouette.model.NeptuneIdentifiedObject;



public interface ChouetteIdGenerator {
	/**
	 * check if an objectId is conform to Trident
	 * 
	 * @param oid
	 *            objectId to check
	 * @return true if valid, false othewise
	 */
	public boolean checkObjectId(String oid, Class<? extends NeptuneIdentifiedObject> clazz);
	
	/**
	 * Convert some object id to chouette id for import
	 */
	public ChouetteId toChouetteId(String objectId, String defaultCodespace, Class<? extends NeptuneIdentifiedObject> clazz);
	
	/**
	 * Convert some object id list to chouette id list
	 * @param lstObjectId
	 * @param defaultCodespace
	 * @return
	 */
	public List<ChouetteId> toListChouetteId(List<String> lstObjectId, String defaultCodespace, Class<? extends NeptuneIdentifiedObject> clazz);
			
	/**
	 * Convert some chouette id to specific format id for export
	 */
	public String toSpecificFormatId(ChouetteId chouetteId, String defaultCodespace, NeptuneIdentifiedObject object);
	
	/**
	 * Convert some chouette id list to specific format id list
	 * @param lstChouetteId
	 * @param defaultCodespace
	 * @return
	 */
	public List<String> toListSpecificFormatId(List<ChouetteId> lstChouetteId, String defaultCodespace, NeptuneIdentifiedObject object);

}
