package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.model.Line;
import mobi.chouette.model.Timetable;
import org.rutebanken.netex.model.ServiceCalendar;
import org.rutebanken.netex.model.ServiceCalendarFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SERVICE_CALENDAR_FRAME_KEY;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SERVICE_CALENDAR_KEY;

public class ServiceCalendarFrameProducer extends NetexProducer { //implements NetexFrameProducer<ServiceCalendarFrame> {

    //@Override
    public List<ServiceCalendarFrame> produce(ExportableData data) {
        Line line = data.getLine();
        Set<Timetable> timetables = data.getTimetables();
        List<ServiceCalendarFrame> serviceCalendarFrames = new ArrayList<>();

        for (Timetable timetable : timetables) {
            timetable.computeLimitOfPeriods();

            String serviceCalendarFrameId = netexId(line.objectIdPrefix(), SERVICE_CALENDAR_FRAME_KEY, line.objectIdSuffix());

            ServiceCalendarFrame serviceCalendarFrame = netexFactory.createServiceCalendarFrame()
                    .withVersion("any")
                    .withId(serviceCalendarFrameId);

            ServiceCalendar serviceCalendar = netexFactory.createServiceCalendar();
            serviceCalendar.setVersion(timetable.getObjectVersion() > 0 ? String.valueOf(timetable.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

            String serviceCalendarId = netexId(timetable.objectIdPrefix(), SERVICE_CALENDAR_KEY, timetable.objectIdSuffix());
            serviceCalendar.setId(serviceCalendarId);

            serviceCalendar.setFromDate(NetexProducerUtils.toOffsetDateTime(timetable.getStartOfPeriod()));
            serviceCalendar.setToDate(NetexProducerUtils.toOffsetDateTime(timetable.getEndOfPeriod()));

            serviceCalendarFrame.setServiceCalendar(serviceCalendar);

            serviceCalendarFrames.add(serviceCalendarFrame);
        }

        return serviceCalendarFrames;
    }
}
