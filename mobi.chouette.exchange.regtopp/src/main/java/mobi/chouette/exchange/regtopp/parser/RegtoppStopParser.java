package mobi.chouette.exchange.regtopp.parser;

import org.apache.commons.lang.StringUtils;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.importer.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.importer.index.Index;
import mobi.chouette.exchange.regtopp.validation.Constant;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppStopParser implements Parser, Validator, Constant {

	@Override
	public void parse(Context context) throws Exception {

		Referential referential = (Referential) context.get(REFERENTIAL);
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		for (RegtoppStopHPL regtoppStop : importer.getStopById()) {
			String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), StopArea.STOPAREA_KEY, regtoppStop.getStopId(), log);

			StopArea stopArea = ObjectFactory.getStopArea(referential, objectId);
			convert(context, regtoppStop, stopArea);
		}
	}

	protected void convert(Context context, RegtoppStopHPL regtoppStop, StopArea stopArea) {
		Referential referential = (Referential) context.get(REFERENTIAL);
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		// TODO convert to WGS
		stopArea.setLatitude(regtoppStop.getStopLat());
		stopArea.setLongitude(regtoppStop.getStopLon());
		stopArea.setLongLatType(LongLatTypeEnum.WGS84);
		stopArea.setName(StringUtils.trimToEmpty(regtoppStop.getFullName()));

		// TODO
		stopArea.setAreaType(ChouetteAreaEnum.BoardingPosition);

		// stopArea.setUrl(AbstractConverter.toString(gtfsStop.getStopUrl()));
		// stopArea.setComment(AbstractConverter.getNonEmptyTrimedString(gtfsStop.getStopDesc()));
		// stopArea.setTimeZone(AbstractConverter.toString(gtfsStop.getStopTimezone()));

		// TODO stopArea.setFareCode(0);

		// if (gtfsStop.getLocationType() == GtfsStop.LocationType.Station) {
		// stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
		// if (AbstractConverter.getNonEmptyTrimedString(gtfsStop.getParentStation()) != null) {
		// // TODO report
		// }
		// } else {
		// if (!importer.getStopById().containsKey(gtfsStop.getParentStation())) {
		// // TODO report
		// }
		// stopArea.setAreaType(ChouetteAreaEnum.BoardingPosition);
		// if (gtfsStop.getParentStation() != null) {
		// String parenId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
		// StopArea.STOPAREA_KEY, gtfsStop.getParentStation(), log);
		// StopArea parent = ObjectFactory.getStopArea(referential, parenId);
		// stopArea.setParent(parent);
		// }
		// }

		// stopArea.setRegistrationNumber(gtfsStop.getStopCode());
		// stopArea.setMobilityRestrictedSuitable(WheelchairBoardingType.Allowed.equals(gtfsStop.getWheelchairBoarding()));
		// stopArea.setStreetName(gtfsStop.getAddressLine());
		// stopArea.setCityName(gtfsStop.getLocality());
		// stopArea.setZipCode(gtfsStop.getPostalCode());
		stopArea.setFilled(true);

	}

	@Override
	public void validate(Context context) throws Exception {

		// Konsistenssjekker, kjøres før parse-metode

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppValidationReporter validationReporter = (RegtoppValidationReporter) context.get(REGTOPP_REPORTER);
		validationReporter.getExceptions().clear();

		ValidationReport mainReporter = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);

		mainReporter.getCheckPoints().add(new CheckPoint(REGTOPP_FILE_HPL, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));

		// stops.txt
		if (importer.hasHPLImporter()) { // the file "*.hpl" exists ?
			validationReporter.reportSuccess(context, REGTOPP_FILE_HPL, RegtoppStopHPL.FILE_EXTENSION);

			Index<RegtoppStopHPL> index = importer.getStopById();

			if (index.getLength() == 0) {
				FileParserValidationError fileError = new FileParserValidationError(RegtoppStopHPL.FILE_EXTENSION, 0, null,
						RegtoppException.ERROR.FILE_WITH_NO_ENTRY, null, "Empty file");
				validationReporter.reportError(context, new RegtoppException(fileError), RegtoppStopHPL.FILE_EXTENSION);
			}

			for (RegtoppStopHPL bean : index) {
				try {
					// Call index validator
					index.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof RegtoppException) {
						validationReporter.reportError(context, (RegtoppException) ex, RegtoppStopHPL.FILE_EXTENSION);
					} else {
						validationReporter.throwUnknownError(context, ex, RegtoppStopHPL.FILE_EXTENSION);
					}
				}
			}
		}
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
