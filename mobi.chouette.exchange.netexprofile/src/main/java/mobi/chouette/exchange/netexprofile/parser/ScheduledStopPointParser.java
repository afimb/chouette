package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.ScheduledStopPointValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.MultilingualString;
import no.rutebanken.netex.model.ScheduledStopPoint;
import no.rutebanken.netex.model.ScheduledStopPointsInFrame_RelStructure;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Log4j
public class ScheduledStopPointParser extends AbstractParser implements Parser {

    @Override
    public void initializeReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        ScheduledStopPointValidator validator = (ScheduledStopPointValidator) ValidatorFactory.create(ScheduledStopPointValidator.class.getName(), context);

        ScheduledStopPointsInFrame_RelStructure scheduledStopPointsStruct = (ScheduledStopPointsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<ScheduledStopPoint> scheduledStopPoints = scheduledStopPointsStruct.getScheduledStopPoint();
        for (ScheduledStopPoint scheduledStopPoint : scheduledStopPoints) {
            String objectId = scheduledStopPoint.getId();
            NetexObjectUtil.addScheduledStopPointReference(referential, objectId, scheduledStopPoint);
            validator.addObjectReference(context, scheduledStopPoint);
        }
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        Collection<ScheduledStopPoint> scheduledStopPoints = netexReferential.getScheduledStopPoints().values();

        for (ScheduledStopPoint scheduledStopPoint : scheduledStopPoints) {
            String scheduledStopPointId = scheduledStopPoint.getId();
            StopPoint stopPoint = ObjectFactory.getStopPoint(referential, scheduledStopPoint.getId());

            Map<String, Object> cachedNetexData = (Map<String, Object>) context.get(NETEX_LINE_DATA_ID_CONTEXT);
            String stopPlaceRefId = (String) cachedNetexData.get(scheduledStopPointId);
            StopArea containedInStopArea = ObjectFactory.getStopArea(referential, stopPlaceRefId);
            stopPoint.setContainedInStopArea(containedInStopArea);

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
