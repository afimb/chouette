package mobi.chouette.exchange.importer.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.chouette.model.NeptuneIdentifiedObject;

public class UpdaterUtils {

	public static Collection<String> getObjectIds(Collection<?> list) {
		final Collection<String> result = new ArrayList<String>();
		for (Object o : list) {
			if (o instanceof NeptuneIdentifiedObject) {
				result.add(((NeptuneIdentifiedObject) o).getChouetteId().getObjectId());
			}
		}
		return result;
	}
	
	/**
	 * Get object id list by codespace
	 * @param list
	 * @return
	 */
	public static Map<String,List<String>> getObjectIdsByCodeSpace(Collection<?> list) {
		final Map<String,List<String>> result = new HashMap<>();
		for (Object o : list) {
			if (o instanceof NeptuneIdentifiedObject) {
				String codespace = ((NeptuneIdentifiedObject) o).getChouetteId().getCodeSpace();
				if (!result.containsKey(codespace))
					result.put(codespace, new ArrayList<String>());

				result.get(codespace).add(((NeptuneIdentifiedObject) o).getChouetteId().getObjectId());
			}
		}
		return result;
	}

	
}
