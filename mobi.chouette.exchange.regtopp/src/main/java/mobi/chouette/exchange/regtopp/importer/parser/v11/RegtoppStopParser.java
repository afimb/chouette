package mobi.chouette.exchange.regtopp.importer.parser.v11;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;
import static mobi.chouette.exchange.regtopp.validation.Constant.REGTOPP_FILE_HPL;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.parser.AbstractConverter;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppStopHPL;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.Coordinate;
import mobi.chouette.model.util.CoordinateUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppStopParser implements Parser, Validator {

	@Override
	public void parse(Context context) throws Exception {
		try {

			Referential referential = (Referential) context.get(REFERENTIAL);
			RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
			RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

			for (AbstractRegtoppStopHPL stop : importer.getStopById()) {
				convertAndAddStopArea(referential, configuration, stop);
			}
		} catch (Exception e) {
			log.error("Error parsing StopArea", e);
			throw e;
		}
	}

	protected StopArea convertAndAddStopArea(Referential referential, RegtoppImportParameters configuration, AbstractRegtoppStopHPL stop) {
		String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), StopArea.STOPAREA_KEY, stop.getFullStopId());
		StopArea stopArea = ObjectFactory.getStopArea(referential, objectId);

		Coordinate wgs84Coordinate = CoordinateUtil.transform(configuration.getCoordinateProjection(), Coordinate.WGS84,
				new Coordinate(stop.getX(), stop.getY()));

		stopArea.setLongitude(wgs84Coordinate.getY());
		stopArea.setLatitude(wgs84Coordinate.getX());
		stopArea.setLongLatType(LongLatTypeEnum.WGS84);

		// UTM coordinates
		stopArea.setX(stop.getX());
		stopArea.setY(stop.getY());
		stopArea.setProjectionType(configuration.getCoordinateProjection());

		stopArea.setName(stop.getFullName());
		stopArea.setRegistrationNumber(stop.getShortName());

		// TODO set correct, some stops are in other countries
		// Could use a reverse geocoder for this, would obtain address etc etc.
		// stopArea.setCountryCode("NO");

		stopArea.setAreaType(ChouetteAreaEnum.BoardingPosition);
		return stopArea;
	}

	@Override
	public void validate(Context context) throws Exception {

		// Konsistenssjekker, kjøres før parse-metode
		try {

			RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
			RegtoppValidationReporter validationReporter = (RegtoppValidationReporter) context.get(RegtoppConstant.REGTOPP_REPORTER);
			validationReporter.getExceptions().clear();

			// ValidationReport mainReporter = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);

			validateHPLIndex(context, importer, validationReporter);
		} catch (Exception e) {
			log.error("Error validating HPL:", e);
			throw e;
		}
	}

	private void validateHPLIndex(Context context, RegtoppImporter importer, RegtoppValidationReporter validationReporter) throws Exception {
		if (importer.hasHPLImporter()) {
			validationReporter.reportSuccess(context, REGTOPP_FILE_HPL, AbstractRegtoppStopHPL.FILE_EXTENSION);

			Index<AbstractRegtoppStopHPL> index = importer.getStopById();

			if (index.getLength() == 0) {
				FileParserValidationError fileError = new FileParserValidationError(AbstractRegtoppStopHPL.FILE_EXTENSION, 0, null,
						RegtoppException.ERROR.FILE_WITH_NO_ENTRY, null, "Empty file");
				validationReporter.reportError(context, new RegtoppException(fileError), AbstractRegtoppStopHPL.FILE_EXTENSION);
			}

			for (AbstractRegtoppStopHPL bean : index) {
				try {
					// Call index validator
					index.validate(bean, importer);
				} catch (Exception ex) {
					log.error(ex, ex);
					if (ex instanceof RegtoppException) {
						validationReporter.reportError(context, (RegtoppException) ex, AbstractRegtoppStopHPL.FILE_EXTENSION);
					} else {
						validationReporter.throwUnknownError(context, ex, AbstractRegtoppStopHPL.FILE_EXTENSION);
					}
				}
				validationReporter.reportErrors(context, bean.getErrors(), AbstractRegtoppStopHPL.FILE_EXTENSION);
				validationReporter.validate(context, AbstractRegtoppStopHPL.FILE_EXTENSION, bean.getOkTests());
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
