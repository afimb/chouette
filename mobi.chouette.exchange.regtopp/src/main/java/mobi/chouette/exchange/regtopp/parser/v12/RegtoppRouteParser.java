package mobi.chouette.exchange.regtopp.parser.v12;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.MAIN_VALIDATION_REPORT;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;
import static mobi.chouette.exchange.regtopp.RegtoppConstant.REGTOPP_REPORTER;
import static mobi.chouette.exchange.regtopp.validation.Constant.REGTOPP_FILE_TMS;

import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.model.enums.DirectionType;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.Index;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.parser.AbstractConverter;
import mobi.chouette.exchange.regtopp.parser.RouteKey;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Company;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.PTDirectionEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppRouteParser extends mobi.chouette.exchange.regtopp.parser.v11.RegtoppRouteParser {

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
	}

	private void validateTMSIndex(Context context, RegtoppImporter importer, RegtoppValidationReporter validationReporter) throws Exception {
		if (importer.hasTMSImporter()) {
			validationReporter.reportSuccess(context, REGTOPP_FILE_TMS, RegtoppRouteTMS.FILE_EXTENSION);

			Index<RegtoppRouteTMS> index = importer.getRouteIndex();

			if (index.getLength() == 0) {
				FileParserValidationError fileError = new FileParserValidationError(RegtoppRouteTMS.FILE_EXTENSION, 0, null,
						RegtoppException.ERROR.FILE_WITH_NO_ENTRY, null, "Empty file");
				validationReporter.reportError(context, new RegtoppException(fileError), RegtoppRouteTMS.FILE_EXTENSION);
			}

			for (RegtoppRouteTMS bean : index) {
				try {
					// Call index validator
					index.validate(bean, importer);
				} catch (Exception ex) {
					log.error(ex);
					if (ex instanceof RegtoppException) {
						validationReporter.reportError(context, (RegtoppException) ex, RegtoppRouteTMS.FILE_EXTENSION);
					} else {
						validationReporter.throwUnknownError(context, ex, RegtoppRouteTMS.FILE_EXTENSION);
					}
				}
				validationReporter.reportErrors(context, bean.getErrors(), RegtoppRouteTMS.FILE_EXTENSION);
				validationReporter.validate(context, RegtoppRouteTMS.FILE_EXTENSION, bean.getOkTests());
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

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		String chouetteLineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY, lineId);
		Line line = ObjectFactory.getLine(referential, chouetteLineId);
		List<Footnote> footnotes = line.getFootnotes();

		Index<RegtoppDestinationDST> destinationIndex = importer.getDestinationById();

		// Add routes and journey patterns
		Index<RegtoppRouteTMS> routeIndex = importer.getRouteIndex();

		for (RegtoppRouteTMS routeSegment : routeIndex) {
			if (lineId.equals(routeSegment.getLineId())) {

				// Add network
				String chouetteNetworkId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.PTNETWORK_KEY,
						routeSegment.getAdminCode());
				Network ptNetwork = ObjectFactory.getPTNetwork(referential, chouetteNetworkId);
				if (!ptNetwork.isFilled()) {
					ptNetwork.setSourceIdentifier("Regtopp");
					ptNetwork.setName(routeSegment.getAdminCode());
					ptNetwork.setRegistrationNumber(routeSegment.getAdminCode());
					ptNetwork.setFilled(true);
				}
				line.setNetwork(ptNetwork);

				// Add authority company
				String chouetteCompanyId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.COMPANY_KEY,
						routeSegment.getAdminCode());
				Company company = ObjectFactory.getCompany(referential, chouetteCompanyId);
				if (!company.isFilled()) {
					company.setRegistrationNumber(routeSegment.getAdminCode());
					company.setName("Authority " + routeSegment.getAdminCode());
					company.setCode(routeSegment.getAdminCode());
					company.setFilled(true);
				}
				line.setCompany(company);

				// TODO Add footnoe to line
				addFootnote(routeSegment.getRemarkId(), null, footnotes, importer);

				RouteKey routeKey = new RouteKey(routeSegment.getLineId(), routeSegment.getDirection(), routeSegment.getRouteId());

				// Create route
				String chouetteRouteId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.ROUTE_KEY, routeKey.toString());
				Route route = ObjectFactory.getRoute(referential, chouetteRouteId);
				if (!route.isFilled()) {
					// Filled = only a flag to indicate that we no longer should write data to this entity
					RegtoppDestinationDST arrivalText = destinationIndex.getValue(routeSegment.getDestinationId());
					if (arrivalText != null) {
						route.setName(arrivalText.getDestinationText());
					}

					route.setDirection(routeSegment.getDirection() == DirectionType.Outbound ? PTDirectionEnum.A : PTDirectionEnum.R);

					// TODO UNSURE
					route.setNumber(routeSegment.getRouteId());
					route.setLine(line);

					// Black magic
					route.setWayBack(routeSegment.getDirection() == DirectionType.Outbound ? "A" : "R");

					route.setFilled(true);

				}

				// Create journey pattern
				String chouetteJourneyPatternId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.JOURNEYPATTERN_KEY,
						routeKey.toString());

				JourneyPattern journeyPattern = ObjectFactory.getJourneyPattern(referential, chouetteJourneyPatternId);
				journeyPattern.setRoute(route);

				// Create stop point
				String chouetteStopPointId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.STOPPOINT_KEY,
						routeKey + routeSegment.getSequenceNumberStop());

				StopPoint stopPoint = createStopPoint(referential, context, routeSegment, chouetteStopPointId);

				// Add stop point to journey pattern AND route (for now)
				journeyPattern.addStopPoint(stopPoint);
				route.getStopPoints().add(stopPoint);

			}
		}

		sortStopPoints(referential);
		updateRouteNames(referential, configuration);
		linkOppositeRoutes(referential, configuration);

	}

	

	// TODO separate out footnote parsing
	protected void addFootnote(String footnoteId, VehicleJourney vehicleJourney, List<Footnote> footnotes, RegtoppImporter importer) throws Exception {
		if (!"000".equals(footnoteId)) {
			if (!footnoteAlreadyAdded(footnotes, footnoteId)) {
				Index<RegtoppFootnoteMRK> index = importer.getFootnoteById();
				RegtoppFootnoteMRK footnote = index.getValue(footnoteId);

				Footnote f = new Footnote();

				f.setLabel(footnote.getDescription());
				f.setKey(footnote.getFootnoteId());
				f.setCode(footnote.getFootnoteId());

				footnotes.add(f);
			}
			if (vehicleJourney != null) {
				for (Footnote existing : footnotes) {
					if (existing.getCode().equals(footnoteId)) {
						vehicleJourney.getFootnotes().add(existing);
					}
				}
			}
		}
	}


	protected StopPoint createStopPoint(Referential referential, Context context, RegtoppRouteTMS routeSegment, String chouetteStopPointId) throws Exception {

		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		StopPoint stopPoint = ObjectFactory.getStopPoint(referential, chouetteStopPointId);
		stopPoint.setPosition(Integer.parseInt(routeSegment.getSequenceNumberStop()));

		String regtoppId = routeSegment.getStopId();
		String chouetteStopAreaId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.STOPAREA_KEY, regtoppId);

		StopArea stopArea = ObjectFactory.getStopArea(referential, chouetteStopAreaId);

		stopPoint.setContainedInStopArea(stopArea);

		return stopPoint;
	}

	private boolean footnoteAlreadyAdded(List<Footnote> addedFootnotes, String footnoteId) {
		for (Footnote existing : addedFootnotes) {
			if (existing.getCode().equals(footnoteId)) {
				return true;
			}
		}
		return false;
	}

	
	static {
		ParserFactory.register(RegtoppRouteParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppRouteParser();
			}
		});
	}

}
