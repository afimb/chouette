package mobi.chouette.exchange.hub;

import java.io.IOException;

import mobi.chouette.common.ChouetteId;
import mobi.chouette.exchange.AbstractChouetteIdGenerator;
import mobi.chouette.exchange.ChouetteIdGenerator;
import mobi.chouette.exchange.ChouetteIdGeneratorFactory;
import mobi.chouette.model.NeptuneIdentifiedObject;

public class HubChouetteIdGenerator extends AbstractChouetteIdGenerator{
	
	@Override
	public String toSpecificFormatId(ChouetteId chouetteId, String defaultCodespace, NeptuneIdentifiedObject object) {
		String objectId = null;
	
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
			ChouetteIdGenerator result = new HubChouetteIdGenerator();
			return result;
		}
	}

	static {
		ChouetteIdGeneratorFactory.factories.put("hub", new DefaultFactory());
	}
}
