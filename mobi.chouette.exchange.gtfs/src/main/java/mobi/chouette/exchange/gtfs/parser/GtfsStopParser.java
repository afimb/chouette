package mobi.chouette.exchange.gtfs.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStop.WheelchairBoardingType;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsStopParser implements Parser, Validator, Constant {

	private Referential referential;
	private GtfsImporter importer;
	private GtfsImportParameters configuration;

	@Override
	public void parse(Context context) throws Exception {

		referential = (Referential) context.get(REFERENTIAL);
		importer = (GtfsImporter) context.get(PARSER);
		configuration = (GtfsImportParameters) context.get(CONFIGURATION);

		for (GtfsStop gtfsStop : importer.getStopById()) {
			if (gtfsStop.getLocationType() != GtfsStop.LocationType.Access) {

				String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
						StopArea.STOPAREA_KEY, gtfsStop.getStopId(), log);

				StopArea stopArea = ObjectFactory.getStopArea(referential, objectId);
				convert(context, gtfsStop, stopArea);

			}
		}
	}

	@Override
	public void validate(Context context) throws Exception {

		importer = (GtfsImporter) context.get(PARSER);
		ActionReport report = (ActionReport) context.get(REPORT);

		// stops.txt
		FileInfo file = new FileInfo(GTFS_STOPS_FILE, FILE_STATE.OK);
		report.getFiles().add(file);
		try {
			Index<GtfsStop> parser = importer.getStopById();
			for (GtfsStop bean : parser) {
				parser.validate(bean, importer);
			}
		} catch (Exception ex) {
			AbstractConverter.populateFileError(file, ex);
			throw ex;
		}
	}

	protected void convert(Context context, GtfsStop gtfsStop, StopArea stopArea) {
		Referential referential = (Referential) context.get(REFERENTIAL);

		stopArea.setLatitude(gtfsStop.getStopLat());
		stopArea.setLongitude(gtfsStop.getStopLon());
		stopArea.setLongLatType(LongLatTypeEnum.WGS84);
		stopArea.setName(AbstractConverter.getNonEmptyTrimedString(gtfsStop.getStopName()));

		stopArea.setUrl(AbstractConverter.toString(gtfsStop.getStopUrl()));
		stopArea.setComment(AbstractConverter.getNonEmptyTrimedString(gtfsStop.getStopDesc()));
		stopArea.setTimeZone(AbstractConverter.toString(gtfsStop.getStopTimezone()));
		stopArea.setFareCode(0);

		if (gtfsStop.getLocationType() == GtfsStop.LocationType.Station) {
			stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
			if (AbstractConverter.getNonEmptyTrimedString(gtfsStop.getParentStation()) != null) {
				// TODO report
			}
		} else {
			if (!importer.getStopById().containsKey(gtfsStop.getParentStation())) {
				// TODO report
			}
			stopArea.setAreaType(ChouetteAreaEnum.BoardingPosition);
			if (gtfsStop.getParentStation() != null) {
				String parenId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
						StopArea.STOPAREA_KEY, gtfsStop.getParentStation(), log);
				StopArea parent = ObjectFactory.getStopArea(referential, parenId);
				stopArea.setParent(parent);
			}
		}

		stopArea.setRegistrationNumber(gtfsStop.getStopCode());
		stopArea.setMobilityRestrictedSuitable(WheelchairBoardingType.Allowed.equals(gtfsStop.getWheelchairBoarding()));
		stopArea.setStreetName(gtfsStop.getAddressLine());
		stopArea.setCityName(gtfsStop.getLocality());
		stopArea.setZipCode(gtfsStop.getPostalCode());
		stopArea.setFilled(true);

	}

	static {
		ParserFactory.register(GtfsStopParser.class.getName(), new ParserFactory() {
			private GtfsStopParser instance = new GtfsStopParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
