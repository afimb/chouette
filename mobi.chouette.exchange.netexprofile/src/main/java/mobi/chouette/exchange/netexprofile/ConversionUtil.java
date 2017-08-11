package mobi.chouette.exchange.netexprofile;

import java.math.BigInteger;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;
import mobi.chouette.model.type.DayTypeEnum;

import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.DayOfWeekEnumeration;
import org.rutebanken.netex.model.MultilingualString;

import static mobi.chouette.common.TimeUtil.toLocalTimeFromJoda;

public class ConversionUtil {

	public static MultilingualString getMLString(String v) {
		if(v == null) {
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

	public static AllVehicleModesOfTransportEnumeration toVehicleModeOfTransportEnum(String value) {
	    if (value == null)
	        return null;
	    else if (value.equals("Air"))
	        return AllVehicleModesOfTransportEnumeration.AIR;
	    else if (value.equals("Train"))
	        return AllVehicleModesOfTransportEnumeration.RAIL;
	    else if (value.equals("LongDistanceTrain"))
	        return AllVehicleModesOfTransportEnumeration.INTERCITY_RAIL;
	    else if (value.equals("LocalTrain"))
	        return AllVehicleModesOfTransportEnumeration.URBAN_RAIL;
	    else if (value.equals("Metro"))
	        return AllVehicleModesOfTransportEnumeration.METRO;
	    else if (value.equals("Tramway"))
	        return AllVehicleModesOfTransportEnumeration.TRAM;
	    else if (value.equals("Coach"))
	        return AllVehicleModesOfTransportEnumeration.COACH;
	    else if (value.equals("Bus"))
	        return AllVehicleModesOfTransportEnumeration.BUS;
	    else if (value.equals("Ferry"))
	        return AllVehicleModesOfTransportEnumeration.WATER;
	    else if (value.equals("Walk"))
	        return AllVehicleModesOfTransportEnumeration.SELF_DRIVE;
	    else if (value.equals("Trolleybus"))
	        return AllVehicleModesOfTransportEnumeration.TROLLEY_BUS;
	    else if (value.equals("Taxi"))
	        return AllVehicleModesOfTransportEnumeration.TAXI;
	    else if (value.equals("Other"))
	        return AllVehicleModesOfTransportEnumeration.UNKNOWN;
	    else
	        return AllVehicleModesOfTransportEnumeration.UNKNOWN;
	}

	public static String getValue(MultilingualString m) {
		String v = null;
		if(m != null) {
			v = StringUtils.trimToNull(m.getValue());
		}
		
		return v;
		
	}

	public static OffsetTime toOffsetTimeUtc(org.joda.time.LocalTime time) {
		return time == null ? null : toLocalTimeFromJoda(time).atOffset(NetexProducerUtils.getZoneOffset(NetexProducerUtils.LOCAL_ZONE_ID)).withOffsetSameInstant(ZoneOffset.UTC);
	}

}
