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
import mobi.chouette.exchange.regtopp.model.v13.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR;

@Log4j
public class StopById extends IndexImpl<AbstractRegtoppStopHPL> {

	public StopById(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	@Override
	public boolean validate(AbstractRegtoppStopHPL bean, RegtoppImporter dao) {
		boolean result = true;

		//Obligatoriske felter
		if (bean.getAdminCode() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppStopHPL.FILE_EXTENSION, bean.getRecordLineNumber(), "Adminkode", null, RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getCounter() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppStopHPL.FILE_EXTENSION, bean.getRecordLineNumber(), "Løpenr", null, RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getStopId() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppStopHPL.FILE_EXTENSION, bean.getRecordLineNumber(), "Holdeplassnr", null, RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getFullName() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppStopHPL.FILE_EXTENSION, bean.getRecordLineNumber(), "Fullstendig navn", null, RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getInterchangeType() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppStopHPL.FILE_EXTENSION, bean.getRecordLineNumber(), "Type", null, RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getInterchangeMinutes() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppStopHPL.FILE_EXTENSION, bean.getRecordLineNumber(), "Spes. omstigningstid", null, RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getCoachClass() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppStopHPL.FILE_EXTENSION, bean.getRecordLineNumber(), "Klasse", null, RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, "")));
			result = false;
		}


		//Holdeplass nr er riktig bygd opp (kommunenummer + 4 siffer)
		String stopId = bean.getStopId();
		String municipalityCodeString = stopId.substring(0, 4);
		String stopSequenceNumber = stopId.substring(4, 8);
		int municipalityCode = Integer.valueOf(municipalityCodeString);
		if (101 <= municipalityCode && municipalityCode <= 2211 && stopSequenceNumber.matches("\\d{4}")) {		//Lots of holes in the 101-2211 range
			bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppStopHPL.FILE_EXTENSION, bean.getRecordLineNumber(), "Holdeplassnr", bean.getStopId(), RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, "")));
			result = false;
		}

		// Koordinater ulike
		if (bean.getX() != null && bean.getY() != null){
			if (!bean.getX().equals(bean.getY())) {
				bean.getOkTests().add(RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
			} else {
				bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppStopHPL.FILE_EXTENSION, bean.getRecordLineNumber(), "X = Y", bean.getX().toString(), RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE, "")));
				result = false;
			}
		}

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
			AbstractRegtoppStopHPL newRecord = (AbstractRegtoppStopHPL) obj;
			AbstractRegtoppStopHPL existingRecord = index.put(newRecord.getFullStopId(), newRecord);
			if (existingRecord != null) {
				log.error("Duplicate key in HPL file. Existing: "+existingRecord+" Ignored duplicate: "+newRecord);
				validationReporter.reportError(new Context(), new RegtoppException(new FileParserValidationError(RegtoppStopHPL.FILE_EXTENSION,
						newRecord.getRecordLineNumber(), "Holdeplassnr", newRecord.getStopId(), ERROR.DUPLICATE_KEY, "Duplicate key")), null);
			}
		}
	}
}
