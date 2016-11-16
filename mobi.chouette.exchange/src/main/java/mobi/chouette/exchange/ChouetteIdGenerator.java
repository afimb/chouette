package mobi.chouette.exchange;

import mobi.chouette.model.ChouetteId;
import mobi.chouette.model.NeptuneIdentifiedObject;



public interface ChouetteIdGenerator {
	/**
	 * check if an objectId is conform to Trident
	 * 
	 * @param oid
	 *            objectId to check
	 * @return true if valid, false othewise
	 */
	public boolean checkObjectId(String oid);
	
	/**
	 * Convert some object id to chouette id for import
	 */
	public ChouetteId toChouetteId(String objectId, String defaultCodespace);
	
	
	/**
	 * Convert some chouette id to specific format id for export
	 */
	public String toSpecificFormatId(ChouetteId chouetteId, String defaultCodespace, NeptuneIdentifiedObject object);

}
