package mobi.chouette.model.statistics;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;

@XmlRootElement(name = "lineStatistics")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "lineNumber", "lineNames", "effectivePeriods", "lines" })
@Getter
public class PublicLine implements Comparable<PublicLine> {
	private String lineNumber;
	private List<String> lineNames = new ArrayList<>();
	private List<Line> lines = new ArrayList<>();
	private List<Period> effectivePeriods = new ArrayList<>();

	@Override
	public int compareTo(PublicLine o) {
		if (lineNumber != null && o.lineNumber != null) {
			return lineNumber.compareTo(o.lineNumber);
		} else {
			return -1;
		}
	}

	public PublicLine(String number) {
		super();
		this.lineNumber = number;
	}

	public void setEffectivePeriods(List<Period> effectivePeriods) {
		this.effectivePeriods = effectivePeriods;
	}
}