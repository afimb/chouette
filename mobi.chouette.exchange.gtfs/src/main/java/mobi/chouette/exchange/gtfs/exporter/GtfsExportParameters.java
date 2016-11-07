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

@XmlRootElement(name = "gtfs-export")
@NoArgsConstructor
@ToString(callSuper=true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"objectIdPrefix","timeZone","keepOriginalId"})

public class GtfsExportParameters  extends AbstractExportParameter {
		
	@Getter @Setter
	@XmlElement(name = "time_zone",required = true)
	private String timeZone;
	
	@Getter @Setter
	@XmlElement(name = "object_id_prefix",required = true)
	private String objectIdPrefix;
	
	@Getter @Setter
	@XmlElement(name = "keep_original_id",required = false)
	private boolean keepOriginalId = false;
	
	public boolean isValid(Logger log, String[] allowedTypes)
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

		return true;
		
	}
}
