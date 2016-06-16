package mobi.chouette.exchange.gtfs.exporter.producer;

import mobi.chouette.exchange.gtfs.exporter.producer.mock.GtfsExporterMock;
import mobi.chouette.exchange.gtfs.model.RouteTypeEnum;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Company;
import mobi.chouette.model.Line;
import mobi.chouette.model.type.TransportModeNameEnum;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class GtfsRouteProducerTest {
    @Test
    public void testSave() throws Exception {
        GtfsExporterMock gtfsExporterMock = new GtfsExporterMock();

        GtfsRouteProducer routeProducer = new GtfsRouteProducer(gtfsExporterMock);
        Line line = new Line();
        line.setObjectId("id:id:id");
        line.setName("short name");
        line.setPublishedName("published name");

        Company company = new Company();
        company.setObjectId("id:id:id");
        line.setCompany(company);
        line.setTransportModeName(TransportModeNameEnum.Air);

        boolean result = routeProducer.save(line, new ActionReport(), "prefix");
        Assert.assertTrue(result);
        Assert.assertEquals(gtfsExporterMock.getExportedRoutes().size(), 1);
        Assert.assertEquals(gtfsExporterMock.getExportedRoutes().get(0).getRouteType(), RouteTypeEnum.AirService);
    }

}