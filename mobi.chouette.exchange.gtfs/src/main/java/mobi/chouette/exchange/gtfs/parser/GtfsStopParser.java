package mobi.chouette.exchange.gtfs.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStop.LocationType;
import mobi.chouette.exchange.gtfs.model.GtfsStop.WheelchairBoardingType;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.gtfs.validation.Constant;
import mobi.chouette.exchange.gtfs.validation.ValidationReporter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsStopParser implements Parser, Validator, Constant {
	
	@Override
	public void validate(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);
		validationReporter.getExceptions().clear();
		
		// stops.txt
		// log.info("validating stops");
		if (importer.hasStopImporter()) { // the file "stops.txt" exists ?
			validationReporter.reportSuccess(context, GTFS_1_GTFS_Common_1, GTFS_STOPS_FILE);

			Index<GtfsStop> parser = null;
			try { // Read and check the header line of the file "stops.txt"
				parser = importer.getStopById(); 
			} catch (Exception ex ) {
				if (ex instanceof GtfsException) {
					validationReporter.reportError(context, (GtfsException)ex, GTFS_STOPS_FILE);
				} else {
					validationReporter.throwUnknownError(context, ex, GTFS_STOPS_FILE);
				}
			}

			validationReporter.validateOkCSV(context, GTFS_STOPS_FILE);
		
			if (parser == null) { // importer.getStopById() fails for any other reason
				validationReporter.throwUnknownError(context, new Exception("Cannot instantiate StopById class"), GTFS_STOPS_FILE);
			} else {
				validationReporter.validate(context, GTFS_STOPS_FILE, parser.getOkTests());
				validationReporter.validateUnknownError(context);
			}
			
			if (!parser.getErrors().isEmpty()) {
				validationReporter.reportErrors(context, parser.getErrors(), GTFS_STOPS_FILE);
				parser.getErrors().clear();
			}
			
			validationReporter.validateOKGeneralSyntax(context, GTFS_STOPS_FILE);
		
			if (parser.getLength() == 0) {
				validationReporter.reportError(context, new GtfsException(GTFS_STOPS_FILE, 1, null, GtfsException.ERROR.FILE_WITH_NO_ENTRY, null, null), GTFS_STOPS_FILE);
			} else {
				validationReporter.validate(context, GTFS_STOPS_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			}
		
			GtfsException fatalException = null;
			boolean hasLocationType = false;
			parser.setWithValidation(true);
			for (GtfsStop bean : parser) {
				try {
					if (bean.getLocationType() == null)
						bean.setLocationType(LocationType.Stop);
					else
						hasLocationType = true;
					parser.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof GtfsException) {
						validationReporter.reportError(context, (GtfsException)ex, GTFS_STOPS_FILE);
					} else {
						validationReporter.throwUnknownError(context, ex, GTFS_STOPS_FILE);
					}
				}
				for(GtfsException ex : bean.getErrors()) {
					if (ex.isFatal())
						fatalException = ex;
				}
				validationReporter.reportErrors(context, bean.getErrors(), GTFS_STOPS_FILE);
				validationReporter.validate(context, GTFS_STOPS_FILE, bean.getOkTests());
			}
			parser.setWithValidation(false);
			if (hasLocationType)
				validationReporter.validate(context, GTFS_STOPS_FILE, GtfsException.ERROR.NO_LOCATION_TYPE);
			else
				validationReporter.reportError(context, new GtfsException(GTFS_STOPS_FILE, 1, null, GtfsException.ERROR.NO_LOCATION_TYPE, null, null), GTFS_STOPS_FILE);
			if (fatalException != null)
				throw fatalException;
		} else {
			validationReporter.reportError(context, new GtfsException(GTFS_STOPS_FILE, 1, null, GtfsException.ERROR.MISSING_FILE, null, null), GTFS_STOPS_FILE);
		}
	}	
	
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
		AbstractConverter.addLocation(context, "stops.txt", stopArea.getObjectId(), gtfsStop.getId());
	}
	
	static {
		ParserFactory.register(GtfsStopParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new GtfsStopParser();
			}
		});
	}
}
