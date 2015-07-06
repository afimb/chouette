package mobi.chouette.exchange.importer;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.model.util.Referential;

@Log4j
public abstract class AbstractDisposeImportCommand implements Command, Constant {


	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;


		try {
			ValidationData validationData = (ValidationData) context.get(VALIDATION_DATA);
			if (validationData != null) validationData.dispose();
			Referential cache = (Referential) context.get(CACHE);
			if (cache != null) cache.clear();
			result = SUCCESS;

		} catch (Exception e) {
			log.error(e, e);
			throw e;
		} 

		return result;
	}


}
