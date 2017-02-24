package mobi.chouette.exchange.netexprofile.exporter;

import mobi.chouette.model.*;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import org.apache.log4j.Logger;

public class ModelTranslator {

    private static final Logger logger = Logger.getLogger(ModelTranslator.class);

    public String convertToNMTOKEN(String s) {
        return s.replaceAll(" ", "-");
    }

    public static String netexId(NeptuneIdentifiedObject model) {
        return model == null ? null : model.objectIdPrefix() + ":" + netexModelName(model) + ":" + model.objectIdSuffix();
    }

    public static String netexId(String objectIdPrefix, String elementName, String objectIdSuffix) {
        return objectIdPrefix + ":" + elementName + ":" + objectIdSuffix;
    }

    public static String netexMockId(NeptuneIdentifiedObject model, String mock) {
        return model == null ? null : model.objectIdPrefix() + ":" + mock + ":" + model.objectIdSuffix();
    }

    public static String netexModelName(NeptuneIdentifiedObject model) {
        if (model == null)
            return null;
        if (model instanceof StopArea) {
            return "StopArea";
        } else if (model instanceof AccessPoint) {
            return "AccessPoint";
        } else if (model instanceof Company) {
            return "Operator";
        } else if (model instanceof AccessLink) {
            return "AccessLink";
        } else if (model instanceof StopPoint) {
            return "StopPoint";
        } else if (model instanceof Network) {
            return "GroupOfLine";
        } else if (model instanceof Line) {
            return "Line";
        } else if (model instanceof Route) {
            return "Route";
        } else if (model instanceof GroupOfLine) {
            return "GroupOfLine";
        } else if (model instanceof JourneyPattern) {
            return "JourneyPattern";
        } else if (model instanceof ConnectionLink) {
            return "ConnectionLink";
        } else if (model instanceof Timetable) {
            return "Timetable";
        } else if (model instanceof VehicleJourney) {
            return "ServiceJourney";
        } else {
            return null;
        }
    }

    public static String toTransportModeNetex(TransportModeNameEnum transportMode) {
        switch (transportMode) {
            case Air:
                return "air";
            case Train:
                return "rail";
            case LongDistanceTrain:
                return "intercityRail";
            case LongDistanceTrain_2:
                return "intercityRail";
            case LocalTrain:
                return "urbanRail";
            case RapidTransit:
                return "urbanRail";
            case Metro:
                return "metro";
            case Tramway:
                return "tram";
            case Coach:
                return "coach";
            case Bus:
                return "bus";
            case Ferry:
                return "water";
            case Waterborne:
                return "water";
            case PrivateVehicle:
                return "selfDrive";
            case Walk:
                return "selfDrive";
            case Trolleybus:
                return "trolleyBus";
            case Bicycle:
                return "selfDrive";
            case Shuttle:
                return "rail";
            case Taxi:
                return "taxi";
            case Val:
                return "rail";
            case Other:
                return "unknown";
            default:
                return "";
        }
    }

    public static TransportModeNameEnum readTransportMode(String netexMode) {
        if (netexMode == null)
            return null;
        else if (netexMode.equals("air"))
            return TransportModeNameEnum.Air;
        else if (netexMode.equals("rail"))
            return TransportModeNameEnum.Train;
        else if (netexMode.equals("intercityRail"))
            return TransportModeNameEnum.LongDistanceTrain;
        else if (netexMode.equals("urbanRail"))
            return TransportModeNameEnum.LocalTrain;
        else if (netexMode.equals("metro"))
            return TransportModeNameEnum.Metro;
        else if (netexMode.equals("tram"))
            return TransportModeNameEnum.Tramway;
        else if (netexMode.equals("coach"))
            return TransportModeNameEnum.Coach;
        else if (netexMode.equals("bus"))
            return TransportModeNameEnum.Bus;
        else if (netexMode.equals("water"))
            return TransportModeNameEnum.Ferry;
        else if (netexMode.equals("selfDrive"))
            return TransportModeNameEnum.Walk;
        else if (netexMode.equals("trolleyBus"))
            return TransportModeNameEnum.Trolleybus;
        else if (netexMode.equals("taxi"))
            return TransportModeNameEnum.Taxi;
        else if (netexMode.equals("unknown"))
            return TransportModeNameEnum.Other;
        else
            return TransportModeNameEnum.Other;
    }

    public static String toDayTypeNetex(DayTypeEnum dayType) {
        if (dayType == null)
            return null;

        switch (dayType) {
            case Monday:
                return "Monday";
            case Tuesday:
                return "Tuesday";
            case Wednesday:
                return "Wednesday";
            case Thursday:
                return "Thursday";
            case Friday:
                return "Friday";
            case Saturday:
                return "Saturday";
            case Sunday:
                return "Sunday";
            default:
                return null;
        }
    }

    public static DayTypeEnum readDayType(String dayType) {
        if (dayType == null)
            return null;
        else if (dayType.equals("Monday"))
            return DayTypeEnum.Monday;
        else if (dayType.equals("Tuesday"))
            return DayTypeEnum.Tuesday;
        else if (dayType.equals("Wednesday"))
            return DayTypeEnum.Wednesday;
        else if (dayType.equals("Thursday"))
            return DayTypeEnum.Thursday;
        else if (dayType.equals("Friday"))
            return DayTypeEnum.Friday;
        else if (dayType.equals("Saturday"))
            return DayTypeEnum.Saturday;
        else if (dayType.equals("Sunday"))
            return DayTypeEnum.Sunday;
        else
            return null;
    }

}