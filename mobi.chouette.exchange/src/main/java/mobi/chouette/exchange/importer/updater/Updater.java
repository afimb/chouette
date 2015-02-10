package mobi.chouette.exchange.importer.updater;

import javax.ejb.Local;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;

@Local
public interface Updater<T> extends Constant {

	void update(Context context, T oldValue, T newValue) throws Exception;

}
