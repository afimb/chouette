package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.LinkSequence_VersionStructure;
import no.rutebanken.netex.model.PublicationDeliveryStructure;
import no.rutebanken.netex.model.Route;
import no.rutebanken.netex.model.RoutesInFrame_RelStructure;

import javax.xml.bind.JAXBElement;
import java.util.List;

@Log4j
public class RoutesParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        @SuppressWarnings("unchecked")
        List<PublicationDeliveryStructure> commonData = (List<PublicationDeliveryStructure>) context.get(NETEX_COMMON_DATA);
        PublicationDeliveryStructure lineData = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
        Referential referential = (Referential) context.get(REFERENTIAL);
        RoutesInFrame_RelStructure contextData = (RoutesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
    }

    private void parseRoute(Context context, PublicationDeliveryStructure lineData, List<PublicationDeliveryStructure> commonData,
                                 Referential referential, JAXBElement<Route> route) {
        //mobi.chouette.model.Route chouetteRoute = ObjectFactory.getRoute(referential, route.getId());
        //chouetteRoute.setName(route.getName().getValue());
    }

    static {
        ParserFactory.register(RoutesParser.class.getName(), new ParserFactory() {
            private RoutesParser instance = new RoutesParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
