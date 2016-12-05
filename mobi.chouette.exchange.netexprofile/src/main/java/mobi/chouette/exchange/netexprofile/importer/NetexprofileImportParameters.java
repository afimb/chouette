package mobi.chouette.exchange.netexprofile.importer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractImportParameter;

@XmlRootElement(name = "netexprofile-import")
@NoArgsConstructor
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "validCodespaces" })
public class NetexprofileImportParameters extends AbstractImportParameter {

	// Comma separated list. Example "NSR,http://www.nsr.no,AVI,http://avinor.no,MOR,http://www.mor.no"
	@Getter
	@Setter
	@XmlElement(name = "valid_codespaces", required = false)
	private String validCodespaces;
	

	
}
