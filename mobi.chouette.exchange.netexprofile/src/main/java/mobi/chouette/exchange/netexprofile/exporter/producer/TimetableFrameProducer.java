package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.model.Line;
import mobi.chouette.model.VehicleJourney;
import org.rutebanken.netex.model.JourneysInFrame_RelStructure;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.TimetableFrame;

import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.TIMETABLE_FRAME_KEY;

public class TimetableFrameProducer extends NetexProducer implements NetexFrameProducer<TimetableFrame> {

    private static ServiceJourneyProducer serviceJourneyProducer = new ServiceJourneyProducer();

    @Override
    public TimetableFrame produce(ExportableData data) {
        Line line = data.getLine();

        String timetableFrameId = netexId(line.objectIdPrefix(), TIMETABLE_FRAME_KEY, line.objectIdSuffix());

        TimetableFrame timetableFrame = netexFactory.createTimetableFrame()
                .withVersion("any")
                .withId(timetableFrameId);

        JourneysInFrame_RelStructure journeysInFrame = netexFactory.createJourneysInFrame_RelStructure();

        for (VehicleJourney vehicleJourney : data.getVehicleJourneys()) {
            ServiceJourney serviceJourney = serviceJourneyProducer.produce(vehicleJourney, line);
            journeysInFrame.getDatedServiceJourneyOrDeadRunOrServiceJourney().add(serviceJourney);
        }

        timetableFrame.setVehicleJourneys(journeysInFrame);

        return timetableFrame;
    }
}
