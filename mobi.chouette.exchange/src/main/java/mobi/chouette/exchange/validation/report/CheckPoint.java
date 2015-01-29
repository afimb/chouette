package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class CheckPoint {

	@XmlAttribute(name = "severity")
	private SEVERITY severity;

	@XmlAttribute(name = "state")
	private STATE state;

	@XmlAttribute(name = "detail_count")
	private Integer detailCount;

	@XmlElement(name = "details")
	private List<Detail> details = new ArrayList<Detail>();

	public enum SEVERITY {
		WARNING, ERROR, IMPROVMENT
	};

	public enum STATE {
		UNCHECK, OK, WARNING, ERROR, FATAL
	};
}
