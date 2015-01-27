package mobi.chouette.importer.updater;

import mobi.chouette.common.Context;

public interface Updater<T> {

	void update(Context context, T oldValue, T newValue) throws Exception;

}
