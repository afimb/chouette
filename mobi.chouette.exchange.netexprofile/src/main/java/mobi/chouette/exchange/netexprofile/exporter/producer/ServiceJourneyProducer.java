package mobi.chouette.exchange.netexprofile.exporter.producer;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexTimeConversionUtil;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalTime;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.DayTypeRefs_RelStructure;
import org.rutebanken.netex.model.JourneyPatternRefStructure;
import org.rutebanken.netex.model.LineRefStructure;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.StopPointInJourneyPatternRefStructure;
import org.rutebanken.netex.model.TimetabledPassingTime;
import org.rutebanken.netex.model.TimetabledPassingTimes_RelStructure;
import org.rutebanken.netex.model.TransportSubmodeStructure;

import static mobi.chouette.exchange.netexprofile.Constant.PRODUCING_CONTEXT;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SERVICE_JOURNEY;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.STOP_POINT_IN_JOURNEY_PATTERN;

public class ServiceJourneyProducer extends NetexProducer {

    @SuppressWarnings("unchecked")
    public ServiceJourney produce(Context context, VehicleJourney vehicleJourney, Line line) {
        Context producingContext = (Context) context.get(PRODUCING_CONTEXT);
        Context calendarContext = (Context) producingContext.get(CalendarProducer.LOCAL_CONTEXT);

        ServiceJourney serviceJourney = netexFactory.createServiceJourney();
        serviceJourney.setVersion(vehicleJourney.getObjectVersion() > 0 ? String.valueOf(vehicleJourney.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        String serviceJourneyId = netexId(vehicleJourney.objectIdPrefix(), SERVICE_JOURNEY, vehicleJourney.objectIdSuffix());
        serviceJourney.setId(serviceJourneyId);

        if (isSet(vehicleJourney.getPublishedJourneyName())) {
            serviceJourney.setName(getMultilingualString(vehicleJourney.getPublishedJourneyName()));
        }
        if (isSet(vehicleJourney.getPublishedJourneyIdentifier())) {
            serviceJourney.setShortName(getMultilingualString(vehicleJourney.getPublishedJourneyIdentifier()));
            serviceJourney.setPublicCode(vehicleJourney.getPublishedJourneyIdentifier());
        } else {
            serviceJourney.setShortName(getMultilingualString(vehicleJourney.objectIdSuffix()));
            serviceJourney.setPublicCode(vehicleJourney.objectIdSuffix());
        }
        if (isSet(vehicleJourney.getComment())) {
            serviceJourney.setDescription(getMultilingualString(vehicleJourney.getComment()));
        }
        if (isSet(vehicleJourney.getTransportMode())) {
            AllVehicleModesOfTransportEnumeration vehicleModeOfTransport = ConversionUtil.toVehicleModeOfTransportEnum(vehicleJourney.getTransportMode());
            serviceJourney.setTransportMode(vehicleModeOfTransport);
        }
        serviceJourney.setTransportSubmode(ConversionUtil.toTransportSubmodeStructure(vehicleJourney.getTransportSubMode()));
 
        JourneyPattern journeyPattern = vehicleJourney.getJourneyPattern();
        JourneyPatternRefStructure journeyPatternRefStruct = netexFactory.createJourneyPatternRefStructure();
        journeyPatternRefStruct.setVersion(journeyPattern.getObjectVersion() != null ? String.valueOf(journeyPattern.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        journeyPatternRefStruct.setRef(journeyPattern.getObjectId());
        serviceJourney.setJourneyPatternRef(netexFactory.createJourneyPatternRef(journeyPatternRefStruct));

        LineRefStructure lineRefStruct = netexFactory.createLineRefStructure();
        lineRefStruct.setVersion(line.getObjectVersion() != null ? String.valueOf(line.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        lineRefStruct.setRef(line.getObjectId());
        serviceJourney.setLineRef(netexFactory.createLineRef(lineRefStruct));

        Context objectContext = (Context) calendarContext.get(vehicleJourney.getObjectId());
        List<String> dayTypeIds = (List<String>) objectContext.get(CalendarProducer.DAY_TYPE_IDS);

        List<Timetable> timetables = vehicleJourney.getTimetables();
        DayTypeRefs_RelStructure dayTypeStruct = netexFactory.createDayTypeRefs_RelStructure();

        for (String dayTypeId : dayTypeIds) {
            DayTypeRefStructure dayTypeRefStruct = netexFactory.createDayTypeRefStructure();
            dayTypeRefStruct.setVersion(timetables.get(0).getObjectVersion() > 0 ? String.valueOf(timetables.get(0).getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
            dayTypeRefStruct.setRef(dayTypeId);
            dayTypeStruct.getDayTypeRef().add(netexFactory.createDayTypeRef(dayTypeRefStruct));
        }

        serviceJourney.setDayTypes(dayTypeStruct);

        if (CollectionUtils.isNotEmpty(vehicleJourney.getVehicleJourneyAtStops())) {
            List<VehicleJourneyAtStop> vehicleJourneyAtStops = vehicleJourney.getVehicleJourneyAtStops();
            vehicleJourneyAtStops.sort(Comparator.comparingInt(o -> o.getStopPoint().getPosition()));

            TimetabledPassingTimes_RelStructure passingTimesStruct = netexFactory.createTimetabledPassingTimes_RelStructure();

            for (int i = 0; i < vehicleJourneyAtStops.size(); i++) {
                VehicleJourneyAtStop vehicleJourneyAtStop = vehicleJourneyAtStops.get(i);

                TimetabledPassingTime timetabledPassingTime = netexFactory.createTimetabledPassingTime();

                StopPoint stopPoint = vehicleJourneyAtStop.getStopPoint();
                StopPointInJourneyPatternRefStructure pointInPatternRefStruct = netexFactory.createStopPointInJourneyPatternRefStructure();
                pointInPatternRefStruct.setVersion(stopPoint.getObjectVersion() > 0 ? String.valueOf(stopPoint.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

                String pointInPatternIdSuffix = stopPoint.objectIdSuffix() + "-" + stopPoint.getPosition();
                String pointInPatternIdRef = netexId(stopPoint.objectIdPrefix(), STOP_POINT_IN_JOURNEY_PATTERN, pointInPatternIdSuffix);
                pointInPatternRefStruct.setRef(pointInPatternIdRef);
                timetabledPassingTime.setPointInJourneyPatternRef(netexFactory.createStopPointInJourneyPatternRef(pointInPatternRefStruct));

                LocalTime departureTime = vehicleJourneyAtStop.getDepartureTime();
                LocalTime arrivalTime = vehicleJourneyAtStop.getArrivalTime();

                if (arrivalTime != null) {
                    if (arrivalTime.equals(departureTime)) {
                        if (!(i + 1 < vehicleJourneyAtStops.size())) {
                        	NetexTimeConversionUtil.populatePassingTimeUtc(timetabledPassingTime, true, vehicleJourneyAtStop);
                        }
                    } else {
                    	NetexTimeConversionUtil.populatePassingTimeUtc(timetabledPassingTime, true, vehicleJourneyAtStop);
                    }
                }
                if (departureTime != null) {
                    if ((i + 1 < vehicleJourneyAtStops.size())) {
                    	NetexTimeConversionUtil.populatePassingTimeUtc(timetabledPassingTime, false, vehicleJourneyAtStop);
                        timetabledPassingTime.setDepartureTime(ConversionUtil.toOffsetTimeUtc(departureTime));
                        if(vehicleJourneyAtStop.getDepartureDayOffset() > 0) {
                        	timetabledPassingTime.setDepartureDayOffset(BigInteger.valueOf(vehicleJourneyAtStop.getDepartureDayOffset()));
                        }

                    }
                }

                passingTimesStruct.getTimetabledPassingTime().add(timetabledPassingTime);
            }

            serviceJourney.setPassingTimes(passingTimesStruct);
        }

        return serviceJourney;
    }
    

}
