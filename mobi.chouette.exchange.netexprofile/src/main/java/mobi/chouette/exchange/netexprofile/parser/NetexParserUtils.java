package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.model.type.*;
import org.rutebanken.netex.model.DayOfWeekEnumeration;
import org.rutebanken.netex.model.EntityInVersionStructure;

import java.sql.Time;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j
public class NetexParserUtils extends ParserUtils {

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

	public static ZoneOffset getZoneOffset(ZoneId zoneId) {
		if (zoneId == null) {
			return null;
		}
		return zoneId.getRules().getOffset(Instant.now(Clock.system(zoneId)));
	}

	public static Date convertToDate(OffsetDateTime offsetDateTime) {
		return Date.from(offsetDateTime.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
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

	public static Integer getVersion(EntityInVersionStructure obj) {
		Integer version = 0;
		try {
			version = Integer.parseInt(obj.getVersion());
		} catch (NumberFormatException e) {
			log.warn("Unable to parse " + obj.getVersion() + " to Integer as supported by Neptune, returning 0");
		}
		return version;
	}

	public static String netexId(String objectIdPrefix, String elementName, String objectIdSuffix) {
		return objectIdPrefix + ":" + elementName + ":" + objectIdSuffix;
	}

}
