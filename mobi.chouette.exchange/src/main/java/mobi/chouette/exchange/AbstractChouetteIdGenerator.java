package mobi.chouette.exchange;

import mobi.chouette.model.ChouetteId;
import mobi.chouette.model.NeptuneIdentifiedObject;


public class AbstractChouetteIdGenerator implements ChouetteIdGenerator{

	@Override
	public boolean checkObjectId(String oid) {
		return false;
	}

	@Override
	public ChouetteId toChouetteId(String objectId, String defaultCodespace) {
		return null;
	}

	@Override
	public String toSpecificFormatId(ChouetteId chouetteId, String defaultCodespace, NeptuneIdentifiedObject object) {
		return null;
	}
	
}
