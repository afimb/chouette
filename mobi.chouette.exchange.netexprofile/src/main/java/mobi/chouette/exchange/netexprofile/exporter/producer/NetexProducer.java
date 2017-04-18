package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.model.*;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;

import static mobi.chouette.exchange.netexprofile.Constant.PRODUCING_CONTEXT;

public class NetexProducer {

    public static final String OBJECT_ID_SPLIT_CHAR = ":";
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
        return model == null ? null : model.objectIdPrefix() + OBJECT_ID_SPLIT_CHAR + netexModelName(model) + OBJECT_ID_SPLIT_CHAR + model.objectIdSuffix();
    }

    public static String netexId(String objectIdPrefix, String elementName, String objectIdSuffix) {
        return objectIdPrefix + OBJECT_ID_SPLIT_CHAR + elementName + OBJECT_ID_SPLIT_CHAR + objectIdSuffix;
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

    public static void resetContext(Context context) {
        Context parsingContext = (Context) context.get(PRODUCING_CONTEXT);
        if (parsingContext != null) {
            for (String key : parsingContext.keySet()) {
                Context localContext = (Context) parsingContext.get(key);
                localContext.clear();
            }
        }
    }

    public static Context getObjectContext(Context context, String localContextName, String objectId) {
        Context parsingContext = (Context) context.get(PRODUCING_CONTEXT);
        if (parsingContext == null) {
            parsingContext = new Context();
            context.put(PRODUCING_CONTEXT, parsingContext);
        }

        Context localContext = (Context) parsingContext.get(localContextName);
        if (localContext == null) {
            localContext = new Context();
            parsingContext.put(localContextName, localContext);
        }

        Context objectContext = (Context) localContext.get(objectId);
        if (objectContext == null) {
            objectContext = new Context();
            localContext.put(objectId, objectContext);
        }

        return objectContext;
    }

    public String objectIdPrefix(String objectId) {
        return objectId.split(OBJECT_ID_SPLIT_CHAR).length > 2 ? objectId.split(OBJECT_ID_SPLIT_CHAR)[0].trim() : "";
    }

    public String objectIdSuffix(String objectId) {
        return objectId.split(OBJECT_ID_SPLIT_CHAR).length > 2 ? objectId.split(OBJECT_ID_SPLIT_CHAR)[2].trim() : "";
    }

}
