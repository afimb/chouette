package mobi.chouette.exchange.gtfs.parser;


import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.RouteTypeEnum;
import mobi.chouette.exchange.gtfs.model.importer.AgencyById;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.gtfs.validation.Constant;
import mobi.chouette.exchange.gtfs.validation.ValidationReporter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.model.Company;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsRouteParser implements Parser, Validator, Constant {

	@Getter
	@Setter
	private String gtfsRouteId;

	/**
	 * Parse the GTFS file routes.txt into a virtual list of GtfsRoute.
	 * This list is virtual: (Re-)Parse the list to access a GtfsRoute.
	 * 
	 * Validation rules of type I and II are checked during this step, 
	 * and results are stored in reports.
	 */
	// TODO. Rename this function "parse(Context context)".
	@Override
	public void validate(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);
		Set<String> agencyIds = new HashSet<String>();
		validationReporter.getExceptions().clear();
		
		// routes.txt
		// log.info("validating routes");
		if (importer.hasRouteImporter()) { // the file "routes.txt" exists ?
			validationReporter.reportSuccess(context, GTFS_1_GTFS_Common_1, GTFS_ROUTES_FILE);

			Index<GtfsRoute> parser = null;
			try { // Read and check the header line of the file "routes.txt"
				parser = importer.getRouteById(); 
			} catch (Exception ex ) {
				if (ex instanceof GtfsException) {
					validationReporter.reportError(context, (GtfsException)ex, GTFS_ROUTES_FILE);
				} else {
					validationReporter.throwUnknownError(context, ex, GTFS_ROUTES_FILE);
				}
			}

			validationReporter.validateOkCSV(context, GTFS_ROUTES_FILE);
		
			if (parser == null) { // importer.getRouteById() fails for any other reason
				validationReporter.throwUnknownError(context, new Exception("Cannot instantiate RouteById class"), GTFS_ROUTES_FILE);
			} else {
				validationReporter.validate(context, GTFS_ROUTES_FILE, parser.getOkTests());
				validationReporter.validateUnknownError(context);
			}
			
			if (!parser.getErrors().isEmpty()) {
				validationReporter.reportErrors(context, parser.getErrors(), GTFS_ROUTES_FILE);
				parser.getErrors().clear();
			}
			
			validationReporter.validateOKGeneralSyntax(context, GTFS_ROUTES_FILE);
		
			if (parser.getLength() == 0) {
				validationReporter.reportError(context, new GtfsException(GTFS_ROUTES_FILE, 1, null, GtfsException.ERROR.FILE_WITH_NO_ENTRY, null, null), GTFS_ROUTES_FILE);
			} else {
				validationReporter.validate(context, GTFS_ROUTES_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			}
		
			GtfsException fatalException = null;
			parser.setWithValidation(true);
			for (GtfsRoute bean : parser) {
				try {
					parser.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof GtfsException) {
						validationReporter.reportError(context, (GtfsException)ex, GTFS_ROUTES_FILE);
					} else {
						validationReporter.throwUnknownError(context, ex, GTFS_ROUTES_FILE);
					}
				}
				if (bean.getAgencyId() != null)
					agencyIds.add(bean.getAgencyId());
				else
					agencyIds.add(GtfsAgency.DEFAULT_ID);
				for(GtfsException ex : bean.getErrors()) {
					if (ex.isFatal())
						fatalException = ex;
				}
				validationReporter.reportErrors(context, bean.getErrors(), GTFS_ROUTES_FILE);
				validationReporter.validate(context, GTFS_ROUTES_FILE, bean.getOkTests());
			}
			parser.setWithValidation(false);
			int i = 1;
			boolean unsuedId = true;
			for (GtfsAgency bean : importer.getAgencyById()) {
				if (agencyIds.add(bean.getAgencyId())) {
					unsuedId = false;
					validationReporter.reportError(context, new GtfsException(GTFS_AGENCY_FILE, i, AgencyById.FIELDS.agency_id.name(), GtfsException.ERROR.UNUSED_ID, null, bean.getAgencyId()), GTFS_AGENCY_FILE);
				}
				i++;
			}
			if (unsuedId)
				validationReporter.validate(context, GTFS_ROUTES_FILE, GtfsException.ERROR.UNUSED_ID);
			if (fatalException != null)
				throw fatalException;
		} else {
			validationReporter.reportError(context, new GtfsException(GTFS_ROUTES_FILE, 1, null, GtfsException.ERROR.MISSING_FILE, null, null), GTFS_ROUTES_FILE);
		}
	}

	
	/**
	 * Translate every (mobi.chouette.exchange.gtfs.model.)GtfsRoute 
	 * to a (mobi.chouette.model.)Line.
	 * 
	 * Validation rules of type III are checked at this step.
	 */
	// TODO. Rename this function "translate(Context context)" or "produce(Context context)", ...
	@Override
	public void parse(Context context) throws Exception {

		Referential referential = (Referential) context.get(REFERENTIAL);
		GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);
		
		Index<GtfsRoute> routes = importer.getRouteById();
		GtfsRoute gtfsRoute = routes.getValue(gtfsRouteId);

		String lineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY,
				gtfsRouteId, log);
		Line line = ObjectFactory.getLine(referential, lineId);
		convert(context, gtfsRoute, line);

		// update validationreport if necessary
		validationReporter.updateValidationReport(context, GTFS_ROUTES_FILE, gtfsRouteId, line);
		
		// PTNetwork
		String ptNetworkId = configuration.getObjectIdPrefix() + ":" + Network.PTNETWORK_KEY + ":"
				+ configuration.getObjectIdPrefix();
		Network ptNetwork = ObjectFactory.getPTNetwork(referential, ptNetworkId);
		line.setNetwork(ptNetwork);

		// Company
		if (gtfsRoute.getAgencyId() != null) {
			String companyId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
					Company.COMPANY_KEY, gtfsRoute.getAgencyId(), log);
			Company company = ObjectFactory.getCompany(referential, companyId);
			line.setCompany(company);
		}
		else if (!referential.getSharedCompanies().isEmpty())
		{
			Company company = referential.getSharedCompanies().values().iterator().next();
			line.setCompany(company);
		}

		// Route VehicleJourney VehicleJourneyAtStop , JourneyPattern ,StopPoint
		GtfsTripParser gtfsTripParser = (GtfsTripParser) ParserFactory.create(GtfsTripParser.class.getName());
		gtfsTripParser.setGtfsRouteId(gtfsRouteId);
		gtfsTripParser.parse(context);

	}

	protected void convert(Context context, GtfsRoute gtfsRoute, Line line) {

		line.setName(AbstractConverter.getNonEmptyTrimedString(gtfsRoute.getRouteLongName()));
		if (line.getName() == null)
			line.setName(AbstractConverter.getNonEmptyTrimedString(gtfsRoute.getRouteShortName()));

		line.setNumber(AbstractConverter.getNonEmptyTrimedString(gtfsRoute.getRouteShortName()));

		line.setPublishedName(AbstractConverter.getNonEmptyTrimedString(gtfsRoute.getRouteLongName()));

		if (line.getPublishedName() != null) {
			line.setName(line.getPublishedName());
		} else {
			line.setName(line.getNumber());
		}

		line.setTransportModeName(toTransportModeNameEnum(gtfsRoute.getRouteType()));

		String[] token = line.getObjectId().split(":");
		line.setRegistrationNumber(token[2]);
		line.setComment(gtfsRoute.getRouteDesc());
		line.setColor(toHexa(gtfsRoute.getRouteColor()));
		line.setTextColor(toHexa(gtfsRoute.getRouteTextColor()));
		line.setUrl(AbstractConverter.toString(gtfsRoute.getRouteUrl()));
		line.setFilled(true);
		AbstractConverter.addLocation(context, "routes.txt", line.getObjectId(), gtfsRoute.getId());
	}

	private TransportModeNameEnum toTransportModeNameEnum(RouteTypeEnum type)
	{
		switch (type) {
		case Tram:
			return TransportModeNameEnum.Tramway;
		case Subway:
			return TransportModeNameEnum.Metro;
		case Rail:
			return TransportModeNameEnum.Train;
		case Bus:
			return TransportModeNameEnum.Bus;
		case Ferry:
			return TransportModeNameEnum.Ferry;
		case Cable:
			return TransportModeNameEnum.Other;
		case Gondola:
			return TransportModeNameEnum.Other;
		case Funicular:
			return TransportModeNameEnum.Other;
			// gestion des nouveaux codes
		case RailwayService:
			return TransportModeNameEnum.Train;
		case HighSpeedRailService:
			return TransportModeNameEnum.Train;
		case LongDistanceTrains:
			return TransportModeNameEnum.LongDistanceTrain;
		case InterRegionalRailService:
			return TransportModeNameEnum.LongDistanceTrain;
		case CarTransportRailService:
			return TransportModeNameEnum.Train;
		case SleeperRailService:
			return TransportModeNameEnum.Train;
		case RegionalRailService:
			return TransportModeNameEnum.RapidTransit;
		case TouristRailwayService:
			return TransportModeNameEnum.Train;
		case RailShuttleWithinComplex:
			return TransportModeNameEnum.Shuttle;
		case SuburbanRailway:
			return TransportModeNameEnum.LocalTrain;
		case ReplacementRailService:
			return TransportModeNameEnum.Train;
		case SpecialRailService:
			return TransportModeNameEnum.Train;
		case LorryTransportRailService:
			return TransportModeNameEnum.Train;
		case AllRailServices:
			return TransportModeNameEnum.Train;
		case CrossCountryRailService:
			return TransportModeNameEnum.Train;
		case VehicleTransportRailService:
			return TransportModeNameEnum.Train;
		case RackandPinionRailway:
			return TransportModeNameEnum.Train;
		case AdditionalRailService:
			return TransportModeNameEnum.Train;
			// 
		case CoachService:
			return TransportModeNameEnum.Coach;
		case InternationalCoachService:
			return TransportModeNameEnum.Coach;
		case NationalCoachService:
			return TransportModeNameEnum.Coach;
		case ShuttleCoachService:
			return TransportModeNameEnum.Shuttle;
		case RegionalCoachService:
			return TransportModeNameEnum.Coach;
		case SpecialCoachService:
			return TransportModeNameEnum.Coach;
		case SightseeingCoachService:
			return TransportModeNameEnum.Coach;
		case TouristCoachService:
			return TransportModeNameEnum.Coach;
		case CommuterCoachService:
			return TransportModeNameEnum.Coach;
		case AllCoachServices:
			return TransportModeNameEnum.Coach;
			// 
		case SuburbanRailwayService:
			return TransportModeNameEnum.RapidTransit;
			// 
		case UrbanRailwayService:
			return TransportModeNameEnum.Metro;
			// 
		case MetroService:
			return TransportModeNameEnum.Metro;
		case UndergroundService:
			return TransportModeNameEnum.Metro;
		case UrbanRailwayService2:
			return TransportModeNameEnum.Metro;
		case AllUrbanRailwayServices:
			return TransportModeNameEnum.Metro;
		case Monorail:
			return TransportModeNameEnum.Metro;
			// 
		case MetroService2:
			return TransportModeNameEnum.Metro;
			// 
		case UndergroundService2:
			return TransportModeNameEnum.Metro;
			// 
		case BusService:
			return TransportModeNameEnum.Bus;
		case RegionalBusService:
			return TransportModeNameEnum.Bus;
		case ExpressBusService:
			return TransportModeNameEnum.Bus;
		case StoppingBusService:
			return TransportModeNameEnum.Bus;
		case LocalBusService:
			return TransportModeNameEnum.Bus;
		case NightBusService:
			return TransportModeNameEnum.Bus;
		case PostBusService:
			return TransportModeNameEnum.Bus;
		case SpecialNeedsBus:
			return TransportModeNameEnum.Bus;
		case MobilityBusService:
			return TransportModeNameEnum.Bus;
		case MobilityBusforRegisteredDisabled:
			return TransportModeNameEnum.Bus;
		case SightseeingBus:
			return TransportModeNameEnum.Bus;
		case ShuttleBus:
			return TransportModeNameEnum.Shuttle;
		case SchoolBus:
			return TransportModeNameEnum.Bus;
		case SchoolandPublicServiceBus:
			return TransportModeNameEnum.Bus;
		case RailReplacementBusService:
			return TransportModeNameEnum.Bus;
		case DemandandResponseBusService:
			return TransportModeNameEnum.Bus;
		case AllBusServices:
			return TransportModeNameEnum.Bus;
			// 
		case TrolleybusService:
			return TransportModeNameEnum.Trolleybus;
			// 
		case TramService:
			return TransportModeNameEnum.Tramway;
		case CityTramService:
			return TransportModeNameEnum.Tramway;
		case LocalTramService:
			return TransportModeNameEnum.Tramway;
		case RegionalTramService:
			return TransportModeNameEnum.Tramway;
		case SightseeingTramService:
			return TransportModeNameEnum.Tramway;
		case ShuttleTramService:
			return TransportModeNameEnum.Tramway;
		case AllTramServices:
			return TransportModeNameEnum.Tramway;
			// 
		case WaterTransportService:
			return TransportModeNameEnum.Ferry;
		case InternationalCarFerryService:
			return TransportModeNameEnum.Ferry;
		case NationalCarFerryService:
			return TransportModeNameEnum.Ferry;
		case RegionalCarFerryService:
			return TransportModeNameEnum.Ferry;
		case LocalCarFerryService:
			return TransportModeNameEnum.Ferry;
		case InternationalPassengerFerryService:
			return TransportModeNameEnum.Ferry;
		case NationalPassengerFerryService:
			return TransportModeNameEnum.Ferry;
		case RegionalPassengerFerryService:
			return TransportModeNameEnum.Ferry;
		case LocalPassengerFerryService:
			return TransportModeNameEnum.Ferry;
		case PostBoatService:
			return TransportModeNameEnum.Ferry;
		case TrainFerryService:
			return TransportModeNameEnum.Ferry;
		case RoadLinkFerryService:
			return TransportModeNameEnum.Ferry;
		case AirportLinkFerryService:
			return TransportModeNameEnum.Ferry;
		case CarHighSpeedFerryService:
			return TransportModeNameEnum.Ferry;
		case PassengerHighSpeedFerryService:
			return TransportModeNameEnum.Ferry;
		case SightseeingBoatService:
			return TransportModeNameEnum.Ferry;
		case SchoolBoat:
			return TransportModeNameEnum.Ferry;
		case CableDrawnBoatService:
			return TransportModeNameEnum.Ferry;
		case RiverBusService:
			return TransportModeNameEnum.Ferry;
		case ScheduledFerryService:
			return TransportModeNameEnum.Ferry;
		case ShuttleFerryService:
			return TransportModeNameEnum.Ferry;
		case AllWaterTransportServices:
			return TransportModeNameEnum.Ferry;
			//
		case AirService:
			return TransportModeNameEnum.Air;
		case InternationalAirService:
			return TransportModeNameEnum.Air;
		case DomesticAirService:
			return TransportModeNameEnum.Air;
		case IntercontinentalAirService:
			return TransportModeNameEnum.Air;
		case DomesticScheduledAirService:
			return TransportModeNameEnum.Air;
		case ShuttleAirService:
			return TransportModeNameEnum.Air;
		case IntercontinentalCharterAirService:
			return TransportModeNameEnum.Air;
		case InternationalCharterAirService:
			return TransportModeNameEnum.Air;
		case RoundTripCharterAirService:
			return TransportModeNameEnum.Air;
		case SightseeingAirService:
			return TransportModeNameEnum.Air;
		case HelicopterAirService:
			return TransportModeNameEnum.Air;
		case DomesticCharterAirService:
			return TransportModeNameEnum.Air;
		case SchengenAreaAirService:
			return TransportModeNameEnum.Air;
		case AirshipService:
			return TransportModeNameEnum.Air;
		case AllAirServices:
			return TransportModeNameEnum.Air;
			// 
		case FerryService:
			return TransportModeNameEnum.Ferry;
			// 
		case TelecabinService:
			return TransportModeNameEnum.Other;
		case TelecabinService2:
			return TransportModeNameEnum.Other;
		case CableCarService:
			return TransportModeNameEnum.Other;
		case ElevatorService:
			return TransportModeNameEnum.Other;
		case ChairLiftService:
			return TransportModeNameEnum.Other;
		case DragLiftService:
			return TransportModeNameEnum.Other;
		case SmallTelecabinService:
			return TransportModeNameEnum.Other;
		case AllTelecabinServices:
			return TransportModeNameEnum.Other;
			//
		case FunicularService:
			return TransportModeNameEnum.Other;
		case FunicularService2:
			return TransportModeNameEnum.Other;
		case AllFunicularService:
			return TransportModeNameEnum.Other;
			//
		case TaxiService:
			return TransportModeNameEnum.Taxi;
		case CommunalTaxiService:
			return TransportModeNameEnum.Taxi;
		case WaterTaxiService:
			return TransportModeNameEnum.Taxi;
		case RailTaxiService:
			return TransportModeNameEnum.Taxi;
		case BikeTaxiService:
			return TransportModeNameEnum.Taxi;
		case LicensedTaxiService:
			return TransportModeNameEnum.Taxi;
		case PrivateHireServiceVehicle:
			return TransportModeNameEnum.Taxi;
		case AllTaxiServices:
			return TransportModeNameEnum.Taxi;
			// 
		case SelfDrive:
			return TransportModeNameEnum.PrivateVehicle;
		case HireCar:
			return TransportModeNameEnum.PrivateVehicle;
		case HireVan:
			return TransportModeNameEnum.PrivateVehicle;
		case HireMotorbike:
			return TransportModeNameEnum.PrivateVehicle;
		case HireCycle:
			return TransportModeNameEnum.PrivateVehicle;
			// 
		case MiscellaneousService:
			return TransportModeNameEnum.Other;
		case CableCar:
			return TransportModeNameEnum.Other;
		case HorseDrawnCarriage:
			return TransportModeNameEnum.Other;
		default:
			return TransportModeNameEnum.Other;
		}
		
	}
	
	private String toHexa(Color color) {
		if (color == null)
			return null;
		String result = Integer.toHexString(color.getRGB());
		if (result.length() == 8)
			result = result.substring(2);
		while (result.length() < 6)
			result = "0" + result;
		return result;
	}

	static {
		ParserFactory.register(GtfsRouteParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new GtfsRouteParser();
			}
		});	
	}
}
