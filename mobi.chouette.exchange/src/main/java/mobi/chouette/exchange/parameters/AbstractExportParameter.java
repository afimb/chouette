package mobi.chouette.exchange.parameters;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "referencesType", "ids", "startDate", "endDate", "addMetadata" }, name = "actionExportParameter")
public class AbstractExportParameter extends AbstractParameter {

	@Getter
	@Setter
	@XmlElement(name = "references_type", required = true)
	private String referencesType;

	@Getter
	@Setter
	@XmlElement(name = "reference_ids")
	private List<Long> ids;

	@Getter
	@Setter
	@XmlElement(name = "start_date")
	private Date startDate;

	@Getter
	@Setter
	@XmlElement(name = "end_date")
	private Date endDate;

	@Getter
	@Setter
	@XmlElement(name = "add_metadata", defaultValue = "true")
	private boolean addMetadata = true;

	public boolean isValid(Logger log, String[] allowedTypes) {
		if (!super.isValid(log)) return false;
		
		if (startDate != null && endDate != null && startDate.after(endDate)) {
			log.error("end date before start date ");
			return false;
		}

		if (referencesType != null && !referencesType.isEmpty()) {
			if (!Arrays.asList(allowedTypes).contains(referencesType.toLowerCase())) {
				log.error("invalid type " + referencesType);
				return false;
			}
		}
		return true;
	}
}
