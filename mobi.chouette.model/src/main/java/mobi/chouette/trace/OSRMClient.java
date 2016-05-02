package mobi.chouette.trace;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import mobi.chouette.model.RouteSection;
import net.minidev.json.JSONArray;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

public class OSRMClient {
    private Logger logger = Logger.getLogger(this.getClass());

    private static final int SRID_WGS84_LATLON = 4326;

    private static String COORDINATE_JSON_PATH;  // = "routes[0].legs[0].steps[*].geometry.coordinates[*]";
    private static String OSRM_URL;              // = "http://router.project-osrm.org/route/v1/driving/{0},{1};{2},{3}?overview=false&steps=true&geometries=geojson";

    public OSRMClient() {
        if (OSRM_URL == null | COORDINATE_JSON_PATH == null) {
            init();
        }
    }


    private void init() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("osrm.properties"));
            OSRM_URL = properties.getProperty("osrm.path");
            COORDINATE_JSON_PATH = properties.getProperty("osrm.cooridinate.json.path");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void calculateRouteSectionGeometry(RouteSection routeSection) throws IOException {

        LineString inputGeometry = routeSection.getInputGeometry();

        Point from = null;
        Point to = null;

        if (inputGeometry.getNumPoints() == 2) {
            from = inputGeometry.getStartPoint();
            to = inputGeometry.getEndPoint();
        } else {
            from = createPoint(routeSection.getDeparture().getX(), routeSection.getDeparture().getY());
            to = createPoint(routeSection.getArrival().getX(), routeSection.getArrival().getY());
        }


        routeSection.setProcessedGeometry(calculateRouteSection(from, to));
    }

    private LineString calculateRouteSection(Point from, Point to) throws IOException {
        validateSrid(from);
        validateSrid(to);
        try {
            String url = MessageFormat.format(OSRM_URL, "" + from.getX(), "" + from.getY(), "" + to.getX(), "" + to.getY());

            ReadContext ctx = JsonPath.parse(getJsonResponse(url));

            List<JSONArray> coordinateJson = ctx.read(COORDINATE_JSON_PATH, List.class);

            Coordinate[] coordinates = new Coordinate[coordinateJson.size()];
            for (int i = 0; i < coordinateJson.size(); i++) {

                double x = (Double)coordinateJson.get(i).get(0);
                double y = (Double)coordinateJson.get(i).get(1);
                coordinates[i] = new Coordinate(x, y);
            }

            CoordinateSequence coordinateSeq = new CoordinateArraySequence(coordinates);
            LineString lineString = new LineString(coordinateSeq, new GeometryFactory());
            lineString.setSRID(SRID_WGS84_LATLON);

            return lineString;
        } catch (IOException e) {
            System.out.println("Exception : " + e.getMessage());
            throw e;
        }
    }

    private void validateSrid(Point point) {
        if (point.getSRID() != SRID_WGS84_LATLON) {
            if (point.getX() > -180 & point.getX() < 180 & point.getY() > -90 & point.getY() < 90) {
                //Assume coordinates are correct
                point.setSRID(SRID_WGS84_LATLON);
            } else {
                throw new RuntimeException("Point is not in WGS84");
            }
        }
    }

    private String getJsonResponse(String url) throws IOException {
        URL resource = new URL(url);
        URLConnection connection = resource.openConnection();
        BufferedReader in = new BufferedReader( new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }



    public static void main(String[] args) throws Exception {
        OSRMClient o = new OSRMClient();
        Point from = createPoint(new BigDecimal(10.399782207054326), new BigDecimal(60.75960054241328));
        Point to = createPoint(new BigDecimal(10.694989585749777), new BigDecimal(60.76338799117235));
        LineString lineString = o.calculateRouteSection(from, to);
        System.out.println(lineString.toText());
    }

    private static Point createPoint(BigDecimal x, BigDecimal y) {
        Coordinate[] coordinates = new Coordinate[1];
        coordinates[0] = new Coordinate(x.doubleValue(),y.doubleValue());
        CoordinateSequence coordinateSeq = new CoordinateArraySequence(coordinates);
        return new Point(coordinateSeq, new GeometryFactory());
    }


}
