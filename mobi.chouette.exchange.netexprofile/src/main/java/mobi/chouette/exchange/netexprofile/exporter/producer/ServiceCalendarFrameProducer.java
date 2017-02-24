package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.model.Line;
import org.rutebanken.netex.model.ServiceCalendarFrame;

import static mobi.chouette.exchange.netexprofile.exporter.ModelTranslator.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SERVICE_CALENDAR_FRAME_KEY;

public class ServiceCalendarFrameProducer extends NetexProducer implements NetexFrameProducer<ServiceCalendarFrame> {

    @Override
    public ServiceCalendarFrame produce(ExportableData data) {
        Line line = data.getLine();

        String serviceCalendarFrameId = netexId(line.objectIdPrefix(), SERVICE_CALENDAR_FRAME_KEY, line.objectIdSuffix());

        ServiceCalendarFrame serviceCalendarFrame = netexFactory.createServiceCalendarFrame()
                .withVersion("any")
                .withId(serviceCalendarFrameId);

        return serviceCalendarFrame;
    }
}
