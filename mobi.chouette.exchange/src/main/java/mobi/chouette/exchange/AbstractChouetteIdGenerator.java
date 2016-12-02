package mobi.chouette.exchange;

import java.util.ArrayList;
import java.util.List;

import mobi.chouette.common.ChouetteId;
import mobi.chouette.model.NeptuneIdentifiedObject;


public class AbstractChouetteIdGenerator implements ChouetteIdGenerator{

	@Override
	public boolean checkObjectId(String oid, Class<? extends NeptuneIdentifiedObject> clazz) {
		return false;
	}

	@Override
	public ChouetteId toChouetteId(String objectId, String defaultCodespace, Class<? extends NeptuneIdentifiedObject> clazz) {
		return null;
	}

	@Override
	public String toSpecificFormatId(ChouetteId chouetteId, String defaultCodespace, NeptuneIdentifiedObject object) {
		return null;
	}

	@Override
	public List<String> toListSpecificFormatId(List<ChouetteId> lstChouetteId, String defaultCodespace, NeptuneIdentifiedObject object) {
		List<String> lstObjectId = new ArrayList<String>();
		
		for(ChouetteId chouetteId: lstChouetteId)
			lstObjectId.add(toSpecificFormatId(chouetteId, defaultCodespace, object));
		
		return lstObjectId;
	}

	@Override
	public List<ChouetteId> toListChouetteId(List<String> lstObjectId, String defaultCodespace, Class<? extends NeptuneIdentifiedObject> clazz) {
		List<ChouetteId> lstChouetteId = new ArrayList<ChouetteId>();
		
		for(String objectId: lstObjectId)
			lstChouetteId.add(toChouetteId(objectId, defaultCodespace,clazz));
		
		return lstChouetteId;
	}
	
}
