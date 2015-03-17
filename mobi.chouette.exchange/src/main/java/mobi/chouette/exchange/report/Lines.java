package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={})
@Data
public class Lines {

	@XmlElement(name = "stats")
	private LineStats stats;

	@XmlElement(name = "line_info")
	private List<LineInfo> list = new ArrayList<>();

}
