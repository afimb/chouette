package mobi.chouette.exchange;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.ChainImpl;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class TransactionnalCommand extends ChainImpl {

	public static final String COMMAND = "TransactionnalCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO transaction 
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		log.info("[DSU] execute " + this.toString());
		result = super.execute(context);
		log.info("[DSU] " + monitor.stop());
		result = SUCCESS;
		return result;
	}

}
