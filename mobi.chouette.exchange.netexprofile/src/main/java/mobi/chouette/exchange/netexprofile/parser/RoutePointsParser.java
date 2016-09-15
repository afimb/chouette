package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.List;

@Log4j
public class RoutePointsParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        @SuppressWarnings("unchecked")
        List<PublicationDeliveryStructure> commonData = (List<PublicationDeliveryStructure>) context.get(NETEX_COMMON_DATA);
        PublicationDeliveryStructure lineData = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
        Referential referential = (Referential) context.get(REFERENTIAL);

        RoutePointsInFrame_RelStructure contextData = (RoutePointsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<RoutePoint> routePoints = contextData.getRoutePoint();
        for (RoutePoint routePoint : routePoints) {
            //parseRoutePoint(context, routePoint);
        }
    }

    private void parseRoutePoint(Context context, Referential referential, RoutePoint routePoint) {
        String id = routePoint.getId();
        Projections_RelStructure projections = routePoint.getProjections();
/*
        List<JAXBElement<PointProjection>> projectionRefOrProjection = projections.getProjectionRefOrProjection();
        for (JAXBElement<PointProjection> jaxbElement : projectionRefOrProjection) {
            // TODO
            parsePointProjection(context, referential, jaxbElement.getValue());
        }
*/
    }

    private void parsePointProjection(Context context, Referential referential, PointProjection pointProjection) {
    }

    static {
        ParserFactory.register(RoutePointsParser.class.getName(), new ParserFactory() {
            private RoutePointsParser instance = new RoutePointsParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
