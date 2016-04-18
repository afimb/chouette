package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public abstract class TripIndex extends IndexImpl<RegtoppTripIndexTIX> {

	public TripIndex(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	@Override
	public boolean validate(RegtoppTripIndexTIX bean, RegtoppImporter dao) {
		boolean result = true;

		if (dao.getDayCodeById().containsKey(bean.getDayCodeRef())){
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(), "Dagkodenr", bean.getDayCodeRef(), RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		if (dao.getRouteIndex().containsKey(bean.getRouteIdRef())){
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(), "Turmønsternr", bean.getRouteIdRef(), RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		if (dao.getDestinationById().containsKey(bean.getDestinationIdDepartureRef())){
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(), "Destinasjonsnr (avgang)", bean.getDestinationIdDepartureRef(), RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		if (dao.getDestinationById().containsKey(bean.getDestinationIdArrivalRef())){
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(), "Destinasjonsnr (ankomst)", bean.getDestinationIdArrivalRef(), RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		if (dao.getFootnoteById().containsKey(bean.getFootnoteId1Ref())){
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(), "Merknadsnr (1)", bean.getFootnoteId1Ref(), RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		if (dao.getFootnoteById().containsKey(bean.getFootnoteId2Ref())){
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(), "Merknadsnr (2)", bean.getFootnoteId2Ref(), RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		return result;

	}

}
