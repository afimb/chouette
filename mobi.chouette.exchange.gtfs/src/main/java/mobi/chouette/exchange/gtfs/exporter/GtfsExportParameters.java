package mobi.chouette.exchange.gtfs.exporter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractExportParameter;

import org.apache.log4j.Logger;

import java.util.Arrays;

@XmlRootElement(name = "gtfs-export")
@NoArgsConstructor
@ToString(callSuper=true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"objectIdPrefix","timeZone", "routeTypeIdScheme"})

public class GtfsExportParameters  extends AbstractExportParameter {
		
	@Getter @Setter
	@XmlElement(name = "time_zone",required = true)
	private String timeZone;
	
	@Getter @Setter
	@XmlElement(name = "object_id_prefix",required = true)
	private String objectIdPrefix;

	@Getter@Setter
	@XmlElement(name = "route_type_id_scheme")
	private String routeTypeIdScheme;
	
	public boolean isValid(Logger log, String[] allowedTypes, String[] allowedRouteTypeIdSchemes)
	{
		if (!super.isValid(log,allowedTypes)) return false;

		if (timeZone == null || timeZone.isEmpty()) {
			log.error("missing time_zone");
			return false;
		}

		if (objectIdPrefix == null || objectIdPrefix.isEmpty()) {
			log.error("missing object_id_prefix");
			return false;
		}

		if (routeTypeIdScheme != null && !routeTypeIdScheme.isEmpty()) {
			if (!Arrays.asList(allowedRouteTypeIdSchemes).contains(routeTypeIdScheme.toLowerCase())) {
				log.error("invalid route type id scheme " + routeTypeIdScheme);
				return false;
			}
		}

		return true;
		
	}
}
