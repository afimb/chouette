package mobi.chouette.exchange.importer.updater;

import java.util.ArrayList;
import java.util.Collection;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.NeptuneIdentifiedObject;

@Log4j
public class UpdaterUtils {

	public static Collection<String> getObjectIds(Collection<?> list) {
		final Collection<String> result = new ArrayList<String>();
		for (Object o : list) {
			if (o instanceof NeptuneIdentifiedObject) {
				if (((NeptuneIdentifiedObject) o).getObjectId() == null )
				{
					log.warn("missing objectid for "+o);
					continue;
				}
				result.add(((NeptuneIdentifiedObject) o).getObjectId());
			}
		}
		return result;
	}
}
