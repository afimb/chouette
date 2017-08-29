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
import mobi.chouette.exchange.netexprofile.util.NetexReferential;

@Log4j
public class StopAssignmentParser extends NetexParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {
		StopAssignmentsInFrame_RelStructure assignmentStruct = (StopAssignmentsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

		if (assignmentStruct != null) {

			NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);

			for (JAXBElement<? extends StopAssignment_VersionStructure> stopAssignmentElement : assignmentStruct.getStopAssignment()) {
				PassengerStopAssignment stopAssignment = (PassengerStopAssignment) stopAssignmentElement.getValue();
				ScheduledStopPointRefStructure scheduledStopPointRef = stopAssignment.getScheduledStopPointRef();
				QuayRefStructure quayRef = stopAssignment.getQuayRef();

				netexReferential.getScheduledStopPointToQuay().put(scheduledStopPointRef.getRef(), quayRef.getRef());
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
