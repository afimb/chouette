package mobi.chouette.exchange.regtopp.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.importer.index.Index;
import mobi.chouette.exchange.regtopp.validation.Constant;
import mobi.chouette.exchange.regtopp.validation.ValidationReporter;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppStopParser implements Parser, Validator, Constant {
	
	@Override
	public void validate(Context context) throws Exception {
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		ValidationReporter validationReporter = (ValidationReporter) context.get(REGTOPP_REPORTER);
		validationReporter.getExceptions().clear();
		
		// stops.txt
		if (importer.hasStopImporter()) { // the file "*.hpl" exists ?
			validationReporter.reportSuccess(context, REGTOPP_FILE_HPL, RegtoppStopHPL.FILE_EXTENSION);

			
			// Parse HPL file
			
			// Build index
			
			// Iterate over and validate pure content
			
			
			Index<RegtoppStopHPL> parser = null;
			try { // Read and check the header line of the file "stops.txt"
				parser = importer.getStopById(); 
			} catch (Exception ex ) {
				if (ex instanceof RegtoppException) {
					validationReporter.reportError(context, (RegtoppException)ex, RegtoppStopHPL.FILE_EXTENSION);
				} else {
					validationReporter.throwUnknownError(context, ex, RegtoppStopHPL.FILE_EXTENSION);
				}
			}

		//	validationReporter.validateOkCSV(context, RegtoppStopHPL.FILE_EXTENSION);
		
			if (parser == null) { // importer.getStopById() fails for any other reason
				validationReporter.throwUnknownError(context, new Exception("Cannot instantiate StopById class"), RegtoppStopHPL.FILE_EXTENSION);
			} else {
				validationReporter.validate(context, RegtoppStopHPL.FILE_EXTENSION, parser.getOkTests());
				validationReporter.validateUnknownError(context);
			}
			
			if (!parser.getErrors().isEmpty()) {
				validationReporter.reportErrors(context, parser.getErrors(), RegtoppStopHPL.FILE_EXTENSION);
				parser.getErrors().clear();
			}
			
		//	validationReporter.validateOKGeneralSyntax(context, RegtoppStopHPL.FILE_EXTENSION);
		
			if (parser.getLength() == 0) {
//				validationReporter.reportError(context, new RegtoppException(RegtoppStopHPL.FILE_EXTENSION, 1, null, RegtoppException.ERROR.FILE_WITH_NO_ENTRY, null, null), RegtoppStopHPL.FILE_EXTENSION);
			} else {
		//		validationReporter.validate(context, RegtoppStopHPL.FILE_EXTENSION, RegtoppException.ERROR.FILE_WITH_NO_ENTRY);
			}
		
			RegtoppException fatalException = null;
			boolean hasLocationType = false;
			parser.setWithValidation(true);
			for (RegtoppStopHPL bean : parser) {
				try {
//					if (bean.getLocationType() == null)
//						bean.setLocationType(LocationType.Stop);
//					else
//						hasLocationType = true;
					parser.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof RegtoppException) {
						validationReporter.reportError(context, (RegtoppException)ex, RegtoppStopHPL.FILE_EXTENSION);
					} else {
						validationReporter.throwUnknownError(context, ex, RegtoppStopHPL.FILE_EXTENSION);
					}
				}
				for(RegtoppException ex : bean.getErrors()) {
					if (ex.isFatal())
						fatalException = ex;
				}
				validationReporter.reportErrors(context, bean.getErrors(), RegtoppStopHPL.FILE_EXTENSION);
				validationReporter.validate(context, RegtoppStopHPL.FILE_EXTENSION, bean.getOkTests());
			}
			parser.setWithValidation(false);
//			if (hasLocationType)
//				validationReporter.validate(context, RegtoppStopHPL.FILE_EXTENSION, RegtoppException.ERROR.NO_LOCATION_TYPE);
//			else
//				validationReporter.reportError(context, new RegtoppException(RegtoppStopHPL.FILE_EXTENSION, 1, null, RegtoppException.ERROR.NO_LOCATION_TYPE, null, null), RegtoppStopHPL.FILE_EXTENSION);
			if (fatalException != null)
				throw fatalException;
		} else {
//			validationReporter.reportError(context, new RegtoppException(RegtoppStopHPL.FILE_EXTENSION, 1, null, RegtoppException.ERROR.MISSING_MANDATORY_FILES, null, null), RegtoppStopHPL.FILE_EXTENSION);
		}
	}	
	
	@Override
	public void parse(Context context) throws Exception {

		Referential referential = (Referential) context.get(REFERENTIAL);
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		for (RegtoppStopHPL gtfsStop : importer.getStopById()) {
			String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
					StopArea.STOPAREA_KEY, gtfsStop.getStopId(), log);

			StopArea stopArea = ObjectFactory.getStopArea(referential, objectId);
			convert(context, gtfsStop, stopArea);
		}
	}
	
	protected void convert(Context context, RegtoppStopHPL gtfsStop, StopArea stopArea) {
		Referential referential = (Referential) context.get(REFERENTIAL);
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		// TODO convert to WGS
		stopArea.setLatitude(gtfsStop.getStopLat());
		stopArea.setLongitude(gtfsStop.getStopLon());
		stopArea.setLongLatType(LongLatTypeEnum.WGS84);
		stopArea.setName(AbstractConverter.getNonEmptyTrimedString(gtfsStop.getFullName()));
		
		// TODO
		stopArea.setAreaType(ChouetteAreaEnum.BoardingPosition);

		//stopArea.setUrl(AbstractConverter.toString(gtfsStop.getStopUrl()));
		//stopArea.setComment(AbstractConverter.getNonEmptyTrimedString(gtfsStop.getStopDesc()));
		//stopArea.setTimeZone(AbstractConverter.toString(gtfsStop.getStopTimezone()));
		
		// TODO stopArea.setFareCode(0);

//		if (gtfsStop.getLocationType() == GtfsStop.LocationType.Station) {
//			stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
//			if (AbstractConverter.getNonEmptyTrimedString(gtfsStop.getParentStation()) != null) {
//				// TODO report
//			}
//		} else {
//			if (!importer.getStopById().containsKey(gtfsStop.getParentStation())) {
//				// TODO report
//			}
//			stopArea.setAreaType(ChouetteAreaEnum.BoardingPosition);
//			if (gtfsStop.getParentStation() != null) {
//				String parenId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
//						StopArea.STOPAREA_KEY, gtfsStop.getParentStation(), log);
//				StopArea parent = ObjectFactory.getStopArea(referential, parenId);
//				stopArea.setParent(parent);
//			}
//		}

		//stopArea.setRegistrationNumber(gtfsStop.getStopCode());
		//stopArea.setMobilityRestrictedSuitable(WheelchairBoardingType.Allowed.equals(gtfsStop.getWheelchairBoarding()));
		//stopArea.setStreetName(gtfsStop.getAddressLine());
		//stopArea.setCityName(gtfsStop.getLocality());
		//stopArea.setZipCode(gtfsStop.getPostalCode());
		stopArea.setFilled(true);

	}
	
	static {
		ParserFactory.register(RegtoppStopParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppStopParser();
			}
		});
	}
}
