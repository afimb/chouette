package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class StopById extends IndexImpl<RegtoppStopHPL> {

	public StopById(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	@Override
	public boolean validate(RegtoppStopHPL bean, RegtoppImporter dao) {
		boolean result = true;

		// Mulige valideringssteg

		// Koordinater ulike
		// Sone 1 og 2 forskjellige
		// Fullstendig navn !§= kortnavn

		// Holdeplassnummer X antall siffer

		log.warn("Validation code for RegtoppStopp not implemented");

		return result;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new StopById(validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(StopById.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
		for (Object obj : parser.getRawContent()) {
			RegtoppStopHPL stop = (RegtoppStopHPL) obj;
			RegtoppStopHPL existing = index.put(stop.getFullStopId(), stop);
			if (existing != null) {
				// TODO fix exception/validation reporting
				validationReporter.reportError(new Context(), new RegtoppException(new FileParserValidationError()), null);
			}
		}
	}
}
