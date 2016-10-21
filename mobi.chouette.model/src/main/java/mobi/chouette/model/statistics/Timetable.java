package mobi.chouette.model.statistics;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;

@XmlRootElement(name = "timetable")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "id", "objectId", "periods" })
@Getter
public class Timetable {
	private Long id;
	private String objectId;
	private List<Period> periods = new ArrayList<>();

	public Timetable(Long id, String objectId) {
		super();
		this.id = id;
		this.objectId = objectId;
	}

	public void setPeriods(List<Period> periods) {
		this.periods = periods;
	}

}