package mobi.chouette.exchange.importer.updater;

import java.util.ArrayList;
import java.util.Collection;

import mobi.chouette.model.NeptuneIdentifiedObject;

public class UpdaterUtils {

	public static Collection<String> getObjectIds(Collection<?> list) {
		final Collection<String> result = new ArrayList<String>();
		for (Object o : list) {
			if (o instanceof NeptuneIdentifiedObject) {
				result.add(((NeptuneIdentifiedObject) o).getObjectId());
			}
		}
		return result;
	}
}
