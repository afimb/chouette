package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.PublicationDeliveryStructure;
import no.rutebanken.netex.model.ScheduledStopPoint;
import no.rutebanken.netex.model.ScheduledStopPointsInFrame_RelStructure;

import java.util.List;

@Log4j
public class ScheduledStopPointsParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        @SuppressWarnings("unchecked")
        List<PublicationDeliveryStructure> commonData = (List<PublicationDeliveryStructure>) context.get(NETEX_COMMON_DATA);
        PublicationDeliveryStructure lineData = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
        Referential referential = (Referential) context.get(REFERENTIAL);
        ScheduledStopPointsInFrame_RelStructure contextData = (ScheduledStopPointsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<ScheduledStopPoint> scheduledStopPoints = contextData.getScheduledStopPoint();
        for (ScheduledStopPoint scheduledStopPoint : scheduledStopPoints) {

        }
    }

    private void parseScheduledStopPoint(Context context, PublicationDeliveryStructure lineData, List<PublicationDeliveryStructure> commonData,
                             Referential referential, ScheduledStopPoint v) {
        // TODO
    }

    static {
        ParserFactory.register(ScheduledStopPointsParser.class.getName(), new ParserFactory() {
            private ScheduledStopPointsParser instance = new ScheduledStopPointsParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
