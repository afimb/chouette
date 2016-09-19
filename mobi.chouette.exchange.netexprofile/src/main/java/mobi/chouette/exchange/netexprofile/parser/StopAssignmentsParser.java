package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.PublicationDeliveryStructure;
import no.rutebanken.netex.model.StopAssignment_VersionStructure;
import no.rutebanken.netex.model.StopAssignmentsInFrame_RelStructure;

import javax.xml.bind.JAXBElement;
import java.util.List;

@Log4j
public class StopAssignmentsParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        @SuppressWarnings("unchecked")
        List<PublicationDeliveryStructure> commonData = (List<PublicationDeliveryStructure>) context.get(NETEX_COMMON_DATA);
        PublicationDeliveryStructure lineData = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
        Referential referential = (Referential) context.get(REFERENTIAL);
        StopAssignmentsInFrame_RelStructure contextData = (StopAssignmentsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends StopAssignment_VersionStructure>> stopAssignments = contextData.getStopAssignment();
        for (JAXBElement<? extends StopAssignment_VersionStructure> stopAssignment : stopAssignments) {

        }
    }

    private void parseStopAssignment(Context context, PublicationDeliveryStructure lineData, List<PublicationDeliveryStructure> commonData,
                                         Referential referential, StopAssignmentsInFrame_RelStructure v) {
        // TODO
    }

    static {
        ParserFactory.register(StopAssignmentsParser.class.getName(), new ParserFactory() {
            private StopAssignmentsParser instance = new StopAssignmentsParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
