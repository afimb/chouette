package mobi.chouette.exchange.regtopp.importer.index.v11;

import org.apache.commons.lang.StringUtils;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class DestinationById extends IndexImpl<RegtoppDestinationDST> {

	public DestinationById(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	@Override
	public boolean validate(RegtoppDestinationDST bean, RegtoppImporter dao) {
		boolean result = true;

		if (StringUtils.trimToNull(bean.getDestinationText()) == null) {
			// validationReporter.reportError(new Context(), ex, filenameInfo);

			// TODO add entry to validationReporter
			result = false;
		}

		return result;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new DestinationById(validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(DestinationById.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
		for (Object obj : parser.getRawContent()) {
			RegtoppDestinationDST newRecord = (RegtoppDestinationDST) obj;
			RegtoppDestinationDST existingRecord = index.put(newRecord.getDestinationId(), newRecord);
			if (existingRecord != null) {
				log.error("Duplicate key in DST file. Existing: "+existingRecord+" Ignored duplicate: "+newRecord);
				validationReporter.reportError(new Context(), new RegtoppException(new FileParserValidationError(RegtoppDestinationDST.FILE_EXTENSION,
						newRecord.getRecordLineNumber(), "Destinasjonsnr", newRecord.getDestinationId(), ERROR.DUPLICATE_KEY, "Duplicate key")), null);
			}
		}
	}
}
