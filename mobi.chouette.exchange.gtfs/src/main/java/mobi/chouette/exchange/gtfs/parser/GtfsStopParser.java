package mobi.chouette.exchange.gtfs.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.importer.Constant;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStop.WheelchairBoardingType;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsStopParser implements Parser, Validator, Constant {

	@Override
	public void parse(Context context) throws Exception {

		Referential referential = (Referential) context.get(REFERENTIAL);
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);

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
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		// stops.txt
		if (importer.hasStopImporter()) {
			// Add to report
			report.addFileInfo(GTFS_STOPS_FILE, FILE_STATE.OK);
		} else {
			// Add to report
			report.addFileInfo(GTFS_STOPS_FILE, FILE_STATE.ERROR, new FileError(FileError.CODE.FILE_NOT_FOUND, "The file stops.txt must be provided (rule 1-GTFS-Stop-1)"));
			// Add to validation report checkpoint 1-GTFS-Stop-1
			validationReport.addDetail(GTFS_1_GTFS_Stop_1, new Location(GTFS_STOPS_FILE, "stops-failure"), "The file stops.txt must be provided", CheckPoint.RESULT.NOK);
			// Stop parsing and render reports (1-GTFS-Stop-1 is fatal)
			throw new Exception("The file stops.txt must be provided");
		}

		Index<GtfsStop> parser = null;
		try {
			parser = importer.getStopById();
		} catch (Exception ex) {
			if (ex instanceof GtfsException) {
				if ( ((GtfsException) ex).getError() == GtfsException.ERROR.DUPLICATE_FIELD)
					;
				else if ( ((GtfsException) ex).getError() == GtfsException.ERROR.INVALID_FILE_FORMAT)
					;
				else if ( ((GtfsException) ex).getError() == GtfsException.ERROR.INVALID_FORMAT)
					;
				else if ( ((GtfsException) ex).getError() == GtfsException.ERROR.MISSING_FIELD)
					;
				else if ( ((GtfsException) ex).getError() == GtfsException.ERROR.MISSING_FILE)
					;
				else if ( ((GtfsException) ex).getError() == GtfsException.ERROR.MISSING_FOREIGN_KEY)
					;
				else if ( ((GtfsException) ex).getError() == GtfsException.ERROR.SYSTEM)
					;
			}
			mobi.chouette.exchange.gtfs.model.importer.Context exceptionContext = new mobi.chouette.exchange.gtfs.model.importer.Context();
			exceptionContext.put(mobi.chouette.exchange.gtfs.model.importer.Context.CODE, "1-GTFS-Stop-1");
			exceptionContext.put(mobi.chouette.exchange.gtfs.model.importer.Context.ERROR, GtfsException.ERROR.MISSING_FILE);
			GtfsException exception = new GtfsException(exceptionContext, ex);
			AbstractConverter.populateFileError(new FileInfo(GTFS_STOPS_FILE, FILE_STATE.ERROR), exception);
			throw exception;
		}
		if (parser == null || parser.getLength() == 0) {
			mobi.chouette.exchange.gtfs.model.importer.Context exceptionContext = new mobi.chouette.exchange.gtfs.model.importer.Context();
			exceptionContext.put(mobi.chouette.exchange.gtfs.model.importer.Context.CODE, "1-GTFS-Stop-2");
			exceptionContext.put(mobi.chouette.exchange.gtfs.model.importer.Context.ERROR, GtfsException.ERROR.INVALID_FILE_FORMAT);
			GtfsException exception = new GtfsException(exceptionContext);
			AbstractConverter.populateFileError(new FileInfo(GTFS_STOPS_FILE, FILE_STATE.ERROR), exception);
			throw exception;
		}
		try {
			for (GtfsStop bean : parser) {
				parser.validate(bean, importer);
			}
		} catch (Exception ex) {
			AbstractConverter.populateFileError(new FileInfo(GTFS_STOPS_FILE, FILE_STATE.ERROR), ex);
			throw ex;
		}

	}

	protected void convert(Context context, GtfsStop gtfsStop, StopArea stopArea) {
		Referential referential = (Referential) context.get(REFERENTIAL);
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);

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

			@Override
			protected Parser create() {
				return  new GtfsStopParser();

			}
		});
	}

}
