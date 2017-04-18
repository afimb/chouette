package mobi.chouette.exchange.netexprofile.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.rutebanken.netex.model.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@ToString()
public class NetexReferential implements Serializable {

    private static final long serialVersionUID = 7552247971380782258L;

    @Getter
    @Setter
    private Map<String, StopPointInJourneyPattern> stopPointsInJourneyPattern = new HashMap<>();

    @Getter
    @Setter
    private Map<String, DayType> dayTypes = new HashMap<>();

    @Getter
    @Setter
    private Map<String, DayTypeAssignment> dayTypeAssignments = new HashMap<>();

    @Getter
    @Setter
    private Map<String, OperatingPeriod> operatingPeriods = new HashMap<>();

    @Getter
    @Setter
    private Map<String, OperatingDay> operatingDays = new HashMap<>();

    @Getter
    @Setter
    private Map<String, StopPlace> sharedStopPlaces = new HashMap<>();

    public void clear() {
        stopPointsInJourneyPattern.clear();
        dayTypes.clear();
        dayTypeAssignments.clear();
        operatingPeriods.clear();
        operatingDays.clear();
    }

    public void dispose() {
        sharedStopPlaces.clear();
    }

}
