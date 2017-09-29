package mobi.chouette.exchange.transfer.exporter;

import java.util.List;

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

@XmlRootElement(name = "transfer-export")
@NoArgsConstructor
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "destReferentialName"})
public class TransferExportParameters extends AbstractExportParameter{

	@Getter
	@Setter
	@XmlElement(name = "dest_referential_name", required = true)
	private String destReferentialName;

	@Override
	public List<String> getAdditionalRequiredReferentialLocks() {
		List<String> requiredLocks= super.getAdditionalRequiredReferentialLocks();
		requiredLocks.add(destReferentialName);
		return requiredLocks;
	}
}
