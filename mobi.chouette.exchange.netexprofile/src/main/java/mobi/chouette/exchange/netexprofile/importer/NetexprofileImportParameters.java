package mobi.chouette.exchange.netexprofile.importer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractImportParameter;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "netexprofile-import")
@NoArgsConstructor
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "profileId" })
public class NetexprofileImportParameters extends AbstractImportParameter {

	@Getter
	@Setter
	@XmlElement(name = "profile_id", required = true)
	private String profileId;
}
