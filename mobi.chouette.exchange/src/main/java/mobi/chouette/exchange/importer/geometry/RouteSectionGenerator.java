package mobi.chouette.exchange.importer.geometry;

import mobi.chouette.exchange.importer.geometry.osrm.OsrmRouteSectionId;

import com.vividsolutions.jts.geom.LineString;

public interface RouteSectionGenerator {


	LineString getRouteSection(OsrmRouteSectionId osrmRouteSectionId);

}
