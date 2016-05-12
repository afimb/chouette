package mobi.chouette.exchange.regtopp.importer.index.v11;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppStopHPL;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR;

import static mobi.chouette.exchange.regtopp.messages.RegtoppMessages.getMessage;

@Log4j
public class StopById extends IndexImpl<AbstractRegtoppStopHPL> {

	public StopById(Context context, RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(context, validationReporter, fileParser);
	}

	@Override
	public boolean validate(AbstractRegtoppStopHPL bean, RegtoppImporter dao) {
		boolean result = true;

		//Mandatory fields
		if (isNotNull(bean.getAdminCode())){
			bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppStopHPL.adminCode"), bean.getAdminCode(), RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (isNotNull(bean.getCounter())) {
			bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppStopHPL.counter"), bean.getCounter(), RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (isNotNull(bean.getStopId())) {
			bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppStopHPL.stopId"), bean.getStopId(), RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (isNotNull(bean.getFullName())) {
			bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppStopHPL.fullName"), bean.getFullName(), RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (isNotNull(bean.getCoachClass())) {
			bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppStopHPL.coachClass"), bean.getCoachClass(), RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		// Coordinates should be different
		if (bean.getX() != null && bean.getY() != null){
			if (!bean.getX().equals(bean.getY())) {
				bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
			} else {
				bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppStopHPL.x") + " = " + getMessage("label.regtoppStopHPL.y") , bean.getX().toString() + "/" + bean.getY().toString(), RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
				result = false;
			}
		}

		return result;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(Context context, RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new StopById(context, validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(StopById.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
	
		for (Object obj : parser.getRawContent()) {
			AbstractRegtoppStopHPL newRecord = (AbstractRegtoppStopHPL) obj;
			AbstractRegtoppStopHPL existingRecord = index.put(newRecord.getFullStopId(), newRecord);
			if (existingRecord != null) {
				log.warn("Duplicate key in HPL file. Existing: "+existingRecord+" Ignored duplicate: "+newRecord);
				validationReporter.reportError(context, new RegtoppException(new FileParserValidationError(getUnderlyingFilename(),
						newRecord.getRecordLineNumber(), getMessage("label.regtoppStopHPL.stopId"), newRecord.getFullStopId(), ERROR.HPL_DUPLICATE_KEY, getMessage("label.validation.duplicateKeyError"))), getUnderlyingFilename());
			}
		}
	}
}
