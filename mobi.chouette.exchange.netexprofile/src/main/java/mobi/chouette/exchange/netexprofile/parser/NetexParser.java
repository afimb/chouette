package mobi.chouette.exchange.netexprofile.parser;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.netexprofile.Constant;

public interface NetexParser extends Parser, Constant {

    public void initReferentials(Context context) throws Exception;

}
