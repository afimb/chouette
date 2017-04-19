package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;

import static mobi.chouette.exchange.netexprofile.Constant.PRODUCING_CONTEXT;

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

}
