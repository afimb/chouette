package mobi.chouette.exchange.netexprofile.exporter.producer;

import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.AVAILABILITY_CONDITION;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.rutebanken.netex.model.AvailabilityCondition;
import org.rutebanken.netex.model.ObjectFactory;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.exporter.NetexprofileExportParameters;

public class NetexProducer {

	public static final String NETEX_DEFAULT_OBJECT_VERSION = "1";

	public static ObjectFactory netexFactory = null;

	static {
		try {
			netexFactory = new ObjectFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected AvailabilityCondition createAvailabilityCondition(Context context) {

		NetexprofileExportParameters configuration = (NetexprofileExportParameters) context.get(Constant.CONFIGURATION);

		String netexId = NetexProducerUtils.createUniqueId(context, AVAILABILITY_CONDITION);
		AvailabilityCondition availabilityCondition = netexFactory.createAvailabilityCondition();
		availabilityCondition.setVersion(NETEX_DEFAULT_OBJECT_VERSION);
		availabilityCondition.setId(netexId);

		if (configuration.getStartDate() != null) {
			availabilityCondition.setFromDate(OffsetDateTime.ofInstant(configuration.getStartDate().toInstant(),ZoneId.systemDefault())); // TODO should have LocalDate
		}
		if (configuration.getEndDate() != null) {
			availabilityCondition.setToDate(OffsetDateTime.ofInstant(configuration.getEndDate().toInstant(),ZoneId.systemDefault())); // TODO should have LocalDate
		}
		
		if(configuration.getStartDate() == null && configuration.getEndDate() == null) {
			availabilityCondition.setFromDate(OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()));
		}
		
		return availabilityCondition;
	}

}
