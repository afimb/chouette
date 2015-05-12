package mobi.chouette.exchange.gtfs.parser;

import java.sql.Time;
import java.util.Calendar;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsTransfer;
import mobi.chouette.exchange.gtfs.model.GtfsTransfer.TransferType;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ConnectionLinkTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsTransferParser implements Parser, Validator, Constant {

	private Referential referential;
	private GtfsImporter importer;
	private GtfsImportParameters configuration;

	@Override
	public void parse(Context context) throws Exception {

		referential = (Referential) context.get(REFERENTIAL);
		importer = (GtfsImporter) context.get(PARSER);
		configuration = (GtfsImportParameters) context.get(CONFIGURATION);

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

		importer = (GtfsImporter) context.get(PARSER);
		ActionReport report = (ActionReport) context.get(REPORT);

		// transfers.txt
		FileInfo file = new FileInfo(GTFS_TRANSFERS_FILE,FILE_STATE.OK);
		report.getFiles().add(file);
		try {
			if (importer.hasFrequencyImporter()) {
				Index<GtfsTransfer> parser = importer.getTransferByFromStop();
				for (GtfsTransfer bean : parser) {
					parser.validate(bean, importer);
				}
			}
		} catch (Exception ex) {
			AbstractConverter.populateFileError(file, ex);
			throw ex;
		}
	}

	protected void convert(Context context, GtfsTransfer gtfsTransfer, ConnectionLink connectionLink) {

		Referential referential = (Referential) context.get(REFERENTIAL);

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
	}

	static {
		ParserFactory.register(GtfsTransferParser.class.getName(), new ParserFactory() {
			private GtfsTransferParser instance = new GtfsTransferParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
