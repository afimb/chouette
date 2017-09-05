package mobi.chouette.exchange.netexprofile.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.OperatingDay;
import org.rutebanken.netex.model.OperatingPeriod;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private List<DayTypeAssignment> dayTypeAssignments = new ArrayList<>();

    @Getter
    @Setter
    private Map<String, OperatingPeriod> operatingPeriods = new HashMap<>();

    @Getter
    @Setter
    private Map<String, OperatingDay> operatingDays = new HashMap<>();

    @Getter
    @Setter
    private Map<String, StopPlace> sharedStopPlaces = new HashMap<>();
    
    
    @Getter
    @Setter
    private Map<String, String> scheduledStopPointToQuay = new HashMap<>();
    
    @Getter
    public Map<String, String> groupOfLinesToNetwork = new HashMap<>();
    
    

    public void clear() {
        stopPointsInJourneyPattern.clear();
        dayTypes.clear();
        dayTypeAssignments.clear();
        operatingPeriods.clear();
        operatingDays.clear();
    }

    public void dispose() {
        sharedStopPlaces.clear();
        scheduledStopPointToQuay.clear();
        groupOfLinesToNetwork.clear();
    }

}
