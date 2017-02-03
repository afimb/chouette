package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import java.sql.Time;
import java.util.Comparator;
import java.util.List;

public class ServiceJourneyProducer extends AbstractJaxbNetexProducer<ServiceJourney, VehicleJourney> {

    //@Override
    public ServiceJourney produce(VehicleJourney vehicleJourney, Line line, boolean addExtension) {
        ServiceJourney serviceJourney = netexFactory.createServiceJourney();
        populateFromModel(serviceJourney, vehicleJourney);

        if (StringUtils.isNotEmpty(vehicleJourney.getPublishedJourneyName())) {
            serviceJourney.setName(getMultilingualString(vehicleJourney.getPublishedJourneyName()));
        }
        if (StringUtils.isNotEmpty(vehicleJourney.getPublishedJourneyIdentifier())) {
            serviceJourney.setPublicCode(vehicleJourney.getPublishedJourneyIdentifier());
        }

        JourneyPattern journeyPattern = vehicleJourney.getJourneyPattern();
        JourneyPatternRefStructure journeyPatternRefStruct = netexFactory.createJourneyPatternRefStructure();
        journeyPatternRefStruct.setVersion(journeyPattern.getObjectVersion() != null ? String.valueOf(journeyPattern.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        journeyPatternRefStruct.setRef(journeyPattern.getObjectId());
        serviceJourney.setJourneyPatternRef(netexFactory.createJourneyPatternRef(journeyPatternRefStruct));

        // TODO find out where and how to get the public code, and set here
        //serviceJourney.setPublicCode("");

        LineRefStructure lineRefStruct = netexFactory.createLineRefStructure();
        lineRefStruct.setVersion(line.getObjectVersion() != null ? String.valueOf(line.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        lineRefStruct.setRef(line.getObjectId());
        serviceJourney.setLineRef(netexFactory.createLineRef(lineRefStruct));

        // TODO set day types

        if (CollectionUtils.isNotEmpty(vehicleJourney.getVehicleJourneyAtStops())) {
            List<VehicleJourneyAtStop> vehicleJourneyAtStops = vehicleJourney.getVehicleJourneyAtStops();
            vehicleJourneyAtStops.sort(Comparator.comparingInt(o -> o.getStopPoint().getPosition()));

            Time firstStopDepartureTime = null;
            TimetabledPassingTimes_RelStructure passingTimesStruct = netexFactory.createTimetabledPassingTimes_RelStructure();

            for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourneyAtStops) {
                TimetabledPassingTime timetabledPassingTime = netexFactory.createTimetabledPassingTime();

                if (firstStopDepartureTime == null) {
                    // TODO verify that all times conforms to UTC in written netex
                    firstStopDepartureTime = vehicleJourneyAtStop.getDepartureTime();
                    serviceJourney.setDepartureTime(toOffsetTime(firstStopDepartureTime));
                }
                if (vehicleJourneyAtStop.getArrivalTime() != null) {
                    timetabledPassingTime.setArrivalTime(toOffsetTime(vehicleJourneyAtStop.getArrivalTime()));
                }
                if (vehicleJourneyAtStop.getDepartureTime() != null) {
                    timetabledPassingTime.setDepartureTime(toOffsetTime(vehicleJourneyAtStop.getDepartureTime()));
                }
                passingTimesStruct.getTimetabledPassingTime().add(timetabledPassingTime);
            }
            serviceJourney.setPassingTimes(passingTimesStruct);
        }

        return serviceJourney;
    }

}
