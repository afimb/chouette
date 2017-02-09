package mobi.chouette.exchange.netexprofile.parser;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;

public class NetexParser implements Constant {

    public static void resetContext(Context context) {
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        if (parsingContext != null) {
            for (String key : parsingContext.keySet()) {
                Context localContext = (Context) parsingContext.get(key);
                localContext.clear();
            }
        }
    }

    static Context getObjectContext(Context context, String localContextName, String objectId) {
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        if (parsingContext == null) {
            parsingContext = new Context();
            context.put(PARSING_CONTEXT, parsingContext);
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
