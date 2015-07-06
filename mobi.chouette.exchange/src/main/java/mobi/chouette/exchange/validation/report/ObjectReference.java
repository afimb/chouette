package mobi.chouette.exchange.validation.report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.ToString;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"type","id"})
public class ObjectReference {

	@XmlType(name="referenceType")
	@XmlEnum
	public enum TYPE  {
		network, 
		company, 
		group_of_line, 
		stop_area, 
		connection_link, 
		access_point, 
		access_link, 
		time_table,
		line,
		route,
		journey_pattern,
		vehicle_journey
	};

	@XmlElement(name = "type",required=true)
	private TYPE type;

	@XmlElement(name = "id",required=true)
	private Long id;

	public ObjectReference(Network object)
	{
		this.type = TYPE.network;
		this.id = object.getId();
	}

	public ObjectReference(Company object)
	{
		this.type = TYPE.company;
		this.id = object.getId();
	}
	public ObjectReference(GroupOfLine object)
	{
		this.type = TYPE.group_of_line;
		this.id = object.getId();
	}
	public ObjectReference(StopArea object)
	{
		this.type = TYPE.stop_area;
		this.id = object.getId();
	}
	public ObjectReference(ConnectionLink object)
	{
		this.type = TYPE.connection_link;
		this.id = object.getId();
	}
	public ObjectReference(AccessPoint object)
	{
		this.type = TYPE.access_point;
		this.id = object.getId();
	}
	public ObjectReference(AccessLink object)
	{
		this.type = TYPE.access_link;
		this.id = object.getId();
	}
	public ObjectReference(Timetable object)
	{
		this.type = TYPE.time_table;
		this.id = object.getId();
	}
	public ObjectReference(Line object)
	{
		this.type = TYPE.line;
		this.id = object.getId();
	}
	public ObjectReference(Route object)
	{
		this.type = TYPE.route;
		this.id = object.getId();
	}
	public ObjectReference(JourneyPattern object)
	{
		this.type = TYPE.journey_pattern;
		this.id = object.getId();
	}
	public ObjectReference(VehicleJourney object)
	{
		this.type = TYPE.vehicle_journey;
		this.id = object.getId();
	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("type", type);
		object.put("id", id);
		return object;
	}

}
