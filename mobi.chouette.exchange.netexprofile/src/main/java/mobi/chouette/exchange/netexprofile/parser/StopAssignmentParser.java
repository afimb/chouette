package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.*;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.JAXBElement;
import java.util.List;

@Log4j
public class StopAssignmentParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        StopAssignmentsInFrame_RelStructure contextData = (StopAssignmentsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends StopAssignment_VersionStructure>> stopAssignments = contextData.getStopAssignment();
        for (JAXBElement<? extends StopAssignment_VersionStructure> stopAssignment : stopAssignments) {
            PassengerStopAssignment passengerStopAssignment = (PassengerStopAssignment) stopAssignment.getValue();
            parsePassengerStopAssignment(referential, passengerStopAssignment);
        }
    }

    private void parsePassengerStopAssignment(Referential referential, PassengerStopAssignment passengerStopAssignment) {
        // optional
        ScheduledStopPointRefStructure scheduledStopPointRef = passengerStopAssignment.getScheduledStopPointRef();
        StopPoint stopPoint = null;
        if (scheduledStopPointRef != null) {
            String scheduledStopPointRefValue= scheduledStopPointRef.getRef();
            if (StringUtils.isNotEmpty(scheduledStopPointRefValue)) {
                stopPoint = ObjectFactory.getStopPoint(referential, scheduledStopPointRefValue);
            }
        }

        // mandatory
        StopPlaceRefStructure stopPlaceRef = passengerStopAssignment.getStopPlaceRef();
        if (stopPlaceRef != null) {
            String stopPlaceRefValue= stopPlaceRef.getRef();
            if (StringUtils.isNotEmpty(stopPlaceRefValue)) {
                StopArea stopArea = ObjectFactory.getStopArea(referential, stopPlaceRefValue);
                stopPoint.setContainedInStopArea(stopArea);
            }
        }

        // mandatory
        // QuayRefStructure quayRef = passengerStopAssignment.getQuayRef(); // disabled because we do not have access to gates/quays in aviation data?

        // optional
        //PassengerStopAssignment_VersionStructure.TrainElements trainElements = passengerStopAssignment.getTrainElements(); // how to handle in chouette?
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
