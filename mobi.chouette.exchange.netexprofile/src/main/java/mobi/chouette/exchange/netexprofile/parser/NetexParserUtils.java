package mobi.chouette.exchange.netexprofile.parser;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.rutebanken.netex.model.AirSubmodeEnumeration;
import org.rutebanken.netex.model.BusSubmodeEnumeration;
import org.rutebanken.netex.model.CoachSubmodeEnumeration;
import org.rutebanken.netex.model.DayOfWeekEnumeration;
import org.rutebanken.netex.model.EntityInVersionStructure;
import org.rutebanken.netex.model.FunicularSubmodeEnumeration;
import org.rutebanken.netex.model.MetroSubmodeEnumeration;
import org.rutebanken.netex.model.OrganisationTypeEnumeration;
import org.rutebanken.netex.model.RailSubmodeEnumeration;
import org.rutebanken.netex.model.TelecabinSubmodeEnumeration;
import org.rutebanken.netex.model.TramSubmodeEnumeration;
import org.rutebanken.netex.model.TransportSubmodeStructure;
import org.rutebanken.netex.model.WaterSubmodeEnumeration;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.type.OrganisationTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.TransportSubModeNameEnum;

@Log4j
public class NetexParserUtils extends ParserUtils {

	public static TransportModeNameEnum toTransportModeNameEnum(String value) {
		if (value == null)
			return null;
		else if (value.equals("air"))
			return TransportModeNameEnum.Air;
		else if (value.equals("rail"))
			return TransportModeNameEnum.Rail;
		else if (value.equals("metro"))
			return TransportModeNameEnum.Metro;
		else if (value.equals("tram"))
			return TransportModeNameEnum.Tram;
		else if (value.equals("coach"))
			return TransportModeNameEnum.Coach;
		else if (value.equals("bus"))
			return TransportModeNameEnum.Bus;
		else if (value.equals("water"))
			return TransportModeNameEnum.Water;
		else if (value.equals("ferry"))
			return TransportModeNameEnum.Ferry;
		else if (value.equals("trolleyBus"))
			return TransportModeNameEnum.TrolleyBus;
		else if (value.equals("taxi"))
			return TransportModeNameEnum.Taxi;
		else if (value.equals("cableway"))
			return TransportModeNameEnum.Cableway;
		else if (value.equals("funicular"))
			return TransportModeNameEnum.Funicular;
		else if (value.equals("lift"))
			return TransportModeNameEnum.Lift;
		else if (value.equals("unknown"))
			return TransportModeNameEnum.Other;
		else if (value.equals("bicycle"))
			return TransportModeNameEnum.Bicycle;
		else
			return TransportModeNameEnum.Other;
	}

	public static TransportSubModeNameEnum toTransportSubModeNameEnum(TransportSubmodeStructure subModeStructure) {
		if (subModeStructure != null) {
			if (subModeStructure.getAirSubmode() != null) {
				AirSubmodeEnumeration mode = subModeStructure.getAirSubmode();
				switch (mode) {
				case DOMESTIC_FLIGHT:
					return TransportSubModeNameEnum.DomesticFlight;
				case HELICOPTER_SERVICE:
					return TransportSubModeNameEnum.HelicopterService;
				case INTERCONTINENTAL_FLIGHT:
					return TransportSubModeNameEnum.InternationalFlight;
				default:
					log.error("Unsupported air sub mode " + mode);
				}
			} else if (subModeStructure.getBusSubmode() != null) {
				BusSubmodeEnumeration mode = subModeStructure.getBusSubmode();
				switch (mode) {
				case AIRPORT_LINK_BUS:
					return TransportSubModeNameEnum.AirportLinkBus;
				case EXPRESS_BUS:
					return TransportSubModeNameEnum.ExpressBus;
				case LOCAL_BUS:
					return TransportSubModeNameEnum.LocalBus;
				case NIGHT_BUS:
					return TransportSubModeNameEnum.NightBus;
				case RAIL_REPLACEMENT_BUS:
					return TransportSubModeNameEnum.RailReplacementBus;
				case REGIONAL_BUS:
					return TransportSubModeNameEnum.RegionalBus;
				case SCHOOL_BUS:
					return TransportSubModeNameEnum.SchoolBus;
				case SHUTTLE_BUS:
					return TransportSubModeNameEnum.ShuttleBus;
				case SIGHTSEEING_BUS:
					return TransportSubModeNameEnum.SightseeingBus;
				default:
					log.error("Unsupported bus sub mode " + mode);
				}
			} else if (subModeStructure.getCoachSubmode() != null) {
				CoachSubmodeEnumeration mode = subModeStructure.getCoachSubmode();
				log.error("Unsupported coach sub mode " + mode);
			} else if (subModeStructure.getFunicularSubmode() != null) {
				FunicularSubmodeEnumeration mode = subModeStructure.getFunicularSubmode();
				switch (mode) {
				case FUNICULAR:
					return TransportSubModeNameEnum.Funicular;
				default:
					log.error("Unsupported funicular sub mode " + mode);
				}
			} else if (subModeStructure.getMetroSubmode() != null) {
				MetroSubmodeEnumeration mode = subModeStructure.getMetroSubmode();
				switch (mode) {
				case METRO:
					return TransportSubModeNameEnum.Metro;
				default:
					log.error("Unsupported metro sub mode " + mode);
				}
			} else if (subModeStructure.getRailSubmode() != null) {
				RailSubmodeEnumeration mode = subModeStructure.getRailSubmode();
				switch (mode) {
				case INTERNATIONAL:
					return TransportSubModeNameEnum.International;
				case INTERREGIONAL_RAIL:
					return TransportSubModeNameEnum.InterregionalRail;
				case LOCAL:
					return TransportSubModeNameEnum.Local;
				case LONG_DISTANCE:
					return TransportSubModeNameEnum.LongDistance;
				case NIGHT_RAIL:
					return TransportSubModeNameEnum.NightRail;
				case REGIONAL_RAIL:
					return TransportSubModeNameEnum.RegionalRail;
				case TOURIST_RAILWAY:
					return TransportSubModeNameEnum.TouristRailway;
				default:
					log.error("Unsupported rail sub mode " + mode);
				}
			} else if (subModeStructure.getTelecabinSubmode() != null) {
				TelecabinSubmodeEnumeration mode = subModeStructure.getTelecabinSubmode();
				switch (mode) {
				case TELECABIN:
					return TransportSubModeNameEnum.Telecabin;
				default:
					log.error("Unsupported telecabin sub mode " + mode);
				}
			} else if (subModeStructure.getTramSubmode() != null) {
				TramSubmodeEnumeration mode = subModeStructure.getTramSubmode();
				switch (mode) {
				case LOCAL_TRAM:
					return TransportSubModeNameEnum.LocalTram;
				default:
					log.error("Unsupported tram sub mode " + mode);
				}
			} else if (subModeStructure.getWaterSubmode() != null) {
				WaterSubmodeEnumeration mode = subModeStructure.getWaterSubmode();
				switch (mode) {
				case HIGH_SPEED_PASSENGER_SERVICE:
					return TransportSubModeNameEnum.HighSpeedPassengerService;
				case HIGH_SPEED_VEHICLE_SERVICE:
				return TransportSubModeNameEnum.HighSpeedVehicleService;
				case INTERNATIONAL_CAR_FERRY:
					return TransportSubModeNameEnum.InternationalCarFerry;
				case INTERNATIONAL_PASSENGER_FERRY:
					return TransportSubModeNameEnum.InternationalPassengerFerry;
				case LOCAL_CAR_FERRY:
					return TransportSubModeNameEnum.LocalCarFerry;
				case LOCAL_PASSENGER_FERRY:
					return TransportSubModeNameEnum.LocalPassengerFerry;
				case NATIONAL_CAR_FERRY:
					return TransportSubModeNameEnum.NationalCarFerry;
				case SIGHTSEEING_SERVICE:
					return TransportSubModeNameEnum.SightseeingService;
				default:
					log.error("Unsupported water sub mode " + mode);
				}
			}

		}

		return null;
	}

	public static ZoneOffset getZoneOffset(ZoneId zoneId) {
		if (zoneId == null) {
			return null;
		}
		return zoneId.getRules().getOffset(Instant.now(Clock.system(zoneId)));
	}

	public static List<DayTypeEnum> convertDayOfWeek(DayOfWeekEnumeration dayOfWeek) {
		List<DayTypeEnum> days = new ArrayList<>();

		switch (dayOfWeek) {
		case MONDAY:
			days.add(DayTypeEnum.Monday);
			break;
		case TUESDAY:
			days.add(DayTypeEnum.Tuesday);
			break;
		case WEDNESDAY:
			days.add(DayTypeEnum.Wednesday);
			break;
		case THURSDAY:
			days.add(DayTypeEnum.Thursday);
			break;
		case FRIDAY:
			days.add(DayTypeEnum.Friday);
			break;
		case SATURDAY:
			days.add(DayTypeEnum.Saturday);
			break;
		case SUNDAY:
			days.add(DayTypeEnum.Sunday);
			break;
		case EVERYDAY:
			days.add(DayTypeEnum.Monday);
			days.add(DayTypeEnum.Tuesday);
			days.add(DayTypeEnum.Wednesday);
			days.add(DayTypeEnum.Thursday);
			days.add(DayTypeEnum.Friday);
			days.add(DayTypeEnum.Saturday);
			days.add(DayTypeEnum.Sunday);
			break;
		case WEEKDAYS:
			days.add(DayTypeEnum.Monday);
			days.add(DayTypeEnum.Tuesday);
			days.add(DayTypeEnum.Wednesday);
			days.add(DayTypeEnum.Thursday);
			days.add(DayTypeEnum.Friday);
			break;
		case WEEKEND:
			days.add(DayTypeEnum.Saturday);
			days.add(DayTypeEnum.Sunday);
			break;
		case NONE:
			// None
			break;
		}
		return days;
	}

	public static AlightingPossibilityEnum getForAlighting(BoardingAlightingPossibilityEnum boardingAlightingPossibility) {
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

	public static BoardingPossibilityEnum getForBoarding(BoardingAlightingPossibilityEnum boardingAlightingPossibility) {
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

	public static OrganisationTypeEnum getOrganisationType(OrganisationTypeEnumeration organisationTypeEnumeration) {
		if (organisationTypeEnumeration == null)
			return null;
		switch (organisationTypeEnumeration) {
		case AUTHORITY:
			return OrganisationTypeEnum.Authority;
		case OPERATOR:
			return OrganisationTypeEnum.Operator;
		}
		return null;
	}

	public static Integer getVersion(EntityInVersionStructure obj) {
		Integer version = 0;
		try {
			version = Integer.parseInt(obj.getVersion());
		} catch (NumberFormatException e) {
			log.debug("Unable to parse " + obj.getVersion() + " to Integer as supported by Neptune, returning 0");
		}
		return version;
	}

	public static String netexId(String objectIdPrefix, String elementName, String objectIdSuffix) {
		return objectIdPrefix + ":" + elementName + ":" + objectIdSuffix;
	}

}
