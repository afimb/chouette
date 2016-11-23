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
	public static Map<String,List<ChouetteId>> getChouetteIdsByCodeSpace(Collection<?> list) {
		final Map<String,List<ChouetteId>> result = new HashMap<>();
		for (Object o : list) {
			if (o instanceof NeptuneIdentifiedObject) {
				String codespace = ((NeptuneIdentifiedObject) o).getChouetteId().getCodeSpace();
				if (!result.containsKey(codespace))
					result.put(codespace, new ArrayList<ChouetteId>());

				result.get(codespace).add(((NeptuneIdentifiedObject) o).getChouetteId());
			}
		}
		return result;
	}

	
}
