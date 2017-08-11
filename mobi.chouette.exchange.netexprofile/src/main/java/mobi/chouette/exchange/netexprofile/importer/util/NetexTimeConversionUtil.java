package mobi.chouette.exchange.netexprofile.importer.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;


import org.rutebanken.netex.model.TimetabledPassingTime;

import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;
import mobi.chouette.exchange.netexprofile.parser.NetexParserUtils;
import mobi.chouette.model.VehicleJourneyAtStop;

public class NetexTimeConversionUtil {
	
	// TODO read this from referential
	private static final ZoneId LOCAL_ZONE_ID = ZoneId.systemDefault();

	public static void populatePassingTimeUtc(TimetabledPassingTime passingTime, boolean arrival, VehicleJourneyAtStop vj) {
	    if((arrival && vj.getArrivalTime() == null || (!arrival && vj.getDepartureTime() == null))) {
	    	return;
	    }

		int dayOffset = arrival ? vj.getArrivalDayOffset() : vj.getDepartureDayOffset();
		LocalTime localTime = TimeUtil.toLocalTimeFromJoda(arrival ? vj.getArrivalTime() : vj.getDepartureTime());

		OffsetTime zuluTime = localTime.atOffset(NetexProducerUtils.getZoneOffset(LOCAL_ZONE_ID)).withOffsetSameInstant(ZoneOffset.UTC);
		if (zuluTime.getHour() > localTime.getHour()) {
			// Shifted before midnight. Note only works for east of GMT?
			dayOffset--;
		}
	    
		if(arrival) {
			passingTime.setArrivalTime(zuluTime);
			if(dayOffset != 0) {
				passingTime.setArrivalDayOffset(BigInteger.valueOf(dayOffset));
			}
		} else {
			passingTime.setDepartureTime(zuluTime);
			if(dayOffset != 0) {
				passingTime.setDepartureDayOffset(BigInteger.valueOf(dayOffset));
			}
		}
	}

	public static void parsePassingTimeUtc(TimetabledPassingTime passingTime, boolean arrival, VehicleJourneyAtStop vj) {
	    if((arrival && passingTime.getArrivalTime() == null || (!arrival && passingTime.getDepartureTime() == null))) {
	    	return;
	    }
	
	    OffsetTime zuluTime = arrival ? passingTime.getArrivalTime() : passingTime.getDepartureTime();
	    BigInteger dayOffset = arrival? passingTime.getArrivalDayOffset() : passingTime.getDepartureDayOffset();
	    if(dayOffset == null) {
	    	dayOffset = BigInteger.ZERO;
	    }



		LocalTime localTime =  zuluTime.withOffsetSameInstant(NetexParserUtils.getZoneOffset(LOCAL_ZONE_ID)).toLocalTime();
	    if(zuluTime.getHour() > localTime.getHour()) {
	    	dayOffset = dayOffset.add(BigInteger.ONE);
	    }
		
	    
		if(arrival) {
			vj.setArrivalTime(TimeUtil.toJodaLocalTime(localTime));

			if(!BigDecimal.ZERO.equals(dayOffset)) {
				vj.setArrivalDayOffset(dayOffset.intValue());
			}
		} else {
			vj.setDepartureTime(TimeUtil.toJodaLocalTime(localTime));

			if(!BigDecimal.ZERO.equals(dayOffset)) {
				vj.setDepartureDayOffset(dayOffset.intValue());
			}
		}
	}

}
