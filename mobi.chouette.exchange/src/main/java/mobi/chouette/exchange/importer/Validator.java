package mobi.chouette.exchange.importer;

import mobi.chouette.common.Context;

public interface Validator {

	public void validate(Context context) throws Exception;

}
