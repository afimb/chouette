package mobi.chouette.exchange.validation.report;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mobi.chouette.exchange.report.AbstractReport;
import mobi.chouette.model.*;

import javax.xml.bind.annotation.*;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"type","id", "objectId"})
public class ObjectReference extends AbstractReport {

    @XmlType(name="referenceType")
    @XmlEnum
	public enum TYPE {
		network("Network"), company("Company"), group_of_line("GroupOfLine"), stop_area("StopArea"), stop_point(
				"StopPoint"), connection_link("ConnectionLink"), access_point("AccessPoint"), access_link("AccessLink"), time_table(
				"Timetable"), line("Line"), route("Route"), journey_pattern("JourneyPattern"), vehicle_journey(
				"VehicleJourney");

		private java.lang.String value;

		private TYPE(final java.lang.String value) {
			this.value = value;
		}

		public static TYPE fromValue(final java.lang.String value) {
			for (TYPE c : TYPE.values()) {
				if (c.value.equals(value)) {
					return c;
				}
			}
			throw new IllegalArgumentException(value);
		}
	};

    @XmlElement(name = "type",required=true)
	private TYPE type;

    @XmlElement(name = "id",required=true)
	private Long id;

    @XmlElement(name = "objectid",required=true)
	private String objectId;

	public ObjectReference(Network object) {
		this.type = TYPE.network;
		this.id = object.getId();
		if (id == null)
			this.objectId = object.getObjectId();

	}

	public ObjectReference(Company object) {
		this.type = TYPE.company;
		this.id = object.getId();
		if (id == null)
			this.objectId = object.getObjectId();
	}

	public ObjectReference(GroupOfLine object) {
		this.type = TYPE.group_of_line;
		this.id = object.getId();
		if (id == null)
			this.objectId = object.getObjectId();
	}

	public ObjectReference(StopArea object) {
		this.type = TYPE.stop_area;
		this.id = object.getId();
		if (id == null)
			this.objectId = object.getObjectId();
	}

	public ObjectReference(ConnectionLink object) {
		this.type = TYPE.connection_link;
		this.id = object.getId();
		if (id == null)
			this.objectId = object.getObjectId();
	}

	public ObjectReference(AccessPoint object) {
		this.type = TYPE.access_point;
		this.id = object.getId();
		if (id == null)
			this.objectId = object.getObjectId();
	}

	public ObjectReference(AccessLink object) {
		this.type = TYPE.access_link;
		this.id = object.getId();
		if (id == null)
			this.objectId = object.getObjectId();
	}

	public ObjectReference(Timetable object) {
		this.type = TYPE.time_table;
		this.id = object.getId();
		if (id == null)
			this.objectId = object.getObjectId();
	}

	public ObjectReference(Line object) {
		this.type = TYPE.line;
		this.id = object.getId();
		if (id == null)
			this.objectId = object.getObjectId();
	}

	public ObjectReference(Route object) {
		this.type = TYPE.route;
		this.id = object.getId();
		if (id == null)
			this.objectId = object.getObjectId();
	}

	public ObjectReference(JourneyPattern object) {
		this.type = TYPE.journey_pattern;
		this.id = object.getId();
		if (id == null)
			this.objectId = object.getObjectId();
	}

	public ObjectReference(VehicleJourney object) {
		this.type = TYPE.vehicle_journey;
		this.id = object.getId();
		if (id == null)
			this.objectId = object.getObjectId();
	}

	public ObjectReference(StopPoint object)
	{
		this.type = TYPE.stop_point;
		this.id = object.getId();
	}

	public ObjectReference(String className, String id) {
		this.type = TYPE.fromValue(className);
		this.objectId = id;
	}

	public static boolean isEligible(String className, String objectId) {
		if (objectId == null || objectId.isEmpty())
			return false;
		try {
			TYPE.fromValue(className);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

}
