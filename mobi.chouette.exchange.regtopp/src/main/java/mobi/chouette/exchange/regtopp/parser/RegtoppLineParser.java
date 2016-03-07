package mobi.chouette.exchange.regtopp.parser;

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.importer.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.importer.index.Index;
import mobi.chouette.exchange.regtopp.validation.Constant;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppLineParser implements Parser, Validator, Constant {

	@Setter
	private String lineId = null;
	
	@Override
	public void validate(Context context) throws Exception {

		// Konsistenssjekker, kjøres før parse-metode.
		
		// Det som kan sjekkes her er at antall poster stemmer og at alle referanser til andre filer er gyldige

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppValidationReporter validationReporter = (RegtoppValidationReporter) context.get(REGTOPP_REPORTER);
		validationReporter.getExceptions().clear();

		ValidationReport mainReporter = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);

		mainReporter.getCheckPoints().add(new CheckPoint(REGTOPP_FILE_TIX, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));

		
		if (importer.hasTIXImporter()) { 
			validationReporter.reportSuccess(context, REGTOPP_FILE_TIX, RegtoppTripIndexTIX.FILE_EXTENSION);

			Index<RegtoppTripIndexTIX> index = importer.getTripIndex();

			if (index.getLength() == 0) {
				FileParserValidationError fileError = new FileParserValidationError(RegtoppTripIndexTIX.FILE_EXTENSION, 0, null,
						RegtoppException.ERROR.FILE_WITH_NO_ENTRY, null, "Empty file");
				validationReporter.reportError(context, new RegtoppException(fileError), RegtoppTripIndexTIX.FILE_EXTENSION);
			}

			for (RegtoppTripIndexTIX bean : index) {
				try {
					// Call index validator
					index.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof RegtoppException) {
						validationReporter.reportError(context, (RegtoppException) ex, RegtoppTripIndexTIX.FILE_EXTENSION);
					} else {
						validationReporter.throwUnknownError(context, ex, RegtoppTripIndexTIX.FILE_EXTENSION);
					}
				}
			}
		}
	}

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

		String chouetteLineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY,
				lineId, log);
		
		// Create the actual Chouette Line and put it in the "referential" space (which is later used by the LineImporterCommand)
		Line line = ObjectFactory.getLine(referential, chouetteLineId);

		// Get index over the TMS file
		Index<RegtoppRouteTMS> routeIndex = importer.getRouteById();
		
		// Get index over all footnotes MRK file
		Index<RegtoppFootnoteMRK> footnoteIndex = importer.getFootnoteById();
		Index<RegtoppDestinationDST> destinationIndex = importer.getDestinationById();
		Index<RegtoppDayCodeDKO> dayCodeIndex = importer.getDayCodeById();
		
		
		Index<RegtoppTripIndexTIX> tripIndex = importer.getTripIndex();

		for (RegtoppTripIndexTIX trip : tripIndex) {
			// Find matching trips
			if(trip.getLineId().equals(chouetteLineId)) {
				// Find matching routes
				RegtoppRouteTMS regtoppRoute = routeIndex.getValue(trip.getRouteId());
				
				String routeId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Route.ROUTE_KEY,
						trip.getRouteId(), log);

				Route route = ObjectFactory.getRoute(referential, routeId);
				route.setLine(line);
//				String wayBack = gtfsTrip.getDirectionId().equals(DirectionType.Outbound) ? "A" : "R";
//				route.setWayBack(wayBack);
//				return route;
								
				
			}
		
		}
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
