package mobi.chouette.exchange.regtopp.importer.index.v11;

import static mobi.chouette.exchange.regtopp.RegtoppConstant.DESTINATION_NULL_REF;
import static mobi.chouette.exchange.regtopp.RegtoppConstant.FOOTNOTE_NULL_REF;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public abstract class TripIndex extends IndexImpl<AbstractRegtoppTripIndexTIX> {

	public TripIndex(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	@Override
	public boolean validate(AbstractRegtoppTripIndexTIX bean, RegtoppImporter dao) {
		boolean result = true;

		//Obligatoriske felter
		if (bean.getAdminCode() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(), "Adminkode", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getCounter() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(), "Løpenr", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getLineId() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(), "Linjenr", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getTripId() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(), "Turnr", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getDirection() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(), "Retning", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getRouteIdRef() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(), "Trmønsternr", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getDepartureTime() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(), "Avgangstid", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}


		if (dao.hasTMSImporter()) {
			if (dao.getRouteByRouteKey().containsKey(bean.getRouteKey())) { // Referanse fra TURIX til TURMSTR er feltene Linjenr, Retning og Turmønsternr
				bean.getOkTests().add(RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE);
			} else {
				bean.getErrors()
						.add(new RegtoppException(new FileParserValidationError(AbstractRegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(),
								"Rutereferanse (kombinasjonen linjenr, retning og turmønsternr)", bean.getRouteKey(),
								RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE, "Unreferenced id.")));
				result = false;
			}
		}

		if (dao.getDayCodeById().containsKey(bean.getDayCodeRef())) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(),
					"Dagkodenr", bean.getDayCodeRef(), RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		// One of destinations may be a null ref
		if (bean instanceof mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX) {
			// Regtopp 1.2
			mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX tix = (RegtoppTripIndexTIX) bean;

			if (tix.getDestinationIdDepartureRef().equals(DESTINATION_NULL_REF) && tix.getDestinationIdArrivalRef().equals(DESTINATION_NULL_REF)) {
				bean.getErrors()
						.add(new RegtoppException(new FileParserValidationError(AbstractRegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(),
								"Enten Destinasjonsnr (avgang) eller Destinasjonsnr (ankomst) må være satt", DESTINATION_NULL_REF,
								RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE, "Unreferenced id.")));
				result = false;
			} else {
				if (bean.getDestinationIdDepartureRef().equals(DESTINATION_NULL_REF)
						|| dao.getDestinationById().containsKey(bean.getDestinationIdDepartureRef())) {
					bean.getOkTests().add(RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE);
				} else {
					bean.getErrors()
							.add(new RegtoppException(new FileParserValidationError(AbstractRegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(),
									"Destinasjonsnr (avgang)", bean.getDestinationIdDepartureRef(), RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE,
									"Unreferenced id.")));
					result = false;
				}

				if (tix.getDestinationIdArrivalRef().equals(DESTINATION_NULL_REF) || dao.getDestinationById().containsKey(tix.getDestinationIdArrivalRef())) {
					bean.getOkTests().add(RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE);
				} else {
					bean.getErrors()
							.add(new RegtoppException(new FileParserValidationError(AbstractRegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(),
									"Destinasjonsnr (ankomst)", tix.getDestinationIdArrivalRef(), RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE,
									"Unreferenced id.")));
					result = false;
				}
			}
		} else {
			// Regtopp 1.1D
			// TODO
		}

		if (bean.getFootnoteId1Ref().equals(FOOTNOTE_NULL_REF) || dao.getFootnoteById().containsKey(bean.getFootnoteId1Ref())) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(),
					"Merknadsnr (1)", bean.getFootnoteId1Ref(), RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		if (bean.getFootnoteId2Ref().equals(FOOTNOTE_NULL_REF) || dao.getFootnoteById().containsKey(bean.getFootnoteId2Ref())) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppTripIndexTIX.FILE_EXTENSION, bean.getRecordLineNumber(),
					"Merknadsnr (2)", bean.getFootnoteId2Ref(), RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		return result;

	}

}
