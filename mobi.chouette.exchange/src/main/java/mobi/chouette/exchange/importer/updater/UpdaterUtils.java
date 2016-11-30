package mobi.chouette.exchange.importer.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.chouette.model.ChouetteId;
import mobi.chouette.model.NeptuneIdentifiedObject;

public class UpdaterUtils {

	public static Collection<ChouetteId> getChouetteIds(Collection<?> list) {
		final Collection<ChouetteId> result = new ArrayList<ChouetteId>();
		for (Object o : list) {
			if (o instanceof NeptuneIdentifiedObject) {
				result.add(((NeptuneIdentifiedObject) o).getChouetteId());
			}
		}
		return result;
	}
	
	/**
	 * Get object id list by codespace
	 * @param list
	 * @return
	 */
	public static Map<String,List<String>> getChouetteIdsByCodeSpace(Collection<?> list) {
		final Map<String,List<String>> result = new HashMap<>();
		for (Object o : list) {
			if (o instanceof NeptuneIdentifiedObject) {
				ChouetteId chouetteId = ((NeptuneIdentifiedObject) o).getChouetteId();
				String codespace = chouetteId.getCodeSpace();
				if (!result.containsKey(codespace))
					result.put(codespace, new ArrayList<String>());

				result.get(codespace).add(chouetteId.getTechnicalId());
			}
		}
		return result;
	}

	
}
