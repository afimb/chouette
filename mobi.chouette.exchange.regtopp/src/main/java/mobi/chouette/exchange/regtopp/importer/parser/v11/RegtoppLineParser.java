package mobi.chouette.exchange.regtopp.importer.parser.v11;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.MAIN_VALIDATION_REPORT;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;
import static mobi.chouette.exchange.regtopp.RegtoppConstant.REGTOPP_REPORTER;
import static mobi.chouette.exchange.regtopp.validation.Constant.REGTOPP_FILE_TIX;
import static mobi.chouette.exchange.regtopp.validation.Constant.REGTOPP_FILE_TMS;

import java.sql.Time;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;
import org.joda.time.LocalTime;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.parser.AbstractConverter;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.importer.parser.LineSpecificParser;
import mobi.chouette.exchange.regtopp.importer.version.VersionHandler;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppLineParser extends LineSpecificParser {

	/*
	 * Validation rules of type I and II are checked during this step, and results are stored in reports.
	 */
	// TODO. Rename this function "parse(Context context)".
	@Override
	public void validate(Context context) throws Exception {

		// Konsistenssjekker, kjøres før parse-metode.

		// Det som kan sjekkes her er at antall poster stemmer og at alle referanser til andre filer er gyldige

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppValidationReporter validationReporter = (RegtoppValidationReporter) context.get(REGTOPP_REPORTER);
		validationReporter.getExceptions().clear();

		ValidationReport mainReporter = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);

		validateTMSIndex(context, importer, validationReporter);

		validateTIXIndex(context, importer, validationReporter);
	}

	private void validateTMSIndex(Context context, RegtoppImporter importer, RegtoppValidationReporter validationReporter) throws Exception {
		if (importer.hasTMSImporter()) {
			validationReporter.reportSuccess(context, REGTOPP_FILE_TMS, AbstractRegtoppRouteTMS.FILE_EXTENSION);

			Index<AbstractRegtoppRouteTMS> index = importer.getRouteIndex();

			if (index.getLength() == 0) {
				FileParserValidationError fileError = new FileParserValidationError(AbstractRegtoppRouteTMS.FILE_EXTENSION, 0, null,
						RegtoppException.ERROR.FILE_WITH_NO_ENTRY, null, "Empty file");
				validationReporter.reportError(context, new RegtoppException(fileError), AbstractRegtoppRouteTMS.FILE_EXTENSION);
			}

			for (AbstractRegtoppRouteTMS bean : index) {
				try {
					// Call index validator
					index.validate(bean, importer);
				} catch (Exception ex) {
					log.error(ex);
					if (ex instanceof RegtoppException) {
						validationReporter.reportError(context, (RegtoppException) ex, AbstractRegtoppRouteTMS.FILE_EXTENSION);
					} else {
						validationReporter.throwUnknownError(context, ex, AbstractRegtoppRouteTMS.FILE_EXTENSION);
					}
				}
				validationReporter.reportErrors(context, bean.getErrors(), AbstractRegtoppRouteTMS.FILE_EXTENSION);
				validationReporter.validate(context, AbstractRegtoppRouteTMS.FILE_EXTENSION, bean.getOkTests());
			}
		}
	}

	private void validateTIXIndex(Context context, RegtoppImporter importer, RegtoppValidationReporter validationReporter) throws Exception {
		if (importer.hasTIXImporter()) {
			validationReporter.reportSuccess(context, REGTOPP_FILE_TIX, RegtoppTripIndexTIX.FILE_EXTENSION);

			Index<AbstractRegtoppTripIndexTIX> index = importer.getTripIndex();

			if (index.getLength() == 0) {
				FileParserValidationError fileError = new FileParserValidationError(RegtoppTripIndexTIX.FILE_EXTENSION, 0, null,
						RegtoppException.ERROR.FILE_WITH_NO_ENTRY, null, "Empty file");
				validationReporter.reportError(context, new RegtoppException(fileError), RegtoppTripIndexTIX.FILE_EXTENSION);
			}

			for (AbstractRegtoppTripIndexTIX bean : index) {
				try {
					// Call index validator
					index.validate(bean, importer);
				} catch (Exception ex) {
					log.error(ex);
					if (ex instanceof RegtoppException) {
						validationReporter.reportError(context, (RegtoppException) ex, RegtoppTripIndexTIX.FILE_EXTENSION);
					} else {
						validationReporter.throwUnknownError(context, ex, RegtoppTripIndexTIX.FILE_EXTENSION);
					}
				}
				validationReporter.reportErrors(context, bean.getErrors(), RegtoppTripIndexTIX.FILE_EXTENSION);
				validationReporter.validate(context, RegtoppTripIndexTIX.FILE_EXTENSION, bean.getOkTests());
			}
		}

	}

	/*
	 * Validation rules of type III are checked at this step.
	 */
	// TODO. Rename this function "translate(Context context)" or "produce(Context context)", ...
	@Override
	public void parse(Context context) throws Exception {

		// Her tar vi allerede konsistenssjekkede data (ref validate-metode over) og bygger opp tilsvarende struktur i chouette.
		// Merk at import er linje-sentrisk, så man skal i denne klassen returnerer 1 line med x antall routes og stoppesteder, journeypatterns osv

		Referential referential = (Referential) context.get(REFERENTIAL);

		// Clear any previous data as this referential is reused / TODO
		if (referential != null) {
			referential.clear(true);
		}

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		String chouetteLineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY, lineId);

		// Create the actual Chouette Line and put it in the "referential" space (which is later used by the LineImporterCommand)
		Line line = ObjectFactory.getLine(referential, chouetteLineId);

		// Find line number (TODO check if index exists)
		Index<RegtoppLineLIN> lineById = importer.getLineById();
		RegtoppLineLIN regtoppLine = lineById.getValue(lineId);
		if (regtoppLine != null) {
			line.setName(regtoppLine.getName());
			line.setPublishedName(regtoppLine.getName());
		}

		List<Footnote> footnotes = line.getFootnotes();

		VersionHandler versionHandler = (VersionHandler) context.get(RegtoppConstant.VERSION_HANDLER);

		// Parse Route and JourneyPattern
		LineSpecificParser routeParser = versionHandler.createRouteParser();
		routeParser.setLineId(lineId);
		routeParser.parse(context);
		
		// Parse VehicleJourney
		LineSpecificParser tripParser = versionHandler.createTripParser();
		tripParser.setLineId(lineId);
		tripParser.parse(context);
		

		// Update transport mode for line
		updateLineTransportMode(referential, line);
		
		// Link line to footnotes
		for (Footnote f : footnotes) {
			f.setLine(line);
		}

		// Update boarding/alighting at StopPoint
		updateBoardingAlighting(referential, configuration);

	}

	private void updateLineTransportMode(Referential referential, Line line) {
		Set<TransportModeNameEnum> detectedTransportModes = new HashSet<TransportModeNameEnum>();

		for(VehicleJourney vj : referential.getVehicleJourneys().values()) {
			detectedTransportModes.add(vj.getTransportMode());
		}
		
		if (detectedTransportModes.size() == 1) {
			// Only one transport mode used for all routes/journeys
			line.setTransportModeName(detectedTransportModes.iterator().next());
		} else {
			line.setTransportModeName(TransportModeNameEnum.Other);
			line.setComment("Multiple transport modes: " + StringUtils.join(detectedTransportModes.toArray()));
		}
	}

	public static Time calculateTripVisitTime(Duration tripDepartureTime, Duration timeSinceTripDepatureTime) {
		// TODO Ugly ugly ugly

		LocalTime localTime = new LocalTime(0, 0, 0, 0)
				.plusSeconds((int) (tripDepartureTime.getStandardSeconds() + timeSinceTripDepatureTime.getStandardSeconds()));

		java.sql.Time sqlTime = new java.sql.Time(localTime.getHourOfDay(), localTime.getMinuteOfHour(), localTime.getSecondOfMinute());

		return sqlTime;

	}

	

	private void updateBoardingAlighting(Referential referential, RegtoppImportParameters configuration) {

		for (Route route : referential.getRoutes().values()) {
			boolean invalidData = false;
			boolean usefullData = false;

			b1: for (JourneyPattern jp : route.getJourneyPatterns()) {
				for (VehicleJourney vj : jp.getVehicleJourneys()) {
					for (VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops()) {
						if (!updateStopPoint(vjas)) {
							invalidData = true;
							break b1;
						}
					}
				}
			}
			if (!invalidData) {
				// check if every stoppoints were updated, complete missing ones to
				// normal; if all normal clean all
				for (StopPoint sp : route.getStopPoints()) {
					if (sp.getForAlighting() == null)
						sp.setForAlighting(AlightingPossibilityEnum.normal);
					if (sp.getForBoarding() == null)
						sp.setForBoarding(BoardingPossibilityEnum.normal);
				}
				for (StopPoint sp : route.getStopPoints()) {
					if (!sp.getForAlighting().equals(AlightingPossibilityEnum.normal)) {
						usefullData = true;
						break;
					}
					if (!sp.getForBoarding().equals(BoardingPossibilityEnum.normal)) {
						usefullData = true;
						break;
					}
				}

			}
			if (invalidData || !usefullData) {
				// remove useless informations
				for (StopPoint sp : route.getStopPoints()) {
					sp.setForAlighting(null);
					sp.setForBoarding(null);
				}
			}

		}
	}

	private boolean updateStopPoint(VehicleJourneyAtStop vjas) {
		StopPoint sp = vjas.getStopPoint();
		BoardingPossibilityEnum forBoarding = getForBoarding(vjas.getBoardingAlightingPossibility());
		AlightingPossibilityEnum forAlighting = getForAlighting(vjas.getBoardingAlightingPossibility());
		if (sp.getForBoarding() != null && !sp.getForBoarding().equals(forBoarding))
			return false;
		if (sp.getForAlighting() != null && !sp.getForAlighting().equals(forAlighting))
			return false;
		sp.setForBoarding(forBoarding);
		sp.setForAlighting(forAlighting);
		return true;
	}

	private AlightingPossibilityEnum getForAlighting(BoardingAlightingPossibilityEnum boardingAlightingPossibility) {
		if (boardingAlightingPossibility == null)
			return AlightingPossibilityEnum.normal;
		switch (boardingAlightingPossibility) {
		case BoardAndAlight:
			return AlightingPossibilityEnum.normal;
		case AlightOnly:
			return AlightingPossibilityEnum.normal;
		case BoardOnly:
			return AlightingPossibilityEnum.forbidden;
		case NeitherBoardOrAlight:
			return AlightingPossibilityEnum.forbidden;
		case BoardAndAlightOnRequest:
			return AlightingPossibilityEnum.request_stop;
		case AlightOnRequest:
			return AlightingPossibilityEnum.request_stop;
		case BoardOnRequest:
			return AlightingPossibilityEnum.normal;
		}
		return null;
	}

	private BoardingPossibilityEnum getForBoarding(BoardingAlightingPossibilityEnum boardingAlightingPossibility) {
		if (boardingAlightingPossibility == null)
			return BoardingPossibilityEnum.normal;
		switch (boardingAlightingPossibility) {
		case BoardAndAlight:
			return BoardingPossibilityEnum.normal;
		case AlightOnly:
			return BoardingPossibilityEnum.forbidden;
		case BoardOnly:
			return BoardingPossibilityEnum.normal;
		case NeitherBoardOrAlight:
			return BoardingPossibilityEnum.forbidden;
		case BoardAndAlightOnRequest:
			return BoardingPossibilityEnum.request_stop;
		case AlightOnRequest:
			return BoardingPossibilityEnum.normal;
		case BoardOnRequest:
			return BoardingPossibilityEnum.request_stop;
		}
		return null;
	}

	static {
		ParserFactory.register(RegtoppLineParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppLineParser();
			}
		});
	}

}
