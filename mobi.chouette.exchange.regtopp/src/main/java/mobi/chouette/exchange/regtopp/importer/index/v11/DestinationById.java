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

import static mobi.chouette.exchange.regtopp.messages.RegtoppMessages.getMessage;

@Log4j
public class DestinationById extends IndexImpl<RegtoppDestinationDST> {

	public DestinationById(Context context, RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(context, validationReporter, fileParser);
	}

	@Override
	public boolean validate(RegtoppDestinationDST bean, RegtoppImporter dao) {
		boolean result = true;

		if (StringUtils.trimToNull(bean.getDestinationText()) != null) {
			bean.getOkTests().add(RegtoppException.ERROR.DST_INVALID_FIELD_VALUE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppDestinationDST.destinationTextHack"), bean.getDestinationText(), RegtoppException.ERROR.DST_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		return result;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(Context context, RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new DestinationById(context, validationReporter, parser);
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
				log.warn("Duplicate key in DST file. Existing: "+existingRecord+" Ignored duplicate: "+newRecord);
				validationReporter.reportError(context, new RegtoppException(new FileParserValidationError(getUnderlyingFilename(),
						newRecord.getRecordLineNumber(), getMessage("label.regtoppDestinationDST.destinationId"), newRecord.getDestinationId(), ERROR.DST_DUPLICATE_KEY, getMessage("label.validation.duplicateKeyError"))), getUnderlyingFilename());
			}
		}
	}
}
