package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.netexprofile.Constant;

@Log4j
public abstract class AbstractParser implements Parser, Constant {

    @SuppressWarnings("unchecked")
    protected static Context getObjectContext(Context context, String localContextName, String objectId) {
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

    public abstract void initReferentials(Context context) throws Exception;

}
