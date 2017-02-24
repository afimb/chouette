package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.*;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;

public class NetexProducer {

    public static final String NETEX_DATA_OJBECT_VERSION = "0";

    public static ObjectFactory netexFactory = null;

    static {
        try {
            netexFactory = new ObjectFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String netexId(NeptuneIdentifiedObject model) {
        return model == null ? null : model.objectIdPrefix() + ":" + netexModelName(model) + ":" + model.objectIdSuffix();
    }

    public static String netexId(String objectIdPrefix, String elementName, String objectIdSuffix) {
        return objectIdPrefix + ":" + elementName + ":" + objectIdSuffix;
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

    protected MultilingualString getMultilingualString(String value) {
        return netexFactory.createMultilingualString()
                .withValue(value);
    }

}
