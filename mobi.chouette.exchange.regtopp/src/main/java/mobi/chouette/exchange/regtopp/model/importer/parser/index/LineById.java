package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.model.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class LineById extends IndexImpl<RegtoppLineLIN> {

	public LineById(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	@Override
	public boolean validate(RegtoppLineLIN bean, RegtoppImporter dao) {
		boolean result = true;

		// Mulige valideringssteg

		// TODO

		log.warn("Validation code for RegtoppLine not implemented");

		return result;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new LineById(validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(LineById.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
		for (Object obj : _parser.getRawContent()) {
			RegtoppLineLIN line = (RegtoppLineLIN) obj;
			RegtoppLineLIN existing = _index.put(line.getLineId(), line);
			if (existing != null) {
				// TODO fix exception/validation reporting (this is a duplicate check)
				_validationReporter.reportError(new Context(), new RegtoppException(new FileParserValidationError()), null);
			}
		}
	}
}
