package mobi.chouette.exchange.regtopp.importer.index.v11;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.enums.TransportType;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

import static mobi.chouette.exchange.regtopp.messages.RegtoppMessages.getMessage;

@Log4j
public abstract class TripIndex extends IndexImpl<AbstractRegtoppTripIndexTIX> {

	public TripIndex(Context context, RegtoppValidationReporter validationReporter, FileContentParser<AbstractRegtoppTripIndexTIX> fileParser) throws Exception {
		super(context, validationReporter, fileParser);
	}

	@Override
	public boolean validate(AbstractRegtoppTripIndexTIX bean, RegtoppImporter dao) throws Exception {
		boolean result = true;

		//Mandatory fields
		if (isNotNull(bean.getAdminCode())) {
			bean.getOkTests().add(RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppTripIndexTIX.adminCode"), bean.getAdminCode(), RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

//		if (isNotNull(bean.getCounter())) {
//			bean.getOkTests().add(RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE);
//		}  else {
//			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppTripIndexTIX.counter"), bean.getCounter(), RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
//			result = false;
//		}

		if (isNotNull(bean.getLineId())) {
			bean.getOkTests().add(RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppTripIndexTIX.lineId"), bean.getLineId(), RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (isNotNull(bean.getTripId())) {
			bean.getOkTests().add(RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppTripIndexTIX.tripId"), bean.getTripId(), RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (isNotNull(bean.getRouteIdRef())) {
			bean.getOkTests().add(RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppTripIndexTIX.routeIdRef"), bean.getRouteIdRef(), RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (bean.getTypeOfService() != TransportType.Unknown) {
			bean.getOkTests().add(RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppTripIndexTIX.typeOfService"), TransportType.Unknown, RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (dao.getDayCodeById().containsKey(bean.getDayCodeRef())) {
			bean.getOkTests().add(RegtoppException.ERROR.TIX_INVALID_MANDATORY_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(),
					getMessage("label.regtoppTripIndexTIX.dayCodeRef"), bean.getDayCodeRef(), RegtoppException.ERROR.TIX_INVALID_MANDATORY_ID_REFERENCE, getMessage("label.validation.invalidMandatoryReference"))));
			result = false;
		}

		// One of destinations may be a null ref
		if (bean instanceof mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX) {
			// Regtopp 1.2
			mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX tix = (RegtoppTripIndexTIX) bean;

			
			if (isNull(tix.getDestinationIdDepartureRef()) && isNull(tix.getDestinationIdArrivalRef())) {
				String errorMessage = getMessage("label.validation.oneOf") + ": " + getMessage("label.regtoppTripIndexTIX.destinationIdDepartureRef") + ", " + getMessage("label.regtoppTripIndexTIX.destinationIdArrivalRef");
				bean.getErrors()
						.add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(),
								getMessage("label.regtoppTripIndexTIX.destinationIdDepartureRef") + ", " + getMessage("label.regtoppTripIndexTIX.destinationIdArrivalRef")
								, tix.getDestinationIdDepartureRef(), RegtoppException.ERROR.TIX_INVALID_OPTIONAL_ID_REFERENCE, errorMessage)));
				result = false;
			} else {
				if (isNotNull(bean.getDestinationIdDepartureRef())) {
					if(dao.getDestinationById().containsKey(bean.getDestinationIdDepartureRef())) {
						bean.getOkTests().add(RegtoppException.ERROR.TIX_INVALID_OPTIONAL_ID_REFERENCE);
					} else {
						bean.getErrors()
						.add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(),
								getMessage("label.regtoppTripIndexTIX.destinationIdDepartureRef"), bean.getDestinationIdDepartureRef(), RegtoppException.ERROR.TIX_INVALID_OPTIONAL_ID_REFERENCE,
								getMessage("label.validation.invalidOptionalReference"))));
						result = false;
					}
				}
				if (isNotNull(tix.getDestinationIdArrivalRef())) {
					if(dao.getDestinationById().containsKey(tix.getDestinationIdArrivalRef())) {
						bean.getOkTests().add(RegtoppException.ERROR.TIX_INVALID_OPTIONAL_ID_REFERENCE);
					} else {
						bean.getErrors()
						.add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(),
								getMessage("label.regtoppTripIndexTIX.destinationIdArrivalRef"), tix.getDestinationIdArrivalRef(), RegtoppException.ERROR.TIX_INVALID_OPTIONAL_ID_REFERENCE,
								getMessage("label.validation.invalidOptionalReference"))));
						result = false;
					}
				}
			}
		} else {
			// Regtopp 1.1D
			// TODO
		}

		if (isNotNull(bean.getFootnoteId1Ref())) {
			if (dao.getFootnoteById().containsKey(bean.getFootnoteId1Ref())) {
				bean.getOkTests().add(RegtoppException.ERROR.TIX_INVALID_OPTIONAL_ID_REFERENCE);
			} else {
				bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(),
						getMessage("label.regtoppTripIndexTIX.footnoteId1Ref"), bean.getFootnoteId1Ref(), RegtoppException.ERROR.TIX_INVALID_OPTIONAL_ID_REFERENCE, getMessage("label.validation.invalidOptionalReference"))));
				result = false;
			}
		}

		if (isNotNull(bean.getFootnoteId2Ref())) {
			if (dao.getFootnoteById().containsKey(bean.getFootnoteId2Ref())) {
				bean.getOkTests().add(RegtoppException.ERROR.TIX_INVALID_OPTIONAL_ID_REFERENCE);
			} else {
				bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(),
						getMessage("label.regtoppTripIndexTIX.footnoteId2Ref"), bean.getFootnoteId2Ref(), RegtoppException.ERROR.TIX_INVALID_OPTIONAL_ID_REFERENCE, getMessage("label.validation.invalidOptionalReference"))));
				result = false;
			}
		}

		return result;

	}

}
