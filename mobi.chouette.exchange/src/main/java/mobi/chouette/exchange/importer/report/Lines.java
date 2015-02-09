package mobi.chouette.exchange.importer.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;


@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Lines {

	@XmlElement(name = "stats")
	private LineStats stats;

	@XmlElement(name = "list")
	private List<LineItem> list = new ArrayList<>();

}
