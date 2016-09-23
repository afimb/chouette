package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.MultilingualString;
import no.rutebanken.netex.model.ScheduledStopPoint;
import no.rutebanken.netex.model.ScheduledStopPointsInFrame_RelStructure;
import org.apache.commons.lang.StringUtils;

import java.util.List;

@Log4j
public class ScheduledStopPointParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        ScheduledStopPointsInFrame_RelStructure contextData = (ScheduledStopPointsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<ScheduledStopPoint> scheduledStopPoints = contextData.getScheduledStopPoint();
        for (ScheduledStopPoint scheduledStopPoint : scheduledStopPoints) {
            parseScheduledStopPoint(referential, scheduledStopPoint);
        }
    }

    private void parseScheduledStopPoint(Referential referential, ScheduledStopPoint scheduledStopPoint) {
        StopPoint stopPoint = ObjectFactory.getStopPoint(referential, scheduledStopPoint.getId());

        // optional
        MultilingualString scheduledStopPointName = scheduledStopPoint.getName();
        if (scheduledStopPointName != null) {
            String scheduledStopPointNameValue = scheduledStopPointName.getValue();
            if (StringUtils.isNotEmpty(scheduledStopPointNameValue)) {
                stopPoint.setComment(scheduledStopPointNameValue);
            }
        }
        stopPoint.setFilled(true);
    }

    static {
        ParserFactory.register(ScheduledStopPointParser.class.getName(), new ParserFactory() {
            private ScheduledStopPointParser instance = new ScheduledStopPointParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
