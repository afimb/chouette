package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;

@Log4j
public class StopAssignmentParser extends NetexParser implements Parser, Constant {

    static final String LOCAL_CONTEXT = "StopAssignment";
    static final String QUAY_ID = "quayId";

    @Override
    public void parse(Context context) throws Exception {
        StopAssignmentsInFrame_RelStructure assignmentStruct = (StopAssignmentsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

        if (assignmentStruct != null) {

            for (JAXBElement<? extends StopAssignment_VersionStructure> stopAssignmentElement : assignmentStruct.getStopAssignment()) {
                PassengerStopAssignment stopAssignment = (PassengerStopAssignment) stopAssignmentElement.getValue();
                ScheduledStopPointRefStructure scheduledStopPointRef = stopAssignment.getScheduledStopPointRef();
                QuayRefStructure quayRef = stopAssignment.getQuayRef();

                if (scheduledStopPointRef != null && quayRef != null) {
                    addQuayId(context, scheduledStopPointRef.getRef(), quayRef.getRef());
                }
            }
        }
    }

    private void addQuayId(Context context, String objectId, String quayId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(QUAY_ID, quayId);
    }

    static {
        ParserFactory.register(StopAssignmentParser.class.getName(), new ParserFactory() {
            private StopAssignmentParser instance = new StopAssignmentParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
