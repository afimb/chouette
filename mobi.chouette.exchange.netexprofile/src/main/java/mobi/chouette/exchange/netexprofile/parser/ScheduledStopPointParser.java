package mobi.chouette.exchange.netexprofile.parser;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.rutebanken.netex.model.ScheduledStopPointsInFrame_RelStructure;

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
