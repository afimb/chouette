package mobi.chouette.exchange.netexprofile.exporter.producer;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalTime;
import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.DayTypeRefs_RelStructure;
import org.rutebanken.netex.model.JourneyPatternRefStructure;
import org.rutebanken.netex.model.LineRefStructure;
import org.rutebanken.netex.model.OperatorRefStructure;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.StopPointInJourneyPatternRefStructure;
import org.rutebanken.netex.model.TimetabledPassingTime;
import org.rutebanken.netex.model.TimetabledPassingTimes_RelStructure;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.importer.util.NetexTimeConversionUtil;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;

public class ServiceJourneyProducer extends NetexProducer {

	public ServiceJourney produce(Context context, VehicleJourney vehicleJourney, Line line) {
        ExportableData exportableData = (ExportableData) context.get(Constant.EXPORTABLE_DATA);
        ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(Constant.EXPORTABLE_NETEX_DATA);

		ServiceJourney serviceJourney = netexFactory.createServiceJourney();
		NetexProducerUtils.populateId(vehicleJourney, serviceJourney);

		serviceJourney.setName(ConversionUtil.getMultiLingualString(vehicleJourney.getPublishedJourneyName()));
		serviceJourney.setPublicCode(vehicleJourney.getPublishedJourneyIdentifier());
		serviceJourney.setDescription(ConversionUtil.getMultiLingualString(vehicleJourney.getComment()));
		serviceJourney.setTransportMode(ConversionUtil.toVehicleModeOfTransportEnum(vehicleJourney.getTransportMode()));
		serviceJourney.setTransportSubmode(ConversionUtil.toTransportSubmodeStructure(vehicleJourney.getTransportSubMode()));

		JourneyPattern journeyPattern = vehicleJourney.getJourneyPattern();
		JourneyPatternRefStructure journeyPatternRefStruct = netexFactory.createJourneyPatternRefStructure();
		NetexProducerUtils.populateReference(journeyPattern, journeyPatternRefStruct, true);
		serviceJourney.setJourneyPatternRef(netexFactory.createJourneyPatternRef(journeyPatternRefStruct));

		LineRefStructure lineRefStruct = netexFactory.createLineRefStructure();
		NetexProducerUtils.populateReference(line, lineRefStruct, true);
		serviceJourney.setLineRef(netexFactory.createLineRef(lineRefStruct));
		
		NoticeProducer.addNoticeAndNoticeAssignments(context, exportableNetexData, exportableNetexData.getNoticeAssignmentsTimetableFrame(), vehicleJourney.getFootnotes(), vehicleJourney);
		
		if (vehicleJourney.getCompany() != null) {
			OperatorRefStructure operatorRefStruct = netexFactory.createOperatorRefStructure();
			NetexProducerUtils.populateReference(vehicleJourney.getCompany(), operatorRefStruct, false);
			serviceJourney.setOperatorRef(operatorRefStruct);
		}


		if (vehicleJourney.getTimetables().size() > 0) {
			DayTypeRefs_RelStructure dayTypeStruct = netexFactory.createDayTypeRefs_RelStructure();
			serviceJourney.setDayTypes(dayTypeStruct);

			for (Timetable t : vehicleJourney.getTimetables()) {
				if (exportableData.getTimetables().contains(t)) {
					DayTypeRefStructure dayTypeRefStruct = netexFactory.createDayTypeRefStructure();
					NetexProducerUtils.populateReference(t, dayTypeRefStruct, false);
					dayTypeStruct.getDayTypeRef().add(netexFactory.createDayTypeRef(dayTypeRefStruct));
				}
			}
		}

		if (CollectionUtils.isNotEmpty(vehicleJourney.getVehicleJourneyAtStops())) {
			List<VehicleJourneyAtStop> vehicleJourneyAtStops = vehicleJourney.getVehicleJourneyAtStops();
			vehicleJourneyAtStops.sort(Comparator.comparingInt(o -> o.getStopPoint().getPosition()));

			TimetabledPassingTimes_RelStructure passingTimesStruct = netexFactory.createTimetabledPassingTimes_RelStructure();

			for (int i = 0; i < vehicleJourneyAtStops.size(); i++) {
				VehicleJourneyAtStop vehicleJourneyAtStop = vehicleJourneyAtStops.get(i);

				TimetabledPassingTime timetabledPassingTime = netexFactory.createTimetabledPassingTime();
				NetexProducerUtils.populateId(vehicleJourneyAtStop, timetabledPassingTime);

				StopPoint stopPoint = vehicleJourneyAtStop.getStopPoint();
				StopPointInJourneyPatternRefStructure pointInPatternRefStruct = netexFactory.createStopPointInJourneyPatternRefStructure();
				NetexProducerUtils.populateReference(stopPoint, pointInPatternRefStruct, true);
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
						if (vehicleJourneyAtStop.getDepartureDayOffset() > 0) {
							timetabledPassingTime.setDepartureDayOffset(BigInteger.valueOf(vehicleJourneyAtStop.getDepartureDayOffset()));
						}

					}
				}

				passingTimesStruct.getTimetabledPassingTime().add(timetabledPassingTime);
				
				NoticeProducer.addNoticeAndNoticeAssignments(context, exportableNetexData, exportableNetexData.getNoticeAssignmentsTimetableFrame(), vehicleJourneyAtStop.getFootnotes(), vehicleJourneyAtStop);
			}

			serviceJourney.setPassingTimes(passingTimesStruct);
		}

		return serviceJourney;
	}
}
