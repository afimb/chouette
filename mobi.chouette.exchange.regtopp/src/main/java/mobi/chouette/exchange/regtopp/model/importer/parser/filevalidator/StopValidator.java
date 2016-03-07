package mobi.chouette.exchange.regtopp.model.importer.parser.filevalidator;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;

public class StopValidator extends FileContentValidator {

	@Override
	public void validate(Context _context, FileContentParser parser) {
		validateUniqueKeys(_context, parser);
		
	}
	
	

}
