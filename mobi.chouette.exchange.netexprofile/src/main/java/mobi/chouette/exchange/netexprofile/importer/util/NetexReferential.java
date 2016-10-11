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
    private Map<String, ResourceFrame> resourceFrames = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Authority> authorities = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Operator> operators = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Organisation> organisations = new HashMap<>();

    @Getter
    @Setter
    private Map<String, DayType> dayTypes = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Route> routes = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Line> lines = new HashMap<>();

    @Getter
    @Setter
    private Map<String, PassengerStopAssignment> passengerStopAssignments = new HashMap<>();

    @Getter
    @Setter
    private Map<String, ScheduledStopPoint> scheduledStopPoints = new HashMap<>();

    @Getter
    @Setter
    private Map<String, JourneyPattern> journeyPatterns = new HashMap<>();

    @Getter
    @Setter
    /**
     * TODO add generics support for more generic types here, i.e. VechicleJourney or Journey
     */
    private Map<String, ServiceJourney> serviceJourneys = new HashMap<>();

    public void clear() {
        authorities.clear();
        operators.clear();
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
