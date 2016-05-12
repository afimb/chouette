package mobi.chouette.exchange.regtopp.validation;

import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;

public class RegtoppWarning extends RegtoppException {

	private static final long serialVersionUID = 1L;

	public RegtoppWarning(FileParserValidationError context) {
		super(context);
	}
}
