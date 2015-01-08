package mobi.chouette.exporter.report;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class Lines {

	@XmlElement(name = "stats")
	private LineStats stats;

	@XmlElement(name = "list")
	private List<LineItem> list;

}
