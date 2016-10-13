package mobi.chouette.exchange.validation.report;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mobi.chouette.exchange.report.AbstractReport;
import mobi.chouette.exchange.validation.report.DataLocation.Path;
import mobi.chouette.model.*;
import mobi.chouette.model.util.NamingUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "file", "objectId", "name", "objectRefs" })
public class Location extends AbstractReport {

	@XmlElement(name = "file")
	private FileLocation file;

	@XmlElement(name = "objectid")
	private String objectId = "";

	@XmlElement(name = "label")
	private String name = "";

	@XmlElement(name = "object_path")
	private List<ObjectReference> objectRefs = new ArrayList<>();

	public Location(DataLocation dl) {
		if (dl.getObject() != null && dl.getObject().getId() != null) {
			init(dl.getObject());
		} else {
			this.name = dl.getName();
			this.objectId = dl.getObjectId();
			if (!dl.getPath().isEmpty()) {
				for (Path path : dl.getPath()) {
					if (ObjectReference.isEligible(path.getObjectClass(), path.getObjectId()))
						objectRefs.add(new ObjectReference(path.getObjectClass(), path.getObjectId()));
				}
			}
		}
		if (dl.getFilename() != null) {
			this.file = new FileLocation(dl);
		}
	}

	public Location(String fileName) {
		this.file = new FileLocation(fileName);
	}

	public Location(String fileName, String locationName) {
		this.file = new FileLocation(fileName);
		this.name = locationName;
	}

	public Location(String fileName, String locationName, int lineNumber, String objectId) {
		this.file = new FileLocation(fileName, lineNumber, -1);
		this.objectId = objectId;
		this.name = locationName;
	}

	public Location(String fileName, String locationName, int lineNumber, int columnNumber, String objectId) {
		this.file = new FileLocation(fileName, lineNumber, columnNumber);
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
		init(chouetteObject);
	}

	private void init(NeptuneIdentifiedObject chouetteObject) {
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

}
