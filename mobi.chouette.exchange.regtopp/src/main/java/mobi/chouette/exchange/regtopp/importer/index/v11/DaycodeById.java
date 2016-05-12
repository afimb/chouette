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
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

import static mobi.chouette.exchange.regtopp.messages.RegtoppMessages.getMessage;

@Log4j

public class DaycodeById extends IndexImpl<RegtoppDayCodeDKO> {

	public DaycodeById(Context context, RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
		super(context, validationReporter, parser);
	}

	@Getter
	private RegtoppDayCodeHeaderDKO header;

	@Override
	public boolean validate(RegtoppDayCodeDKO bean, RegtoppImporter dao) {
		boolean result = true;

		//Mandatory fields
		if (isNotNull(bean.getAdminCode())) {
			bean.getOkTests().add(RegtoppException.ERROR.DKO_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppDayCodeDKO.adminCode"), bean.getAdminCode(), RegtoppException.ERROR.DKO_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (isNotNull(bean.getCounter())) {
			bean.getOkTests().add(RegtoppException.ERROR.DKO_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppDayCodeDKO.counter"), bean.getCounter(), RegtoppException.ERROR.DKO_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (isNotNull(bean.getDayCodeId())) {
			bean.getOkTests().add(RegtoppException.ERROR.DKO_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppDayCodeDKO.dayCodeId"), bean.getDayCodeId(), RegtoppException.ERROR.DKO_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		return result;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(Context context, RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new DaycodeById(context, validationReporter, parser);
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
					log.warn("Duplicate key in DKO file. Existing: "+existingRecord+" Ignored duplicate: "+newRecord);
					validationReporter.reportError(context, new RegtoppException(new FileParserValidationError(getUnderlyingFilename(),
							newRecord.getRecordLineNumber(), getMessage("label.regtoppDayCodeDKO.dayCode"), newRecord.getDayCode(), ERROR.DKO_DUPLICATE_KEY, getMessage("label.validation.duplicateKeyError"))), getUnderlyingFilename());
				}

			}
		}
	}

}
