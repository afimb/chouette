package mobi.chouette.exchange;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Chain;

@Log4j
@Stateless
public class TransactionnalCommand {

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context, Chain chain) throws Exception {
		boolean result = Constant.ERROR;
		log.info("[DSU] execute " + this.toString());
		result = chain.execute(context);
		result = Constant.SUCCESS;
		return result;
	}

}
