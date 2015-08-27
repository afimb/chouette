package mobi.chouette.exchange.gtfs.parser;

import java.sql.Time;
import java.util.Calendar;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsTransfer;
import mobi.chouette.exchange.gtfs.model.GtfsTransfer.TransferType;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.gtfs.validation.Constant;
import mobi.chouette.exchange.gtfs.validation.ValidationReporter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ConnectionLinkTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsTransferParser implements Parser, Validator, Constant {

	@Override
	public void parse(Context context) throws Exception {

		Referential referential = (Referential) context.get(REFERENTIAL);
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);

		for (GtfsTransfer gtfsTransfer : importer.getTransferByFromStop()) {

			String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
					ConnectionLink.CONNECTIONLINK_KEY, gtfsTransfer.getFromStopId() + "_" + gtfsTransfer.getToStopId(),
					log);
			ConnectionLink connectionLink = ObjectFactory.getConnectionLink(referential, objectId);
			convert(context, gtfsTransfer, connectionLink);
		}
	}

	@Override
	public void validate(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);
		
		// transfers.txt
		if (importer.hasTransferImporter()) {
			// Add to report
			report.addFileInfo(GTFS_TRANSFERS_FILE, FILE_STATE.OK);
			
			Index<GtfsTransfer> parser = null;
			try { // Read and check the header line of the file "routes.txt"
				parser = importer.getTransferByFromStop();
			} catch (Exception ex ) {
				if (ex instanceof GtfsException) {
					validationReporter.reportError(context, (GtfsException)ex, GTFS_TRANSFERS_FILE);
				} else {
					validationReporter.throwUnknownError(context, ex, GTFS_TRANSFERS_FILE);
				}
			}
		
			if (parser == null || parser.getLength() == 0) { // importer.getTransferByFromStop() fails for any other reason
				validationReporter.throwUnknownError(context, new Exception("Cannot instantiate TransferByFromStop class"), GTFS_TRANSFERS_FILE);
			}
			
			parser.getErrors().clear();
			try {
				for (GtfsTransfer bean : parser) {
					validationReporter.reportErrors(context, bean.getErrors(), GTFS_TRANSFERS_FILE);
					parser.validate(bean, importer);
				}
			} catch (Exception ex) {
				AbstractConverter.populateFileError(new FileInfo(GTFS_TRANSFERS_FILE, FILE_STATE.ERROR), ex);
				throw ex;
			}
		}
	}

	protected void convert(Context context, GtfsTransfer gtfsTransfer, ConnectionLink connectionLink) {

		Referential referential = (Referential) context.get(REFERENTIAL);
		GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);

		StopArea startOfLink = ObjectFactory.getStopArea(referential, AbstractConverter.composeObjectId(
				configuration.getObjectIdPrefix(), StopArea.STOPAREA_KEY, gtfsTransfer.getFromStopId(), log));
		connectionLink.setStartOfLink(startOfLink);
		StopArea endOfLink = ObjectFactory.getStopArea(referential, AbstractConverter.composeObjectId(
				configuration.getObjectIdPrefix(), StopArea.STOPAREA_KEY, gtfsTransfer.getToStopId(), log));
		connectionLink.setEndOfLink(endOfLink);
		connectionLink.setCreationTime(Calendar.getInstance().getTime());
		connectionLink.setLinkType(ConnectionLinkTypeEnum.Overground);
		if (gtfsTransfer.getMinTransferTime() != null) {
			connectionLink.setDefaultDuration(new Time(gtfsTransfer.getMinTransferTime() * 1000));
		}
		if (gtfsTransfer.getTransferType().equals(TransferType.NoAllowed)) {
			connectionLink.setName("FORBIDDEN");
		} else {
			connectionLink.setName("from " + connectionLink.getStartOfLink().getName() + " to "
					+ connectionLink.getEndOfLink().getName());
		}
		connectionLink.setFilled(true);
	}

	static {
		ParserFactory.register(GtfsTransferParser.class.getName(), new ParserFactory() {

			@Override
			protected Parser create() {
				return new GtfsTransferParser();
			}
		});
	}

}
