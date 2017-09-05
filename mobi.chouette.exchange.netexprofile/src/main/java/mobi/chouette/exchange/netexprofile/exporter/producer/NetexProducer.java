package mobi.chouette.exchange.netexprofile.exporter.producer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
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

	public static final String NETEX_DATA_OJBECT_VERSION = "1";

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
