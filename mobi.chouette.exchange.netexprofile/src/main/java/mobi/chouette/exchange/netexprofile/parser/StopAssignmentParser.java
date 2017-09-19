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
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class StopAssignmentParser extends NetexParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {
		StopAssignmentsInFrame_RelStructure assignmentStruct = (StopAssignmentsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

		if (assignmentStruct != null) {
			Referential referential = (Referential) context.get(REFERENTIAL);

			for (JAXBElement<? extends StopAssignment_VersionStructure> stopAssignmentElement : assignmentStruct.getStopAssignment()) {
				PassengerStopAssignment stopAssignment = (PassengerStopAssignment) stopAssignmentElement.getValue();
				ScheduledStopPointRefStructure scheduledStopPointRef = stopAssignment.getScheduledStopPointRef().getValue();
				QuayRefStructure quayRef = stopAssignment.getQuayRef();

				mobi.chouette.model.StopArea quay = ObjectFactory.getStopArea(referential, quayRef.getRef());
				if(quay.getAreaType() == null) {
					quay.setAreaType(ChouetteAreaEnum.BoardingPosition);
				}

				ScheduledStopPoint scheduledStopPoint = ObjectFactory.getScheduledStopPoint(referential, scheduledStopPointRef.getRef());
				scheduledStopPoint.setContainedInStopArea(quay);

				
			}
		}
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
