package mobi.chouette.exchange.netexprofile.parser;

import javax.xml.bind.JAXBElement;

import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.QuayRefStructure;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.StopAssignment_VersionStructure;
import org.rutebanken.netex.model.StopAssignmentsInFrame_RelStructure;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.Referential;

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
        
		Referential referential = (Referential) context.get(REFERENTIAL);
		StopArea result = referential.getSharedStopAreas().get(quayId);
		if (result == null) {
			result = new StopArea();
			result.setObjectId(quayId);
			result.setDetached(true);
			result.setAreaType(ChouetteAreaEnum.BoardingPosition);
			referential.getSharedStopAreas().put(quayId, result);
		} 
		if (!referential.getStopAreas().containsKey(quayId)) {
			referential.getStopAreas().put(quayId, result);
		}
        
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
