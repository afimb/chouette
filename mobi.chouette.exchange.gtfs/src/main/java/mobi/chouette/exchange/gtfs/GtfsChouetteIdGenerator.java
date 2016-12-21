package mobi.chouette.exchange.gtfs;

import java.io.IOException;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.ChouetteId;
import mobi.chouette.exchange.AbstractChouetteIdGenerator;
import mobi.chouette.exchange.ChouetteIdGenerator;
import mobi.chouette.exchange.ChouetteIdGeneratorFactory;
import mobi.chouette.model.NeptuneIdentifiedObject;

@Log4j
public class GtfsChouetteIdGenerator extends AbstractChouetteIdGenerator{
	@Override
	public boolean checkObjectId(String oid, Class<? extends NeptuneIdentifiedObject> clazz) {
		if (oid == null)
			return false;

		Pattern p = Pattern.compile("(\\w|_)+:([0-9A-Za-z]|_|-)+");
		
		Pattern p2 = Pattern.compile("([0-9A-Za-z]|_|-)+");
		
		return (p.matcher(oid).matches() || p2.matcher(oid).matches());
	}

	@Override
	public ChouetteId toChouetteId(String objectId, String defaultCodespace, Class<? extends NeptuneIdentifiedObject> clazz) {
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
			
			chouetteId = new ChouetteId(codespace, technicalId, false);
		} else {
			log.info("Object id : " + objectId + " non conforme au format neptune");
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

	public static class DefaultFactory extends ChouetteIdGeneratorFactory {

		@Override
		protected ChouetteIdGenerator create() throws IOException {
			ChouetteIdGenerator result = new GtfsChouetteIdGenerator();
			return result;
		}
	}

	static {
		ChouetteIdGeneratorFactory.factories.put("gtfs", new DefaultFactory());
	}
}
