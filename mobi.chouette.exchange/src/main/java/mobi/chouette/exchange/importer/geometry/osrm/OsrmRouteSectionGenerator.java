package mobi.chouette.exchange.importer.geometry.osrm;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ejb.Singleton;

import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.importer.geometry.PolylineDecoder;
import mobi.chouette.exchange.importer.geometry.RouteSectionGenerator;
import mobi.chouette.exchange.importer.geometry.osrm.model.OsrmLeg;
import mobi.chouette.exchange.importer.geometry.osrm.model.OsrmResponse;
import mobi.chouette.exchange.importer.geometry.osrm.model.OsrmRoute;
import mobi.chouette.exchange.importer.geometry.osrm.model.OsrmStep;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;

import static mobi.chouette.common.PropertyNames.OSRM_ROUTE_SECTIONS_BASE;

/**
 * Generate route sections between two points by external osrm service.
 */
@Singleton(name = OsrmRouteSectionGenerator.BEAN_NAME)
@Log4j
public class OsrmRouteSectionGenerator implements RouteSectionGenerator {

	public static final String BEAN_NAME = "OsrmRouteSectionGenerator";

	private static final int TIMEOUT_SECONDS = 10;

	private Map<TransportModeNameEnum, String> urlPerTransportMode;

	private ObjectReader osrmResponseReader = new ObjectMapper().reader( OsrmResponse.class);
	private PolylineDecoder polylineDecoder = new PolylineDecoder();
	private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), LongLatTypeEnum.WGS84.getValue());

	@Override
	public LineString getRouteSection(OsrmRouteSectionId osrmRouteSectionId) {

		Coordinate from = osrmRouteSectionId.getFrom();
		Coordinate to = osrmRouteSectionId.getTo();
		TransportModeNameEnum transportMode = osrmRouteSectionId.getTransportMode();

		try {
			String url=getUrl(from, to, transportMode);
			if (url!=null) {
				String osrmResponseString = invokeService(url);
				if(osrmResponseString != null) {
					return mapToLineString(osrmResponseString);
				} else {
					log.info("Skipping route section generation since no route was found for request : " + url);
				}
			} else {
				log.debug("Skipping route section generation as no osrm endpoint defined for transport mode: " + transportMode);
			}
		} catch (RuntimeException re) {
			log.warn("Osrm route section generation failed: " + re.getMessage(), re);
		}
		return null;
	}

	private String getUrl(Coordinate from, Coordinate to, TransportModeNameEnum transportMode) {
		String baseUrl = getUrlPerTransportMode().get(transportMode);

		if (baseUrl == null) {
			return null;
		}
		return baseUrl + "/route/v1/driving/" + toUrlPath(from, to) + "?overview=false&steps=true&geometries=polyline";
	}

	private String toUrlPath(Coordinate from, Coordinate to) {
		return Joiner.on(",").join(from.x, from.y) + ";" + Joiner.on(",").join(to.x, to.y);
	}

	LineString mapToLineString(String osrmResponseString) {

		try {
			OsrmResponse osrmResponse = osrmResponseReader.readValue(osrmResponseString);
			if (osrmResponse != null && !CollectionUtils.isEmpty(osrmResponse.routes)) {
				Coordinate[] coordinates = osrmResponse.routes.stream().map(OsrmRoute::getLegs).filter(Objects::nonNull).flatMap(List::stream)
						.map(OsrmLeg::getSteps).filter(Objects::nonNull).flatMap(List::stream).map(OsrmStep::getGeometry)
						.map(ls -> polylineDecoder.decode(ls)).flatMap(List::stream).toArray(Coordinate[]::new);
				return geometryFactory.createLineString(coordinates);
			}
		} catch (Exception e) {
			log.warn("Failed parse osrm response: " + osrmResponseString);
		}
		return null;
	}

	/**
	 * Return the LineString in JSON format corresponding to the routing request.
	 * @param urlString the routing request.
	 * @return the LineString in JSON format or null if no route is found.
	 * @throws OsrmRouteSectionException if the service invocation fails.
	 */
	private String invokeService(String urlString) {
		log.debug("Invoking osrm route generation: " + urlString);
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setConnectTimeout(TIMEOUT_SECONDS * (int) DateUtils.MILLIS_PER_SECOND);
			connection.connect();
			int httpResponseCode = connection.getResponseCode();
			if(httpResponseCode == 200) {
				InputStream is = connection.getInputStream();
				StringWriter writer = new StringWriter();
				IOUtils.copy(is, writer);
				return writer.toString();
			} else if(httpResponseCode == 400) {
				return null;
			} else {
				throw new OsrmRouteSectionException("Osrm route section generation failed with response code " + httpResponseCode + " for url: " + urlString);
			}
		} catch (IOException e) {
			throw new OsrmRouteSectionException("Osrm route section generation failed for url: " + urlString, e);
		}
	}

	private Map<TransportModeNameEnum, String> getUrlPerTransportMode() {
		if (urlPerTransportMode == null) {
			urlPerTransportMode = new HashMap<>();
			for (TransportModeNameEnum transportMode : TransportModeNameEnum.values()) {
				String key = OSRM_ROUTE_SECTIONS_BASE + transportMode.name().toLowerCase();
				String url = System.getProperty(key);
				if (url != null) {
					urlPerTransportMode.put(transportMode, url);
				}
			}

		}

		return urlPerTransportMode;
	}
}
