package mobi.chouette.exchange.netexprofile.importer.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.rutebanken.netex.model.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@ToString()
public class NetexReferential implements Serializable {

    private static final long serialVersionUID = 7552247971380782258L;

    @Getter
    @Setter
    private Map<String, Organisation> organisations = new HashMap<String, Organisation>();

    @Getter
    @Setter
    private Map<String, DayType> dayTypes = new HashMap<String, DayType>();

    @Getter
    @Setter
    private Map<String, Route> routes = new HashMap<String, Route>();

    @Getter
    @Setter
    private Map<String, Line> lines = new HashMap<String, Line>();

    @Getter
    @Setter
    private Map<String, PassengerStopAssignment> passengerStopAssignments = new HashMap<String, PassengerStopAssignment>();

    @Getter
    @Setter
    private Map<String, ScheduledStopPoint> scheduledStopPoints = new HashMap<String, ScheduledStopPoint>();

    @Getter
    @Setter
    private Map<String, JourneyPattern> journeyPatterns = new HashMap<String, JourneyPattern>();

    @Getter
    @Setter
    /**
     * TODO add generics support for more generic types here, i.e. VechicleJourney or Journey
     */
    private Map<String, ServiceJourney> serviceJourneys = new HashMap<String, ServiceJourney>();

    public void clear() {
        organisations.clear();
        dayTypes.clear();
        routes.clear();
        lines.clear();
        passengerStopAssignments.clear();
        scheduledStopPoints.clear();
        journeyPatterns.clear();
        serviceJourneys.clear();
    }

    public void dispose() {
        clear();
    }

}
