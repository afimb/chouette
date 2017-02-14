package mobi.chouette.exchange.netexprofile.importer.util;

import org.rutebanken.netex.model.Common_VersionFrameStructure;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

public class NetexObjectUtil {

    public static void addStopPointInJourneyPatternRef(NetexReferential referential, String objectId, StopPointInJourneyPattern stopPointInJourneyPattern) {
        if (stopPointInJourneyPattern == null) {
            throw new NullPointerException("Unknown stop point in journey pattern : " + objectId);
        }
        if (!referential.getStopPointsInJourneyPattern().containsKey(objectId)) {
            referential.getStopPointsInJourneyPattern().put(objectId, stopPointInJourneyPattern);
        }
    }

    public static StopPointInJourneyPattern getStopPointInJourneyPattern(NetexReferential referential, String objectId) {
        StopPointInJourneyPattern stopPointInJourneyPattern = referential.getStopPointsInJourneyPattern().get(objectId);
        if (stopPointInJourneyPattern == null) {
            throw new NullPointerException("Unknown stop point in journey pattern : " + objectId);
        }
        return stopPointInJourneyPattern;
    }

    public static <T> List<T> getFrames(Class<T> clazz, List<JAXBElement<? extends Common_VersionFrameStructure>> dataObjectFrames) {
        List<T> foundFrames = new ArrayList<>();

        for (JAXBElement<? extends Common_VersionFrameStructure> frame : dataObjectFrames) {
            if (frame.getValue().getClass().equals(clazz)) {
                foundFrames.add(clazz.cast(frame.getValue()));
            }
        }

        return foundFrames;
    }

}
