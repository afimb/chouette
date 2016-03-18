package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.NamingUtil;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "file", "line", "objectId", "name", "objectRefs" })
public class Location {

	@XmlElement(name = "file")
	private FileLocation file;

	@XmlElement(name = "line")
	private LineLocation line;

	@XmlElement(name = "objectid")
	private String objectId = "";

	@XmlElement(name = "label")
	private String name = "";

	@XmlElement(name = "object_path")
	private List<ObjectReference> objectRefs = new ArrayList<>();

	public Location(String fileName) {
		this.file = new FileLocation(fileName);
	}

	public Location(String fileName, String locationName) {
		this.file = new FileLocation(fileName);
		this.name = locationName;
	}

	public Location(String fileName, String locationName, int lineNumber, String objectId) {
		this.file = new FileLocation(fileName, lineNumber, -1, objectId);
		this.objectId = objectId;
		this.name = locationName;
	}

	public Location(String fileName, String locationName, int lineNumber, int columnNumber, String objectId) {
		this.file = new FileLocation(fileName, lineNumber, columnNumber, objectId);
		this.objectId = objectId;
		this.name = locationName;
	}

	public Location(String fileName, String locationName, int lineNumber) {
		this.file = new FileLocation(fileName, lineNumber, -1);
		this.name = locationName;
	}

	public Location(String fileName, int lineNumber, int columnNumber) {
		this.file = new FileLocation(fileName, lineNumber, columnNumber);
	}

	public Location(String fileName, int lineNumber, int columnNumber, String objectId) {
		this.file = new FileLocation(fileName, lineNumber, columnNumber);
		this.objectId = objectId;
	}

	public Location(NeptuneIdentifiedObject chouetteObject) {
		this.objectId = chouetteObject.getObjectId();
		this.name = buildName(chouetteObject);
		if (chouetteObject instanceof VehicleJourney) {
			VehicleJourney object = (VehicleJourney) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getJourneyPattern()));
			objectRefs.add(new ObjectReference(object.getJourneyPattern().getRoute()));
			objectRefs.add(new ObjectReference(object.getJourneyPattern().getRoute().getLine()));
		} else if (chouetteObject instanceof JourneyPattern) {
			JourneyPattern object = (JourneyPattern) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getRoute()));
			objectRefs.add(new ObjectReference(object.getRoute().getLine()));
		} else if (chouetteObject instanceof Route) {
			Route object = (Route) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getLine()));
		} else if (chouetteObject instanceof Line) {
			Line object = (Line) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof AccessLink) {
			AccessLink object = (AccessLink) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getAccessPoint()));
			objectRefs.add(new ObjectReference(object.getAccessPoint().getContainedIn()));
		} else if (chouetteObject instanceof AccessPoint) {
			AccessPoint object = (AccessPoint) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getContainedIn()));
		} else if (chouetteObject instanceof StopArea) {
			StopArea object = (StopArea) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof ConnectionLink) {
			ConnectionLink object = (ConnectionLink) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof Network) {
			Network object = (Network) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof Company) {
			Company object = (Company) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof GroupOfLine) {
			GroupOfLine object = (GroupOfLine) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof Timetable) {
			Timetable object = (Timetable) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		}

	}

	public Location(FileLocation sourceLocation, String objectId) {
		this.file = sourceLocation;
		this.objectId = objectId;
	}

	public Location(FileLocation sourceLocation, NeptuneIdentifiedObject chouetteObject) {
		this.file = sourceLocation;
		this.objectId = chouetteObject.getObjectId();
		this.name = buildName(chouetteObject);
		addLineLocation(this,  chouetteObject);
	}

	public static String buildName(NeptuneIdentifiedObject chouetteObject) {
		if (chouetteObject instanceof VehicleJourney) {
			VehicleJourney object = (VehicleJourney) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof JourneyPattern) {
			JourneyPattern object = (JourneyPattern) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof Route) {
			Route object = (Route) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof Line) {
			Line object = (Line) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof AccessLink) {
			AccessLink object = (AccessLink) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof AccessPoint) {
			AccessPoint object = (AccessPoint) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof StopArea) {
			StopArea object = (StopArea) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof ConnectionLink) {
			ConnectionLink object = (ConnectionLink) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof Network) {
			Network object = (Network) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof Company) {
			Company object = (Company) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof GroupOfLine) {
			GroupOfLine object = (GroupOfLine) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof Timetable) {
			Timetable object = (Timetable) chouetteObject;
			return NamingUtil.getName(object);
		}
		return "unnammed";
	}

	public static void addLineLocation(Location loc, NeptuneIdentifiedObject chouetteObject) {
		if (loc.getLine() != null) return;
		Line line = null;
		try {
			if (chouetteObject instanceof VehicleJourney) {
				VehicleJourney object = (VehicleJourney) chouetteObject;
				line = object.getRoute().getLine();
			} else if (chouetteObject instanceof JourneyPattern) {
				JourneyPattern object = (JourneyPattern) chouetteObject;
				line = object.getRoute().getLine();
			} else if (chouetteObject instanceof StopPoint) {
				StopPoint object = (StopPoint) chouetteObject;
				line = object.getRoute().getLine();
			} else if (chouetteObject instanceof Route) {
				Route object = (Route) chouetteObject;
				line = object.getLine();
			} else if (chouetteObject instanceof Line) {
				line = (Line) chouetteObject;
			}
		} catch (NullPointerException ex) {
			// ignore line path
		}
		if (line != null )
	       loc.setLine(new LineLocation(line));

	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		if (file != null)
			object.put("file", file.toJson());
		if (line != null)
			object.put("line", line.toJson());
		if (objectId != null)
			object.put("objectid", objectId);
		if (name != null)
			object.put("label", name);
		if (!objectRefs.isEmpty()) {
			JSONArray paths = new JSONArray();
			object.put("object_path", paths);
			for (ObjectReference ref : objectRefs) {
				paths.put(ref.toJson());
			}
		}
		return object;
	}

}
