package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.model.type.*;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.DayOfWeekEnumeration;

import java.sql.Date;
import java.sql.Time;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Log4j
public class NetexParserUtils extends ParserUtils {

	public static String fromPTDirectionType(PTDirectionEnum type) {
		if (type == null)
			return null;
		return type.toString();
	}

	public static PTDirectionEnum toPTDirectionType(String value) {
		if (value == null)
			return null;
		PTDirectionEnum result = null;
		try {
			result = PTDirectionEnum.valueOf(StringUtils.capitalize(value));
		} catch (Exception e) {
			log.error("unable to translate " + value + " as PTDirection");
		}
		return result;
	}

	public static String fromPTNetworkSourceType(PTNetworkSourceTypeEnum type) {
		if (type == null)
			return null;
		return type.toString();
	}

	public static PTNetworkSourceTypeEnum toPTNetworkSourceType(String value) {
		if (value == null)
			return null;
		PTNetworkSourceTypeEnum result = null;
		try {
			result = PTNetworkSourceTypeEnum.valueOf(StringUtils.capitalize(value));
		} catch (Exception e) {
			log.error("unable to translate " + value + " as PTNetworkSourceType");
		}
		return result;
	}

	public static String fromConnectionLinkType(ConnectionLinkTypeEnum type) {
		if (type == null)
			return null;
		switch (type) {
		case Underground:
			return "indoors";
		case Overground:
			return "outdoors";
		case Mixed:
			return "mixed";
		default:
			return "unknown";
		}
	}

	public static ConnectionLinkTypeEnum toConnectionLinkType(String value) {
		if (value == null)
			return null;
		if (value.equals("indoors"))
			return ConnectionLinkTypeEnum.Underground;
		else if (value.equals("outdoors"))
			return ConnectionLinkTypeEnum.Overground;
		else if (value.equals("mixed"))
			return ConnectionLinkTypeEnum.Mixed;
		else
			return null;
	}

	public static String fromTransportModeNameEnum(TransportModeNameEnum type) {
		switch (type) {
		case Air:
			return "air";
		case Train:
			return "rail";
		case LongDistanceTrain:
			return "intercityRail";
		case LongDistanceTrain_2:
			return "intercityRail";
		case LocalTrain:
			return "urbanRail";
		case RapidTransit:
			return "urbanRail";
		case Metro:
			return "metro";
		case Tramway:
			return "tram";
		case Coach:
			return "coach";
		case Bus:
			return "bus";
		case Ferry:
			return "water";
		case Waterborne:
			return "water";
		case PrivateVehicle:
			return "selfDrive";
		case Walk:
			return "selfDrive";
		case Trolleybus:
			return "trolleyBus";
		case Bicycle:
			return "selfDrive";
		case Shuttle:
			return "rail";
		case Taxi:
			return "taxi";
		case Val:
			return "rail";
		case Other:
			return "unknown";
		default:
			return "";
		}
	}

	public static TransportModeNameEnum toTransportModeNameEnum(String value) {
		if (value == null)
			return null;
		else if (value.equals("air"))
			return TransportModeNameEnum.Air;
		else if (value.equals("rail"))
			return TransportModeNameEnum.Train;
		else if (value.equals("intercityRail"))
			return TransportModeNameEnum.LongDistanceTrain;
		else if (value.equals("urbanRail"))
			return TransportModeNameEnum.LocalTrain;
		else if (value.equals("metro"))
			return TransportModeNameEnum.Metro;
		else if (value.equals("tram"))
			return TransportModeNameEnum.Tramway;
		else if (value.equals("coach"))
			return TransportModeNameEnum.Coach;
		else if (value.equals("bus"))
			return TransportModeNameEnum.Bus;
		else if (value.equals("water"))
			return TransportModeNameEnum.Ferry;
		else if (value.equals("selfDrive"))
			return TransportModeNameEnum.Walk;
		else if (value.equals("trolleyBus"))
			return TransportModeNameEnum.Trolleybus;
		else if (value.equals("taxi"))
			return TransportModeNameEnum.Taxi;
		else if (value.equals("unknown"))
			return TransportModeNameEnum.Other;
		else
			return TransportModeNameEnum.Other;
	}

	public static LongLatTypeEnum toLongLatTypeEnum(String value) {
		if (value == null)
			return null;
		else if (value.equals("WGS84"))
			return LongLatTypeEnum.WGS84;
		else if (value.equals("WGS92"))
			return LongLatTypeEnum.WGS92;
		else
			return LongLatTypeEnum.Standard;
	}

	public static List<DayTypeEnum> getDayTypes(List<String> values) {
		List<DayTypeEnum> result = new ArrayList<DayTypeEnum>();
		for (String dayType : values) {
			try {
				result.add(DayTypeEnum.valueOf(dayType));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return result;

	}

	public static List<Date> getCalendarDays(List<String> values) {
		List<Date> result = new ArrayList<Date>();
		for (String value : values) {
			try {
				result.add(NetexParserUtils.getSQLDate(value));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return result;
	}

	public static ZoneOffset getZoneOffset(ZoneId zoneId) {
		if (zoneId == null) {
			return null;
		}
		return zoneId.getRules().getOffset(Instant.now(Clock.system(zoneId)));
	}

	public static Time convertToSqlTime(OffsetTime offsetTime) {
		if (offsetTime == null) {
			return null;
		}
		return Time.valueOf(offsetTime.withOffsetSameInstant(ZoneOffset.UTC).toLocalTime());
	}

	public static Time convertToSqlTime(OffsetTime offsetTime, ZoneOffset zoneOffset) {
		if (offsetTime == null) {
			return null;
		}
		return Time.valueOf(offsetTime.withOffsetSameInstant(zoneOffset).toLocalTime());
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

}
