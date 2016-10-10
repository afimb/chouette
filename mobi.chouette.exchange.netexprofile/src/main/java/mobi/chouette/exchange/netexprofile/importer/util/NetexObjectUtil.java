package mobi.chouette.exchange.netexprofile.importer.util;

import no.rutebanken.netex.model.*;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO add common methods for extracting various types of objects from netex objects...
public class NetexObjectUtil {

    public static void addAuthorityReference(NetexReferential referential, String objectId, Authority authority) {
        if (authority == null) {
            throw new NullPointerException("Unknown authority : " + objectId);
        }
        if (!referential.getAuthorities().containsKey(objectId)) {
            referential.getAuthorities().put(objectId, authority);
        }
    }

    public static Authority getAuthority(NetexReferential referential, String objectId) {
        Authority authority = referential.getAuthorities().get(objectId);
        if (authority == null) {
            throw new NullPointerException("Unknown authority : " + objectId);
        }
        return authority;
    }

    public static void addOperatorReference(NetexReferential referential, String objectId, Operator operator) {
        if (operator == null) {
            throw new NullPointerException("Unknown operator : " + objectId);
        }
        if (!referential.getOperators().containsKey(objectId)) {
            referential.getOperators().put(objectId, operator);
        }
    }

    public static Operator getOperator(NetexReferential referential, String objectId) {
        Operator operator = referential.getOperators().get(objectId);
        if (operator == null) {
            throw new NullPointerException("Unknown operator : " + objectId);
        }
        return operator;
    }

    public static void addOrganisationReference(NetexReferential referential, String objectId, Organisation organisation) {
        if (organisation == null) {
            throw new NullPointerException("Unknown organisation : " + objectId);
        }
        if (!referential.getOrganisations().containsKey(objectId)) {
            referential.getOrganisations().put(objectId, organisation);
        }
    }

    public static Organisation getOrganisation(NetexReferential referential, String objectId) {
        Organisation organisation = referential.getOrganisations().get(objectId);
        if (organisation == null) {
            throw new NullPointerException("Unknown organisation : " + objectId);
        }
        return organisation;
    }

    public static void addDayTypeReference(NetexReferential referential, String objectId, DayType dayType) {
        if (dayType == null) {
            throw new NullPointerException("Unknown day type : " + objectId);
        }
        if (!referential.getDayTypes().containsKey(objectId)) {
            referential.getDayTypes().put(objectId, dayType);
        }
    }

    public static DayType getDayTypes(NetexReferential referential, String objectId) {
        DayType dayType = referential.getDayTypes().get(objectId);
        if (dayType == null) {
            throw new NullPointerException("Unknown day type : " + objectId);
        }
        return dayType;
    }

    public static void addRouteReference(NetexReferential referential, String objectId, Route route) {
        if (route == null) {
            throw new NullPointerException("Unknown route : " + objectId);
        }
        if (!referential.getRoutes().containsKey(objectId)) {
            referential.getRoutes().put(objectId, route);
        }
    }

    public static Route getRoute(NetexReferential referential, String objectId) {
        Route route = referential.getRoutes().get(objectId);
        if (route == null) {
            throw new NullPointerException("Unknown route : " + objectId);
        }
        return route;
    }

    public static void addLineReference(NetexReferential referential, String objectId, Line line) {
        if (line == null) {
            throw new NullPointerException("Unknown line : " + objectId);
        }
        if (!referential.getLines().containsKey(objectId)) {
            referential.getLines().put(objectId, line);
        }
    }

    public static Line getLine(NetexReferential referential, String objectId) {
        Line line = referential.getLines().get(objectId);
        if (line == null) {
            throw new NullPointerException("Unknown line : " + objectId);
        }
        return line;
    }

    public static String getOperatorRefOfLine(Line line) {
        OperatorRefStructure operatorRefStruct = line.getOperatorRef();
        return operatorRefStruct == null ? null : operatorRefStruct.getRef();
    }

    public static List<String> getRouteRefsOfLine(Line line) {
        List<String> routeIds = new ArrayList<>();
        RouteRefs_RelStructure routeRefsStruct = line.getRoutes();
        if (routeRefsStruct == null) {
            return Collections.emptyList();
        } else {
            List<RouteRefStructure> routeRefs = routeRefsStruct.getRouteRef();
            for (RouteRefStructure routeRef : routeRefs) {
                if (routeRef != null && StringUtils.isNotEmpty(routeRef.getRef().trim())) {
                    String routeId = routeRef.getRef().trim();
                    routeIds.add(routeId);
                }
            }
        }
        return routeIds;
    }


    public static void addPassengerStopAssignmentReference(NetexReferential referential, String objectId, PassengerStopAssignment stopAssignment) {
        if (stopAssignment == null) {
            throw new NullPointerException("Unknown stop assignment : " + objectId);
        }
        if (!referential.getPassengerStopAssignments().containsKey(objectId)) {
            referential.getPassengerStopAssignments().put(objectId, stopAssignment);
        }
    }

    public static PassengerStopAssignment getPassengerStopAssignment(NetexReferential referential, String objectId) {
        PassengerStopAssignment stopAssignment = referential.getPassengerStopAssignments().get(objectId);
        if (stopAssignment == null) {
            throw new NullPointerException("Unknown stop assignment : " + objectId);
        }
        return stopAssignment;
    }

    public static void addScheduledStopPointReference(NetexReferential referential, String objectId, ScheduledStopPoint scheduledStopPoint) {
        if (scheduledStopPoint == null) {
            throw new NullPointerException("Unknown scheduled stop point : " + objectId);
        }
        if (!referential.getScheduledStopPoints().containsKey(objectId)) {
            referential.getScheduledStopPoints().put(objectId, scheduledStopPoint);
        }
    }

    public static ScheduledStopPoint getScheduledStopPoint(NetexReferential referential, String objectId) {
        ScheduledStopPoint scheduledStopPoint = referential.getScheduledStopPoints().get(objectId);
        if (scheduledStopPoint == null) {
            throw new NullPointerException("Unknown scheduled stop point : " + objectId);
        }
        return scheduledStopPoint;
    }

    public static void addJourneyPatternReference(NetexReferential referential, String objectId, JourneyPattern journeyPattern) {
        if (journeyPattern == null) {
            throw new NullPointerException("Unknown journey pattern : " + objectId);
        }
        if (!referential.getJourneyPatterns().containsKey(objectId)) {
            referential.getJourneyPatterns().put(objectId, journeyPattern);
        }
    }

    public static JourneyPattern getJourneyPattern(NetexReferential referential, String objectId) {
        JourneyPattern journeyPattern = referential.getJourneyPatterns().get(objectId);
        if (journeyPattern == null) {
            throw new NullPointerException("Unknown journey pattern : " + objectId);
        }
        return journeyPattern;
    }

    public static void addServiceJourneyReference(NetexReferential referential, String objectId, ServiceJourney serviceJourney) {
        if (serviceJourney == null) {
            throw new NullPointerException("Unknown service journey: " + objectId);
        }
        if (!referential.getServiceJourneys().containsKey(objectId)) {
            referential.getServiceJourneys().put(objectId, serviceJourney);
        }
    }

    public static ServiceJourney getServiceJourney(NetexReferential referential, String objectId) {
        ServiceJourney serviceJourney = referential.getServiceJourneys().get(objectId);
        if (serviceJourney == null) {
            throw new NullPointerException("Unknown service journey : " + objectId);
        }
        return serviceJourney;
    }

}
