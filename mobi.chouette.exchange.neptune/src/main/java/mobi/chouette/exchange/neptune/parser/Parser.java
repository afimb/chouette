package mobi.chouette.exchange.neptune.parser;

import mobi.chouette.common.Context;

public interface Parser {

	public void parse(Context context) throws Exception;

}
