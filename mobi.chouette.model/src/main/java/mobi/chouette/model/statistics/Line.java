package mobi.chouette.model.statistics;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;


@XmlRootElement(name = "line")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "id", "objectId", "name", "timetables" })
@Getter
public class Line {
	private Long id;
	public Line(Long id, String objectId, String name) {
		super();
		this.id = id;
		this.objectId = objectId;
		this.name = name;
		
	}
	private String objectId;
	private String name;
	private List<Timetable> timetables = new ArrayList<>();
}
