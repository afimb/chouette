package mobi.chouette.exchange.regtopp.importer.index.v12;

import static mobi.chouette.exchange.regtopp.RegtoppConstant.DESTINATION_NULL_REF;
import static mobi.chouette.exchange.regtopp.RegtoppConstant.FOOTNOTE_NULL_REF;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class RouteByIndexingKey extends  IndexImpl<AbstractRegtoppRouteTMS> {

	public RouteByIndexingKey(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new RouteByIndexingKey(validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(RouteByIndexingKey.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
		for (Object obj : parser.getRawContent()) {
			AbstractRegtoppRouteTMS newRecord = (AbstractRegtoppRouteTMS) obj;
			AbstractRegtoppRouteTMS existingRecord = index.put(newRecord.getIndexingKey(), newRecord);
			if (existingRecord != null) {
				log.error("Duplicate key in TMS file. Existing: "+existingRecord+" Ignored duplicate: "+newRecord);
				validationReporter.reportError(new Context(), new RegtoppException(new FileParserValidationError(RegtoppRouteTMS.FILE_EXTENSION,
						newRecord.getRecordLineNumber(), "Linjenr/Turmønsternr/Retning/Sekvensnummer", newRecord.getIndexingKey(), ERROR.DUPLICATE_KEY, "Duplicate key")), null);
			}
		}
	}
	
	@Override
	public boolean validate(AbstractRegtoppRouteTMS bean, RegtoppImporter dao) throws Exception {
		boolean result = true;

		//Obligatoriske felter
		if (bean.getAdminCode() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Adminkode", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getCounter() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Løpenr", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getLineId() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Linjenr", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getDirection() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Retning", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getRouteId() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Turmønsternr", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getSequenceNumberStop() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Sekvensnr hpl", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getStopId() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Holdeplassnr", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		//Minst en
		if (bean.getDriverTimeArrival() != null || bean.getDriverTimeDeparture() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Kjøretid", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getDestinationId().equals(DESTINATION_NULL_REF) || dao.getDestinationById().containsKey(bean.getDestinationId())){
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Destinasjonsnr", bean.getDestinationId(), RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		if (bean.getRemarkId().equals(FOOTNOTE_NULL_REF) || dao.getFootnoteById().containsKey(bean.getRemarkId())){
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Merknadssnr", bean.getRemarkId(), RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		if (dao.getStopById().containsKey(bean.getFullStopId())){
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Holdeplassnr/stoppunktnr avg.", bean.getFullStopId(), RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}


		return result;
	}

}
