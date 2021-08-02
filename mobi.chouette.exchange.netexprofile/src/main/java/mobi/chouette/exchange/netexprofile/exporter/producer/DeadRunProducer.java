package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.importer.util.NetexTimeConversionUtil;
import mobi.chouette.model.DeadRunAtStop;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalTime;
import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.DayTypeRefs_RelStructure;
import org.rutebanken.netex.model.JourneyPatternRefStructure;
import org.rutebanken.netex.model.StopPointInJourneyPatternRefStructure;
import org.rutebanken.netex.model.TimetabledPassingTime;
import org.rutebanken.netex.model.TimetabledPassingTimes_RelStructure;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

public class DeadRunProducer extends NetexProducer {

    public org.rutebanken.netex.model.DeadRun produce(Context context, mobi.chouette.model.DeadRun chouetteDeadRun, Line line) {
        ExportableData exportableData = (ExportableData) context.get(Constant.EXPORTABLE_DATA);
        ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(Constant.EXPORTABLE_NETEX_DATA);

        org.rutebanken.netex.model.DeadRun deadRun = netexFactory.createDeadRun();
        NetexProducerUtils.populateId(chouetteDeadRun, deadRun);

        JourneyPattern journeyPattern = chouetteDeadRun.getJourneyPattern();
        JourneyPatternRefStructure journeyPatternRefStruct = netexFactory.createJourneyPatternRefStructure();
        NetexProducerUtils.populateReference(journeyPattern, journeyPatternRefStruct, true);
        deadRun.setJourneyPatternRef(netexFactory.createJourneyPatternRef(journeyPatternRefStruct));


        if (chouetteDeadRun.getTimetables().size() > 0) {
            DayTypeRefs_RelStructure dayTypeStruct = netexFactory.createDayTypeRefs_RelStructure();
            deadRun.setDayTypes(dayTypeStruct);

            for (Timetable t : chouetteDeadRun.getTimetables()) {
                if (exportableData.getTimetables().contains(t)) {
                    DayTypeRefStructure dayTypeRefStruct = netexFactory.createDayTypeRefStructure();
                    NetexProducerUtils.populateReference(t, dayTypeRefStruct, false);
                    dayTypeStruct.getDayTypeRef().add(netexFactory.createDayTypeRef(dayTypeRefStruct));
                }
            }
        }

        if (CollectionUtils.isNotEmpty(chouetteDeadRun.getDeadRunAtStops())) {
            List<DeadRunAtStop> deadRunAtStops = chouetteDeadRun.getDeadRunAtStops();
            deadRunAtStops.sort(Comparator.comparingInt(o -> o.getStopPoint().getPosition()));

            TimetabledPassingTimes_RelStructure passingTimesStruct = netexFactory.createTimetabledPassingTimes_RelStructure();

            for (int i = 0; i < deadRunAtStops.size(); i++) {
                DeadRunAtStop deadRunAtStop = deadRunAtStops.get(i);

                TimetabledPassingTime timetabledPassingTime = netexFactory.createTimetabledPassingTime();
                NetexProducerUtils.populateId(deadRunAtStop, timetabledPassingTime);

                StopPoint stopPoint = deadRunAtStop.getStopPoint();
                StopPointInJourneyPatternRefStructure pointInPatternRefStruct = netexFactory.createStopPointInJourneyPatternRefStructure();
                NetexProducerUtils.populateReference(stopPoint, pointInPatternRefStruct, true);
                timetabledPassingTime.setPointInJourneyPatternRef(netexFactory.createStopPointInJourneyPatternRef(pointInPatternRefStruct));

                LocalTime departureTime = deadRunAtStop.getDepartureTime();
                LocalTime arrivalTime = deadRunAtStop.getArrivalTime();

                if (arrivalTime != null) {
                    if (arrivalTime.equals(departureTime)) {
                        if (!(i + 1 < deadRunAtStops.size())) {
                            NetexTimeConversionUtil.populatePassingTimeUtc(timetabledPassingTime, true, deadRunAtStop);
                        }
                    } else {
                        NetexTimeConversionUtil.populatePassingTimeUtc(timetabledPassingTime, true, deadRunAtStop);
                    }
                }
                if (departureTime != null) {
                    if ((i + 1 < deadRunAtStops.size())) {
                        NetexTimeConversionUtil.populatePassingTimeUtc(timetabledPassingTime, false, deadRunAtStop);
                        timetabledPassingTime.setDepartureTime(TimeUtil.toLocalTimeFromJoda(departureTime));
                        if (deadRunAtStop.getDepartureDayOffset() > 0) {
                            timetabledPassingTime.setDepartureDayOffset(BigInteger.valueOf(deadRunAtStop.getDepartureDayOffset()));
                        }

                    }
                }

                passingTimesStruct.getTimetabledPassingTime().add(timetabledPassingTime);
            }

            deadRun.setPassingTimes(passingTimesStruct);
        }

        return deadRun;
    }
}
