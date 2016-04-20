package mobi.chouette.exchange.regtopp.importer.index.v12;

import static mobi.chouette.exchange.regtopp.RegtoppConstant.*;

import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

public abstract class RouteIndex extends IndexImpl<RegtoppRouteTMS> {

	public RouteIndex(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	@Override
	public boolean validate(RegtoppRouteTMS bean, RegtoppImporter dao) {
		boolean result = true;

		//Obligatoriske felter
		if (bean.getAdminCode() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Adminkode", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getCounter() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Løpenr", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getLineId() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Linjenr", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getDirection() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Retning", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getRouteId() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Turmønsternr", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getSequenceNumberStop() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Sekvensnr hpl", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getStopId() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Holdeplassnr", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		//Minst en
		if (bean.getDriverTimeArrival() != null || bean.getDriverTimeDeparture() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Kjøretid", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getDestinationId().equals(DESTINATION_NULL_REF) || dao.getDestinationById().containsKey(bean.getDestinationId())){
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Destinasjonsnr", bean.getDestinationId(), RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		if (bean.getRemarkId().equals(FOOTNOTE_NULL_REF) || dao.getFootnoteById().containsKey(bean.getRemarkId())){
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Merknadssnr", bean.getRemarkId(), RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		if (dao.getStopById().containsKey(bean.getStopId())){
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Holdeplassnr", bean.getStopId(), RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		return result;
	}

}
