package mobi.chouette.exchange.regtopp.importer.index.v12;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR;

@Log4j
public class RouteByIndexingKey extends RouteIndex {

	public RouteByIndexingKey(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new RouteByIndexingKey(validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(RouteByIndexingKey.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
		for (Object obj : parser.getRawContent()) {
			AbstractRegtoppRouteTMS newRecord = (AbstractRegtoppRouteTMS) obj;
			AbstractRegtoppRouteTMS existingRecord = index.put(newRecord.getIndexingKey(), newRecord);
			if (existingRecord != null) {
				log.error("Duplicate key in TMS file. Existing: "+existingRecord+" Ignored duplicate: "+newRecord);
				validationReporter.reportError(new Context(), new RegtoppException(new FileParserValidationError(RegtoppRouteTMS.FILE_EXTENSION,
						newRecord.getRecordLineNumber(), "Linjenr/Turmønsternr/Retning/Sekvensnummer", newRecord.getIndexingKey(), ERROR.DUPLICATE_KEY, "Duplicate key")), null);
			}
		}
	}
}
