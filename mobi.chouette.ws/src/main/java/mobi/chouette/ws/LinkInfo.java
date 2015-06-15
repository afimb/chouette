package mobi.chouette.ws;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.NoArgsConstructor;
import mobi.chouette.model.iev.Link;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"rel","href","type","method"})
public class LinkInfo {

	@XmlElement(name = "rel", required=true)
	private String rel;

	@XmlElement(name = "href", required=true)
	private String href;

	@XmlElement(name = "type", required=true)
	private String type;

	@XmlElement(name = "method", required=true)
	private String method;
	
	public LinkInfo(Link link,UriInfo uriInfo)
	{
		rel = link.getRel();
		href = uriInfo.getBaseUri()+link.getHref();
		type = link.getType();
		method = link.getMethod();
	}

}
