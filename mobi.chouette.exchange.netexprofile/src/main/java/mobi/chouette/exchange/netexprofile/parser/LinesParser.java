package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.DataManagedObjectStructure;
import no.rutebanken.netex.model.LinesInFrame_RelStructure;
import no.rutebanken.netex.model.PublicationDeliveryStructure;

import javax.xml.bind.JAXBElement;
import java.util.List;

@Log4j
public class LinesParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        @SuppressWarnings("unchecked")
        List<PublicationDeliveryStructure> commonData = (List<PublicationDeliveryStructure>) context.get(NETEX_COMMON_DATA);
        PublicationDeliveryStructure lineData = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
        Referential referential = (Referential) context.get(REFERENTIAL);
        LinesInFrame_RelStructure contextData = (LinesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends DataManagedObjectStructure>> lines = contextData.getLine_();
        for (JAXBElement<? extends DataManagedObjectStructure> line : lines) {

        }
    }

    private void parseLine(Context context, PublicationDeliveryStructure lineData, List<PublicationDeliveryStructure> commonData,
                             Referential referential, JAXBElement<? extends DataManagedObjectStructure> line) {
        // TODO
    }

    static {
        ParserFactory.register(LinesParser.class.getName(), new ParserFactory() {
            private LinesParser instance = new LinesParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
