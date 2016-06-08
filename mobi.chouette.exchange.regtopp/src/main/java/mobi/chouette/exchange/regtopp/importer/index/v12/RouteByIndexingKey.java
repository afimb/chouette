package mobi.chouette.exchange.regtopp.importer.index.v12;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static mobi.chouette.exchange.regtopp.messages.RegtoppMessages.getMessage;

@Log4j
public class RouteByIndexingKey extends IndexImpl<AbstractRegtoppRouteTMS> {

	public RouteByIndexingKey(Context context, RegtoppValidationReporter validationReporter, FileContentParser<AbstractRegtoppRouteTMS> fileParser) throws Exception {
		super(context, validationReporter, fileParser);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(Context context, RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new RouteByIndexingKey(context, validationReporter, parser);
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
				String fields = getMessage("label.regtoppRouteTMS.lineId") + "/" + getMessage("label.regtoppRouteTMS.routeId") +
						"/" + getMessage("label.regtoppRouteTMS.direction") + "/" + getMessage("label.regtoppRouteTMS.counter");
				log.warn("Duplicate key in TMS file. Existing: "+existingRecord+" Ignored duplicate: "+newRecord);
				validationReporter.reportError(context, new RegtoppException(new FileParserValidationError(getUnderlyingFilename(),
						newRecord.getRecordLineNumber(), fields, newRecord.getIndexingKey(), ERROR.TMS_DUPLICATE_KEY, getMessage("label.validation.duplicateKeyError"))), getUnderlyingFilename());
			}
		}
	}
	
	@Override
	public boolean validate(AbstractRegtoppRouteTMS bean, RegtoppImporter dao) throws Exception {
		boolean result = true;

		//Mandatory fields
		if (isNotNull(bean.getAdminCode())) {
			bean.getOkTests().add(RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppRouteTMS.adminCode"), bean.getAdminCode(), RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (isNotNull(bean.getCounter())) {
			bean.getOkTests().add(RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppRouteTMS.counter"), bean.getCounter(), RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (isNotNull(bean.getLineId())) {
			bean.getOkTests().add(RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppRouteTMS.lineId"), bean.getLineId(), RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (isNotNull(bean.getRouteId())) {
			bean.getOkTests().add(RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppRouteTMS.routeId"), bean.getRouteId(), RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (isNotNull(bean.getSequenceNumberStop())) {
			bean.getOkTests().add(RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppRouteTMS.sequenceNumberStop"), bean.getSequenceNumberStop(), RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (isNotNull(bean.getStopId())) {
			bean.getOkTests().add(RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppRouteTMS.stopId"), bean.getStopId(), RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (dao.hasHPLImporter()) {
			if (dao.getStopById().containsKey(bean.getFullStopId())) {
				bean.getOkTests().add(ERROR.TMS_INVALID_MANDATORY_ID_REFERENCE);
			} else {
				log.debug("Bean with stopId value of '" + bean.getFullStopId() + "' had no match in index: " + getLogString(dao.getStopById().keys()));
				bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppRouteTMS.stopId"), bean.getFullStopId(), RegtoppException.ERROR.TMS_INVALID_MANDATORY_ID_REFERENCE, getMessage("label.validation.invalidMandatoryReference"))));
				result = false;
			}
		}

		//Minst en
		if (isNotNull(bean.getDriverTimeArrival()) || isNotNull(bean.getDriverTimeDeparture())) {
			bean.getOkTests().add(RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE);
		}  else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppRouteTMS.driverTimeArrival"), bean.getDriverTimeArrival() + "/" + bean.getDriverTimeDeparture(), RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (isNotNull(bean.getDestinationId()) || dao.getDestinationById().containsKey(bean.getDestinationId())){
			bean.getOkTests().add(RegtoppException.ERROR.TMS_INVALID_OPTIONAL_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppRouteTMS.destinationId"), bean.getDestinationId(), RegtoppException.ERROR.TMS_INVALID_OPTIONAL_ID_REFERENCE, getMessage("label.validation.invalidOptionalReference"))));
			result = false;
		}

		if (isNotNull(bean.getRemarkId()) || dao.getFootnoteById().containsKey(bean.getRemarkId())){
			bean.getOkTests().add(RegtoppException.ERROR.TMS_INVALID_OPTIONAL_ID_REFERENCE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(), getMessage("label.regtoppRouteTMS.remarkId"), bean.getRemarkId(), RegtoppException.ERROR.TMS_INVALID_OPTIONAL_ID_REFERENCE, getMessage("label.validation.invalidOptionalReference"))));
			result = false;
		}

		return result;
	}

	// TODO Use Java 8 Stream API when chouette has dropped Java 7 support
	private String getLogString(Iterator<String> it) {
		return Arrays.toString(toList(it).toArray());
	}

	private List<String> toList(Iterator<String> it) {
		List<String> strings = new ArrayList<>();
		while(it.hasNext()) {
			strings.add(it.next());
		}
		return strings;
	}

}
