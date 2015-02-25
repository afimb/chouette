package mobi.chouette.model.api;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
public class Link implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final String GET_METHOD = "get";
	public static final String POST_METHOD = "post";
	public static final String PUT_METHOD = "put";
	public static final String DELETE_METHOD = "delete";

	public static final String LOCATION_REL = "location";
	public static final String CANCEL_REL = "cancel";
	public static final String DELETE_REL = "delete";
	//public static final String DOWNLOAD_REL = "download";
	
	public static final String PARAMETERS_REL = "parameters";
	public static final String DATA_REL = "data";
	public static final String VALIDATION_REL = "validation";

	@Column(name = "rel")
	@Getter
	@Setter
	@XmlAttribute(name = "rel")
	private String rel;

	@Column(name = "href")
	@Getter
	@Setter
	@XmlAttribute(name = "href")
	private String href;

	@Column(name = "type")
	@Getter
	@Setter
	@XmlAttribute(name = "type")
	private String type;

	@Column(name = "method")
	@Getter
	@Setter
	@XmlAttribute(name = "method")
	private String method;

}
