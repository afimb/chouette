package mobi.chouette.exchange.gtfs.exporter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.NoArgsConstructor;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractExportParameter;

@NoArgsConstructor
@ToString(callSuper=true)
@XmlAccessorType(XmlAccessType.FIELD)
public class GtfsExportParameters  extends AbstractExportParameter {
		
	@XmlAttribute(name = "time_zone")
	private String timeZone;
	
	@XmlAttribute(name = "object_id_prefix")
	private String objectIdPrefix;
}
