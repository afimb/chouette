package mobi.chouette.exchange.gtfs.exporter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractExportParameter;

@NoArgsConstructor
@ToString(callSuper=true)
@XmlAccessorType(XmlAccessType.FIELD)

public class GtfsExportParameters  extends AbstractExportParameter {
		
	@Getter @Setter
	@XmlElement(name = "time_zone",required = true)
	private String timeZone;
	
	@Getter @Setter
	@XmlElement(name = "object_id_prefix",required = true)
	private String objectIdPrefix;
}
