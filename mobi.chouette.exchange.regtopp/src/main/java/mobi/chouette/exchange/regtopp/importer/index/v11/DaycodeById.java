package mobi.chouette.exchange.regtopp.importer.index.v11;

import java.util.List;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeHeaderDKO;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR;

@Log4j
public class DaycodeById extends IndexImpl<RegtoppDayCodeDKO> {

	@Getter
	private RegtoppDayCodeHeaderDKO header;

	public DaycodeById(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	@Override
	public boolean validate(RegtoppDayCodeDKO bean, RegtoppImporter dao) {
		boolean result = true;

		//Obligatoriske felter
		if (bean.getAdminCode() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppDayCodeDKO.FILE_EXTENSION, bean.getRecordLineNumber(), "Adminkode", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getCounter() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppDayCodeDKO.FILE_EXTENSION, bean.getRecordLineNumber(), "Løpenr", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getDayCodeId() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppDayCodeDKO.FILE_EXTENSION, bean.getRecordLineNumber(), "Dagkodenr", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		return result;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new DaycodeById(validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(DaycodeById.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {

		// First object in list will be the header object, the rest the usual DayCodeDKO objects
		List<Object> rawContent = parser.getRawContent();
		for (int i = 0; i < rawContent.size(); i++) {
			if (i == 0) {
				header = (RegtoppDayCodeHeaderDKO) rawContent.get(i);
			} else {
				RegtoppDayCodeDKO newRecord = (RegtoppDayCodeDKO) rawContent.get(i);
				RegtoppDayCodeDKO existingRecord = index.put(newRecord.getDayCodeId(), newRecord);
				if (existingRecord != null) {
					log.error("Duplicate key in DKO file. Existing: "+existingRecord+" Ignored duplicate: "+newRecord);
					validationReporter.reportError(new Context(), new RegtoppException(new FileParserValidationError(RegtoppDayCodeDKO.FILE_EXTENSION,
							newRecord.getRecordLineNumber(), "Dagkode", newRecord.getDayCode(), ERROR.DUPLICATE_KEY, "Duplicate key")), null);
				}

			}
		}
	}

}
