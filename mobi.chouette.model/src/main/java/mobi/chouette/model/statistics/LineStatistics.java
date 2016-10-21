package mobi.chouette.model.statistics;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@XmlRootElement(name = "lineStatistics")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "publicLines" })
@Getter
@Setter
public class LineStatistics {
	private List<PublicLine> publicLines = new ArrayList<PublicLine>();
}