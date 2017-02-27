package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;

import java.util.HashSet;
import java.util.Set;

import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SITE_FRAME_KEY;

public class SiteFrameProducer extends NetexProducer implements NetexFrameProducer<SiteFrame> {

    private static StopPlaceProducer stopPlaceProducer = new StopPlaceProducer();

    @Override
    public SiteFrame produce(ExportableData data) {
        Line line = data.getLine();
        Set<StopArea> stopAreas = new HashSet<>();
        stopAreas.addAll(data.getStopPlaces());
        stopAreas.addAll(data.getCommercialStops());

        String siteFrameId = netexId(line.objectIdPrefix(), SITE_FRAME_KEY, line.objectIdSuffix());

        SiteFrame siteFrame = netexFactory.createSiteFrame()
                .withVersion("any")
                .withId(siteFrameId);

        StopPlacesInFrame_RelStructure stopPlacesStruct = netexFactory.createStopPlacesInFrame_RelStructure();

        for (StopArea stopArea : stopAreas) {
            StopPlace stopPlace = stopPlaceProducer.produce(stopArea);
            stopPlacesStruct.getStopPlace().add(stopPlace);
        }

        siteFrame.setStopPlaces(stopPlacesStruct);

        return siteFrame;
    }
}
