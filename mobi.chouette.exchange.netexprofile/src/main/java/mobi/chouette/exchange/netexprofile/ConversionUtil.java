package mobi.chouette.exchange.netexprofile;

import java.math.BigInteger;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.TransportSubModeNameEnum;

import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.AirSubmodeEnumeration;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.BusSubmodeEnumeration;
import org.rutebanken.netex.model.DayOfWeekEnumeration;
import org.rutebanken.netex.model.FunicularSubmodeEnumeration;
import org.rutebanken.netex.model.MetroSubmodeEnumeration;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.RailSubmodeEnumeration;
import org.rutebanken.netex.model.TelecabinSubmodeEnumeration;
import org.rutebanken.netex.model.TramSubmodeEnumeration;
import org.rutebanken.netex.model.TransportSubmodeStructure;
import org.rutebanken.netex.model.WaterSubmodeEnumeration;

import static mobi.chouette.common.TimeUtil.toLocalTimeFromJoda;

public class ConversionUtil {

	public static MultilingualString getMLString(String v) {
		if (v == null) {
			return null;
		} else {
			return new MultilingualString().withValue(v);
		}
	}

	public static Integer asInteger(BigInteger v) {
		if (v == null) {
			return null;
		} else {
			return v.intValue();
		}
	}

	public static BigInteger asBigInteger(Integer v) {
		if (v == null) {
			return null;
		} else {
			return BigInteger.valueOf(v.longValue());
		}
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

	public static AllVehicleModesOfTransportEnumeration toVehicleModeOfTransportEnum(TransportModeNameEnum value) {
		if (value == null)
			return null;
		switch (value) {
		case Air:
			return AllVehicleModesOfTransportEnumeration.AIR;
		case Bus:
			return AllVehicleModesOfTransportEnumeration.BUS;
		case Coach:
			return AllVehicleModesOfTransportEnumeration.COACH;
		case Metro:
			return AllVehicleModesOfTransportEnumeration.METRO;
		case Rail:
			return AllVehicleModesOfTransportEnumeration.RAIL;
		case TrolleyBus:
			return AllVehicleModesOfTransportEnumeration.TROLLEY_BUS;
		case Tram:
			return AllVehicleModesOfTransportEnumeration.TRAM;
		case Water:
		case Ferry:
			return AllVehicleModesOfTransportEnumeration.WATER;
		case Lift:
		case Cableway:
			return AllVehicleModesOfTransportEnumeration.CABLEWAY;
		case Funicular:
			return AllVehicleModesOfTransportEnumeration.FUNICULAR;
		case Taxi:
			return AllVehicleModesOfTransportEnumeration.TAXI;

		case Bicycle:
		case Other:
		default:
			return AllVehicleModesOfTransportEnumeration.UNKNOWN;

		}

	}

	public static TransportSubmodeStructure toTransportSubmodeStructure(TransportSubModeNameEnum transportSubMode) {
		if (transportSubMode == null) {
			return null;
		} else {
			switch (transportSubMode) {

			/**
			 * Bus sub modes
			 */
			case AirportLinkBus:
				return new TransportSubmodeStructure().withBusSubmode(BusSubmodeEnumeration.AIRPORT_LINK_BUS);
			case ExpressBus:
				return new TransportSubmodeStructure().withBusSubmode(BusSubmodeEnumeration.EXPRESS_BUS);
			case LocalBus:
				return new TransportSubmodeStructure().withBusSubmode(BusSubmodeEnumeration.LOCAL_BUS);
			case NightBus:
				return new TransportSubmodeStructure().withBusSubmode(BusSubmodeEnumeration.NIGHT_BUS);
			case RailReplacementBus:
				return new TransportSubmodeStructure().withBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);
			case RegionalBus:
				return new TransportSubmodeStructure().withBusSubmode(BusSubmodeEnumeration.REGIONAL_BUS);
			case SchoolBus:
				return new TransportSubmodeStructure().withBusSubmode(BusSubmodeEnumeration.SCHOOL_BUS);
			case ShuttleBus:
				return new TransportSubmodeStructure().withBusSubmode(BusSubmodeEnumeration.SHUTTLE_BUS);
			case SightseeingBus:
				return new TransportSubmodeStructure().withBusSubmode(BusSubmodeEnumeration.SIGHTSEEING_BUS);

				/**
				 * Tram sub modes
				 */
			case LocalTram:
				return new TransportSubmodeStructure().withTramSubmode(TramSubmodeEnumeration.LOCAL_TRAM);

				/**
				 * Rail sub modes
				 */
			case International:
				return new TransportSubmodeStructure().withRailSubmode(RailSubmodeEnumeration.INTERNATIONAL);
			case InterregionalRail:
				return new TransportSubmodeStructure().withRailSubmode(RailSubmodeEnumeration.INTERREGIONAL_RAIL);
			case Local:
				return new TransportSubmodeStructure().withRailSubmode(RailSubmodeEnumeration.LOCAL);
			case LongDistance:
				return new TransportSubmodeStructure().withRailSubmode(RailSubmodeEnumeration.LONG_DISTANCE);
			case NightRail:
				return new TransportSubmodeStructure().withRailSubmode(RailSubmodeEnumeration.NIGHT_RAIL);
			case RegionalRail:
				return new TransportSubmodeStructure().withRailSubmode(RailSubmodeEnumeration.REGIONAL_RAIL);
			case TouristRailway:
				return new TransportSubmodeStructure().withRailSubmode(RailSubmodeEnumeration.TOURIST_RAILWAY);

				/**
				 * Metro sub modes
				 */
			case Metro:
				return new TransportSubmodeStructure().withMetroSubmode(MetroSubmodeEnumeration.METRO);

				/**
				 * Air sub modes
				 */
			case DomesticFlight:
				return new TransportSubmodeStructure().withAirSubmode(AirSubmodeEnumeration.DOMESTIC_FLIGHT);
			case HelicopterService:
				return new TransportSubmodeStructure().withAirSubmode(AirSubmodeEnumeration.HELICOPTER_SERVICE);
			case InternationalFlight:
				return new TransportSubmodeStructure().withAirSubmode(AirSubmodeEnumeration.INTERNATIONAL_FLIGHT);

				/**
				 * Water sub modes
				 */
			case HighSpeedPassengerService:
				return new TransportSubmodeStructure().withWaterSubmode(WaterSubmodeEnumeration.HIGH_SPEED_PASSENGER_SERVICE);
			case HighSpeedVehicleService:
				return new TransportSubmodeStructure().withWaterSubmode(WaterSubmodeEnumeration.HIGH_SPEED_VEHICLE_SERVICE);
			case InternationalCarFerry:
				return new TransportSubmodeStructure().withWaterSubmode(WaterSubmodeEnumeration.INTERNATIONAL_CAR_FERRY);
			case InternationalPassengerFerry:
				return new TransportSubmodeStructure().withWaterSubmode(WaterSubmodeEnumeration.INTERNATIONAL_PASSENGER_FERRY);
			case LocalCarFerry:
				return new TransportSubmodeStructure().withWaterSubmode(WaterSubmodeEnumeration.LOCAL_CAR_FERRY);
			case LocalPassengerFerry:
				return new TransportSubmodeStructure().withWaterSubmode(WaterSubmodeEnumeration.LOCAL_PASSENGER_FERRY);
			case NationalCarFerry:
				return new TransportSubmodeStructure().withWaterSubmode(WaterSubmodeEnumeration.NATIONAL_CAR_FERRY);
			case SightseeingService:
				return new TransportSubmodeStructure().withWaterSubmode(WaterSubmodeEnumeration.SIGHTSEEING_SERVICE);

				/**
				 * Cabelway sub modes
				 */
			case Telecabin:
				return new TransportSubmodeStructure().withTelecabinSubmode(TelecabinSubmodeEnumeration.TELECABIN);

				/**
				 * Funicular sub modes
				 */
			case Funicular:


			default:
				// Fall through
			}
		}

		return null;
	}

	public static String getValue(MultilingualString m) {
		String v = null;
		if (m != null) {
			v = StringUtils.trimToNull(m.getValue());
		}

		return v;

	}

	public static OffsetTime toOffsetTimeUtc(org.joda.time.LocalTime time) {
		return time == null ? null
				: toLocalTimeFromJoda(time).atOffset(NetexProducerUtils.getZoneOffset(NetexProducerUtils.LOCAL_ZONE_ID)).withOffsetSameInstant(ZoneOffset.UTC);
	}

}
