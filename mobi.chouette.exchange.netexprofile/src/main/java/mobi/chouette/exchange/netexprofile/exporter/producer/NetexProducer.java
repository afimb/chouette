package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import org.rutebanken.netex.model.AvailabilityCondition;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.regex.Pattern;

import static mobi.chouette.exchange.netexprofile.Constant.PRODUCING_CONTEXT;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.AVAILABILITY_CONDITION;

public class NetexProducer {

    public static final String NETEX_DATA_OJBECT_VERSION = "0";

    protected static final String NSR_XMLNS = "NSR";
    protected static final String NSR_XMLNSURL = "http://www.rutebanken.org/ns/nsr";
    protected static final Pattern idStructurePattern = Pattern.compile("^([A-Z]{3}):([A-Za-z]*):([0-9A-Za-z_\\-]*)$");

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

    protected AvailabilityCondition createAvailabilityCondition(mobi.chouette.model.NeptuneIdentifiedObject neptuneIdentifiedObject) {

        // TODO temporary generating random id suffix, find a better way to create object id suffixes
        String availabilityConditionId = netexId(neptuneIdentifiedObject.objectIdPrefix(), AVAILABILITY_CONDITION, String.valueOf(NetexProducerUtils.generateRandomId()));
        AvailabilityCondition availabilityCondition = netexFactory.createAvailabilityCondition();
        availabilityCondition.setVersion(neptuneIdentifiedObject.getObjectVersion() > 0 ? String.valueOf(neptuneIdentifiedObject.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        availabilityCondition.setId(availabilityConditionId);

        availabilityCondition.setFromDate(OffsetDateTime.now(ZoneId.systemDefault())); // TODO fix correct from date, for now using dummy dates
        availabilityCondition.setToDate(availabilityCondition.getFromDate().plusMonths(1L)); // TODO fix correct to date, for now using dummy dates
        return availabilityCondition;
    }

}
