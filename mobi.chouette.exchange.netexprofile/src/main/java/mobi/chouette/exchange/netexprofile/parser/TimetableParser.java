package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.*;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.List;

@Log4j
public class TimetableParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        TimetableFrame timetableFrame = (TimetableFrame) context.get(NETEX_LINE_DATA_CONTEXT);
        Timetable timetable = ObjectFactory.getTimetable(referential, timetableFrame.getId());

        // TODO: retrieve the validityConditions from netex instead
        //Period period = new Period(new Date(), new Date());
        //timetable.addPeriod(period);

        ValidityConditions_RelStructure validityConditions = timetableFrame.getValidityConditions();
        if (validityConditions != null) {
            parseValidityConditions(referential, validityConditions);
        }
        JourneysInFrame_RelStructure vehicleJourneysStruct = timetableFrame.getVehicleJourneys();
        if (vehicleJourneysStruct != null) {
            parseVehicleJourneys(referential, vehicleJourneysStruct);
        }
        referential.getTimetables().put(timetable.getObjectId(), timetable);
        timetable.setFilled(true);
    }

    private void parseValidityConditions(Referential referential, ValidityConditions_RelStructure validityConditions)  throws Exception {
        List<Object> availabilityConditionElements = validityConditions.getValidityConditionRefOrValidBetweenOrValidityCondition_();
        // should iterate all availability conditions, for now only retrieving first occurrence
/*
            for (JAXBElement<AvailabilityCondition> availabilityConditionElement : availabilityConditionElements) {
                AvailabilityCondition value = availabilityConditionElement.getValue();
            }
*/
        if (availabilityConditionElements != null && availabilityConditionElements.size() > 0) {
            AvailabilityCondition availabilityCondition = ((JAXBElement<AvailabilityCondition>) availabilityConditionElements.get(0)).getValue();
            // TODO: solve problem with jdk 8 specific classes in netex-java-model
            //java.time.OffsetDateTime fromDate = availabilityCondition.getFromDate();
        }
    }

    private void parseVehicleJourneys(Referential referential, JourneysInFrame_RelStructure vehicleJourneysStruct) throws Exception {
        List<Journey_VersionStructure> serviceJourneyStructs = vehicleJourneysStruct.getDatedServiceJourneyOrDeadRunOrServiceJourney();
        for (Journey_VersionStructure serviceJourneyStruct : serviceJourneyStructs) {
            ServiceJourney serviceJourney = (ServiceJourney) serviceJourneyStruct;
            parseServiceJourney(referential, serviceJourney);
        }
    }

    private void parseServiceJourney(Referential referential, ServiceJourney serviceJourney) throws Exception {
        VehicleJourney vehicleJourney = ObjectFactory.getVehicleJourney(referential, serviceJourney.getId());
        JourneyPattern journeyPattern = null;

        // java.time.OffsetTime departureTime = serviceJourney.getDepartureTime(); // how to handle in chouette (jdk 8 classes)?

        DayTypeRefs_RelStructure dayTypesStruct = serviceJourney.getDayTypes();
        if (dayTypesStruct != null) {
            parseDayTypes(referential, dayTypesStruct);
        }

        JAXBElement<? extends JourneyPatternRefStructure> journeyPatternRefStructElement = serviceJourney.getJourneyPatternRef();
        if (journeyPatternRefStructElement != null) {
            JourneyPatternRefStructure journeyPatternRefStructure = journeyPatternRefStructElement.getValue();
            String journeyPatternId = journeyPatternRefStructure.getRef();
            journeyPattern = ObjectFactory.getJourneyPattern(referential, journeyPatternId);
            vehicleJourney.setJourneyPattern(journeyPattern);
        }

        // String publicCode = serviceJourney.getPublicCode(); // how to handle in chouette model?

        OperatorRefStructure operatorRefStruct = serviceJourney.getOperatorRef();
        if (operatorRefStruct != null) {
            String operatorId = operatorRefStruct.getRef();
            Company company = ObjectFactory.getCompany(referential, operatorId);
            vehicleJourney.setCompany(company);
        }

        // TODO: consider if we should have a RouteRef in ServiceJourney instead of LineRef, chouette model only supports references to Routes
        JAXBElement<? extends LineRefStructure> lineRefStructElement = serviceJourney.getLineRef();
        if (lineRefStructElement != null) {
            LineRefStructure lineRefStructure = lineRefStructElement.getValue();
            String lineId = lineRefStructure.getRef();
            Line line = ObjectFactory.getLine(referential, lineId);
            //vehicleJourney.setLIne(line);
        }

        TimetabledPassingTimes_RelStructure timetabledPassingTimesStruct = serviceJourney.getPassingTimes();
        if (timetabledPassingTimesStruct != null) {
            parsePassingTimes(referential, vehicleJourney, journeyPattern, timetabledPassingTimesStruct);
        }
        vehicleJourney.setFilled(true);
    }

    private void parseDayTypes(Referential referential, DayTypeRefs_RelStructure dayTypesStruct) throws Exception {
        List<JAXBElement<? extends DayTypeRefStructure>> dayTypeRefElements = dayTypesStruct.getDayTypeRef();
        for (JAXBElement<? extends DayTypeRefStructure> dayTypeRefElement : dayTypeRefElements) {
            DayTypeRefStructure dayTypeRefStructure = dayTypeRefElement.getValue();
            String dayTypeRef = dayTypeRefStructure.getRef();
            // TODO: handle timetable daytypes here...
        }
    }

    private void parsePassingTimes(Referential referential, VehicleJourney vehicleJourney, JourneyPattern journeyPattern, TimetabledPassingTimes_RelStructure timetabledPassingTimesStruct) throws Exception {
        List<TimetabledPassingTime> timetabledPassingTimes = timetabledPassingTimesStruct.getTimetabledPassingTime();
        if (timetabledPassingTimes != null && timetabledPassingTimes.size() > 0) {
            for (TimetabledPassingTime timetabledPassingTime : timetabledPassingTimes) {
                VehicleJourneyAtStop vehicleJourneyAtStop = ObjectFactory.getVehicleJourneyAtStop();
                vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
                JAXBElement<? extends PointInJourneyPatternRefStructure> pointInJourneyPatternRefStructElement =
                        timetabledPassingTime.getPointInJourneyPatternRef();
                if (pointInJourneyPatternRefStructElement != null) {
                    PointInJourneyPatternRefStructure pointInJourneyPatternRefStruct = pointInJourneyPatternRefStructElement.getValue();
                    String pointInJourneyPatternId = pointInJourneyPatternRefStruct.getRef();
                    List<StopPoint> stopPoints = journeyPattern.getStopPoints();
                    for (StopPoint stopPoint : stopPoints) {
                        if (stopPoint.getObjectId().equals(pointInJourneyPatternId)) {
                            vehicleJourneyAtStop.setStopPoint(stopPoint);
                        }
                    }
                }
                // TODO: add arrival and departure times here, first merge with rutebanken_develop to support jdk 8, and new date/time api
            }
        }
    }

    static {
        ParserFactory.register(TimetableParser.class.getName(), new ParserFactory() {
            private TimetableParser instance = new TimetableParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
