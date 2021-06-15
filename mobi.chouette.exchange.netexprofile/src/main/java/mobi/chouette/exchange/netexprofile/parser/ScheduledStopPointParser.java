package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.type.TimingPointStatusEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.rutebanken.netex.model.ScheduledStopPointsInFrame_RelStructure;
import org.rutebanken.netex.model.TimingPointStatusEnumeration;

@Log4j
public class ScheduledStopPointParser implements Parser, Constant {
	@Override
	public void parse(Context context) throws Exception {
		Referential referential = (Referential) context.get(REFERENTIAL);
		ScheduledStopPointsInFrame_RelStructure scheduledStopPointsInFrameStruct = (ScheduledStopPointsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

		if (scheduledStopPointsInFrameStruct != null) {

			for (org.rutebanken.netex.model.ScheduledStopPoint netexScheduledStopPoint : scheduledStopPointsInFrameStruct.getScheduledStopPoint()) {

				ScheduledStopPoint scheduledStopPoint = ObjectFactory.getScheduledStopPoint(referential, netexScheduledStopPoint.getId());
				if (netexScheduledStopPoint.getName() != null) {
					scheduledStopPoint.setName(netexScheduledStopPoint.getName().getValue());
				}
				TimingPointStatusEnumeration timingPointStatus = netexScheduledStopPoint.getTimingPointStatus();
				if(timingPointStatus != null) {
					switch (timingPointStatus) {
						case TIMING_POINT:
							scheduledStopPoint.setTimingPointStatus(TimingPointStatusEnum.timingPoint);
							break;
						case NOT_TIMING_POINT:
							scheduledStopPoint.setTimingPointStatus(TimingPointStatusEnum.notTimingPoint);
							break;
						case SECONDARY_TIMING_POINT:
							scheduledStopPoint.setTimingPointStatus(TimingPointStatusEnum.secondaryTimingPoint);
							break;
						default:
							log.warn("Ignoring unknown timing point status: " + timingPointStatus);
					}
				}
			}
		}

	}

	static {
		ParserFactory.register(ScheduledStopPointParser.class.getName(),
				new ParserFactory() {
					private ScheduledStopPointParser instance = new ScheduledStopPointParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
