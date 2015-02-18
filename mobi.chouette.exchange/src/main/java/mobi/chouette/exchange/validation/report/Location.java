package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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
import mobi.chouette.model.PTNetwork;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;

@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class Location {


	@XmlElement(name = "file")
	private FileLocation file;

	@XmlElement(name = "object_id")
	private String objectId;

	@XmlElement(name = "label")
	private String name;

	@XmlElement(name = "object_path")
	private List<ObjectReference> objectRefs = new ArrayList<>();


	public Location(String fileName)
	{
		this.file = new FileLocation(fileName);
	}

	public Location(String fileName, int lineNumber, int columnNumber)
	{
		this.file = new FileLocation(fileName,lineNumber,columnNumber);
	}

	public Location(String fileName, int lineNumber, int columnNumber, String objectId)
	{
		this.file = new FileLocation(fileName,lineNumber,columnNumber);
		this.objectId = objectId;
	}

	public Location(NeptuneIdentifiedObject chouetteObject)
	{
		this.objectId = chouetteObject.getObjectId();
		this.name = chouetteObject.getName();
		if (chouetteObject instanceof VehicleJourney)
		{
			VehicleJourney object = (VehicleJourney) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getJourneyPattern()));
			objectRefs.add(new ObjectReference(object.getRoute()));
			objectRefs.add(new ObjectReference(object.getRoute().getLine()));
		}
		else if (chouetteObject instanceof JourneyPattern)
		{
			JourneyPattern object = (JourneyPattern) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getRoute()));
			objectRefs.add(new ObjectReference(object.getRoute().getLine()));
		}
		else if (chouetteObject instanceof Route)
		{
			Route object = (Route) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getLine()));
		}
		else if (chouetteObject instanceof Line)
		{
			Line object = (Line) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		}
		else if (chouetteObject instanceof AccessLink)
		{
			AccessLink object = (AccessLink) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getStopArea()));
		}
		else if (chouetteObject instanceof AccessPoint)
		{
			AccessPoint object = (AccessPoint) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getContainedIn()));
		}
		else if (chouetteObject instanceof StopArea)
		{
			StopArea object = (StopArea) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		}
		else if (chouetteObject instanceof ConnectionLink)
		{
			ConnectionLink object = (ConnectionLink) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		}
		else if (chouetteObject instanceof PTNetwork)
		{
			PTNetwork object = (PTNetwork) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		}
		else if (chouetteObject instanceof Company)
		{
			Company object = (Company) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		}
		else if (chouetteObject instanceof GroupOfLine)
		{
			GroupOfLine object = (GroupOfLine) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		}
		else if (chouetteObject instanceof Timetable)
		{
			Timetable object = (Timetable) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		}

	}

	public Location(FileLocation sourceLocation, String objectId) 
	{
		this.file = sourceLocation;
		this.objectId = objectId;
	}

}
