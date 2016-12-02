package mobi.chouette.exchange.importer.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.chouette.common.ChouetteId;
import mobi.chouette.model.NeptuneIdentifiedObject;

public class UpdaterUtils {

	public static Collection<ChouetteId> getChouetteIds(Collection<? extends NeptuneIdentifiedObject> list) {
		final Collection<ChouetteId> result = new ArrayList<ChouetteId>();
		for (NeptuneIdentifiedObject o : list) {
			result.add(((NeptuneIdentifiedObject) o).getChouetteId());
		}
		return result;
	}

	/**
	 * Get object id list by codespace
	 * 
	 * @param list
	 * @return
	 */
	public static Map<String, List<String>> getChouetteIdsByCodeSpace(Collection<? extends NeptuneIdentifiedObject> list) {
		final Map<String, List<String>> result = new HashMap<>();
		for (NeptuneIdentifiedObject o : list) {
			ChouetteId chouetteId = o.getChouetteId();
			String codespace = chouetteId.getCodeSpace();
			if (!result.containsKey(codespace))
				result.put(codespace, new ArrayList<String>());

			result.get(codespace).add(chouetteId.getTechnicalId());

		}
		return result;
	}
	/**
	 * Get object id list by codespace
	 * 
	 * @param list
	 * @return
	 */
	public static Map<String, List<String>>dispatchChouetteIdsByCodeSpace(Collection<ChouetteId> list) {
		final Map<String, List<String>> result = new HashMap<>();
		for (ChouetteId chouetteId : list) {
			String codespace = chouetteId.getCodeSpace();
			if (!result.containsKey(codespace))
				result.put(codespace, new ArrayList<String>());

			result.get(codespace).add(chouetteId.getTechnicalId());

		}
		return result;
	}

}
