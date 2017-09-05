package mobi.chouette.exchange.netexprofile.exporter.producer;

import static mobi.chouette.exchange.netexprofile.Constant.PRODUCING_CONTEXT;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.AVAILABILITY_CONDITION;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.rutebanken.netex.model.AvailabilityCondition;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.exporter.NetexprofileExportParameters;

public class NetexProducer {

	public static final String NETEX_DATA_OJBECT_VERSION = "0";

	protected static final String NSR_XMLNSURL = "http://www.rutebanken.org/ns/nsr";

	public static ObjectFactory netexFactory = null;

	static {
		try {
			netexFactory = new ObjectFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected MultilingualString getMultilingualString(String value) {
		if (value != null) {
			return netexFactory.createMultilingualString().withValue(value);
		} else {
			return null;
		}

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

	protected AvailabilityCondition createAvailabilityCondition(Context context) {

		NetexprofileExportParameters configuration = (NetexprofileExportParameters) context.get(Constant.CONFIGURATION);

		String availabilityConditionId = netexId(configuration.getDefaultCodespacePrefix(), AVAILABILITY_CONDITION,
				String.valueOf(NetexProducerUtils.generateSequentialId()));
		AvailabilityCondition availabilityCondition = netexFactory.createAvailabilityCondition();
		availabilityCondition.setVersion("1");
		availabilityCondition.setId(availabilityConditionId);

		if (configuration.getStartDate() != null) {
			availabilityCondition.setFromDate(OffsetDateTime.from(configuration.getStartDate().toInstant()));
		}
		if (configuration.getEndDate() != null) {
			availabilityCondition.setToDate(OffsetDateTime.from(configuration.getEndDate().toInstant()));
		}
		
		if(configuration.getStartDate() == null && configuration.getEndDate() == null) {
			availabilityCondition.setFromDate(OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()));
		}
		
		return availabilityCondition;
	}

}
