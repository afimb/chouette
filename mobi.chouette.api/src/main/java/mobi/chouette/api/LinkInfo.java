package mobi.chouette.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.NoArgsConstructor;
import mobi.chouette.model.api.Link;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={})
public class LinkInfo {

	@XmlElement(name = "rel")
	private String rel;

	@XmlElement(name = "href")
	private String href;

	@XmlElement(name = "type")
	private String type;

	@XmlElement(name = "method")
	private String method;
	
	public LinkInfo(Link link)
	{
		rel = link.getRel();
		href = link.getHref();
		type = link.getType();
		method = link.getMethod();
	}

}
