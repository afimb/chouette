package mobi.chouette.common.chain;

import javax.ejb.Local;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;

@Local
public interface Command extends Constant {

	boolean execute(Context context) throws Exception;
}
