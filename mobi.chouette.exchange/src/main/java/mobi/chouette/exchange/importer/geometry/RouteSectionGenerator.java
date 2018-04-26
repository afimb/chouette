package mobi.chouette.exchange.importer.geometry;

import mobi.chouette.model.type.TransportModeNameEnum;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

public interface RouteSectionGenerator {


	LineString getRouteSection(Coordinate from, Coordinate to, TransportModeNameEnum transportMode);

}
