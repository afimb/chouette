package mobi.chouette.exchange.validation.checkpoint;

import java.util.regex.Pattern;

import mobi.chouette.common.ChouetteId;
import mobi.chouette.exchange.AbstractChouetteIdGenerator;
import mobi.chouette.model.NeptuneIdentifiedObject;

public class DummyChouetteIdGenerator extends AbstractChouetteIdGenerator{
	@Override
	public boolean checkObjectId(String oid,
			Class<? extends NeptuneIdentifiedObject> clazz) {
		if (oid == null)
			return false;

		Pattern p = Pattern.compile("(\\w|_)+:([0-9A-Za-z]|_|-)+");
		
		Pattern p2 = Pattern.compile("([0-9A-Za-z]|_|-)+");
		
		return (p.matcher(oid).matches() || p2.matcher(oid).matches());
	}

	@Override
	public ChouetteId toChouetteId(String objectId, String defaultCodespace,
			Class<? extends NeptuneIdentifiedObject> clazz) {
		ChouetteId chouetteId = null;
		
		// If object id is conform to gtfs format
		if (checkObjectId(objectId,clazz)) {
			String [] objectIdArray = objectId.split(":");
			String codespace;
			String technicalId;
			if (objectIdArray.length == 2) {
				codespace = objectIdArray[0];
				technicalId = objectIdArray[1];
			} else { // Codespace par défaut
				codespace = defaultCodespace;
				technicalId = objectIdArray[0];
			}
			
			chouetteId = new ChouetteId();
			chouetteId.setCodeSpace(codespace);
			chouetteId.setTechnicalId(technicalId);
		}
		
		return chouetteId;
	}

	@Override
	public String toSpecificFormatId(ChouetteId chouetteId, String defaultCodespace, NeptuneIdentifiedObject object) {
		String objectId = null;
		// Produire l'identifiant pour chaque type d'objet possible pour chaque format : NB : les types d'objet sont différents selon les formats
		
		if (chouetteId.getCodeSpace() == null)
			objectId += defaultCodespace;
		else
			objectId += chouetteId.getCodeSpace();
		
		objectId += ":";
		objectId += chouetteId.getTechnicalId();
		
		return objectId;
	}
}
