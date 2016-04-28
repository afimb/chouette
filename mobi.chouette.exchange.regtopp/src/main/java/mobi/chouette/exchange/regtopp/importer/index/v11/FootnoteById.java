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
import mobi.chouette.exchange.regtopp.model.v11.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class FootnoteById extends IndexImpl<RegtoppFootnoteMRK> {

	public FootnoteById(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	@Override
	public boolean validate(RegtoppFootnoteMRK bean, RegtoppImporter dao) {
		boolean result = true;

		if (StringUtils.trimToNull(bean.getDescription()) != null) {
			bean.getOkTests().add(RegtoppException.ERROR.MRK_INVALID_FIELD_VALUE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppFootnoteMRK.FILE_EXTENSION, bean.getRecordLineNumber(), "Destinasjonstekst", null, RegtoppException.ERROR.MRK_INVALID_FIELD_VALUE, "")));
			result = false;
		}

		return result;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new FootnoteById(validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(FootnoteById.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
		for (Object obj : parser.getRawContent()) {
			RegtoppFootnoteMRK newRecord = (RegtoppFootnoteMRK) obj;
			RegtoppFootnoteMRK existingRecord = index.put(newRecord.getFootnoteId(), newRecord);
			if (existingRecord != null) {
				// TODO fix exception/validation reporting
				log.error("Duplicate key in MRK file. Existing: "+existingRecord+" Ignored duplicate: "+newRecord);
				validationReporter.reportError(new Context(), new RegtoppException(new FileParserValidationError(RegtoppFootnoteMRK.FILE_EXTENSION,
						newRecord.getRecordLineNumber(), "Merknadsnr", newRecord.getFootnoteId(), ERROR.DUPLICATE_KEY, "Duplicate key")), null);
			}
		}
	}
}
