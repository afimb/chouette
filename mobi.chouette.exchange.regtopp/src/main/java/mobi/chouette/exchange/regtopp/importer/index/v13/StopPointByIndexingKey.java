package mobi.chouette.exchange.regtopp.importer.index.v13;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppStopPointSTP;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class StopPointByIndexingKey extends IndexImpl<RegtoppStopPointSTP> {

	public StopPointByIndexingKey(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new StopPointByIndexingKey(validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(StopPointByIndexingKey.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
		for (Object obj : parser.getRawContent()) {
			RegtoppStopPointSTP newRecord = (RegtoppStopPointSTP) obj;
			RegtoppStopPointSTP existingRecord = index.put(newRecord.getIndexingKey(), newRecord);
			if (existingRecord != null) {
				log.error("Duplicate key in STP file. Existing: "+existingRecord+" Ignored duplicate: "+newRecord);
				validationReporter.reportError(new Context(), new RegtoppException(new FileParserValidationError(RegtoppStopPointSTP.FILE_EXTENSION,
						newRecord.getRecordLineNumber(), "Holdeplassnr/Stoppunktsnummer", newRecord.getIndexingKey(), ERROR.DUPLICATE_KEY, "Duplicate key")), null);
			}
		}
	}
}
