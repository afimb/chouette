package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.JourneysInFrame_RelStructure;
import no.rutebanken.netex.model.PublicationDeliveryStructure;

import java.util.List;

@Log4j
public class VehicleJourneysParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        PublicationDeliveryStructure lineData = (PublicationDeliveryStructure) context.get(mobi.chouette.exchange.netexprofile.Constant.NETEX_LINE_DATA_JAVA);
        @SuppressWarnings("unchecked")
        List<PublicationDeliveryStructure> commonData = (List<PublicationDeliveryStructure>) context
                .get(mobi.chouette.exchange.netexprofile.Constant.NETEX_COMMON_DATA);
        Referential referential = (Referential) context.get(REFERENTIAL);
        JourneysInFrame_RelStructure contextData = (JourneysInFrame_RelStructure) context.get(mobi.chouette.exchange.netexprofile.Constant.NETEX_LINE_DATA_CONTEXT);
    }

    private void parseServiceJourney(Context context, PublicationDeliveryStructure lineData, List<PublicationDeliveryStructure> commonData,
                                         Referential referential, JourneysInFrame_RelStructure v) {
        // TODO
    }

    static {
        ParserFactory.register(VehicleJourneysParser.class.getName(), new ParserFactory() {
            private VehicleJourneysParser instance = new VehicleJourneysParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
