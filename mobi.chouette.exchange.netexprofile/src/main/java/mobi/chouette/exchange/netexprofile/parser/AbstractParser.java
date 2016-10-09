package mobi.chouette.exchange.netexprofile.parser;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;

public abstract class AbstractParser implements Constant {

    public abstract void initializeReferentials(Context context) throws Exception;

}
