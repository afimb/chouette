package mobi.chouette.exchange.regtopp.importer.index.v12;

import static mobi.chouette.exchange.regtopp.RegtoppConstant.DESTINATION_NULL_REF;
import static mobi.chouette.exchange.regtopp.RegtoppConstant.FOOTNOTE_NULL_REF;

import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

public abstract class RouteIndex extends IndexImpl<AbstractRegtoppRouteTMS> {

	public RouteIndex(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	@Override
	public boolean validate(AbstractRegtoppRouteTMS bean, RegtoppImporter dao) {
		boolean result = true;

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

		if (dao.getStopById().containsKey(bean.getStopId())){
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppRouteTMS.FILE_EXTENSION, bean.getRecordLineNumber(), "Holdeplassnr", bean.getStopId(), RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE, "Unreferenced id.")));
			result = false;
		}

		return result;
	}

}
