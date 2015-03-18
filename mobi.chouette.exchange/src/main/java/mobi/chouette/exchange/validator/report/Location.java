package mobi.chouette.exchange.validator.report;

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
import mobi.chouette.model.PTNetwork;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.NamingUtil;

@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"file","objectId","name","objectRefs"})
public class Location {


	@XmlElement(name = "file")
	private FileLocation file;

	@XmlElement(name = "objectid")
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
		if (chouetteObject instanceof VehicleJourney)
		{
			VehicleJourney object = (VehicleJourney) chouetteObject;
			this.name = NamingUtil.getName(object);
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getJourneyPattern()));
			objectRefs.add(new ObjectReference(object.getRoute()));
			objectRefs.add(new ObjectReference(object.getRoute().getLine()));
		}
		else if (chouetteObject instanceof JourneyPattern)
		{
			JourneyPattern object = (JourneyPattern) chouetteObject;
			this.name = NamingUtil.getName(object);
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getRoute()));
			objectRefs.add(new ObjectReference(object.getRoute().getLine()));
		}
		else if (chouetteObject instanceof Route)
		{
			Route object = (Route) chouetteObject;
			this.name = NamingUtil.getName(object);
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getLine()));
		}
		else if (chouetteObject instanceof Line)
		{
			Line object = (Line) chouetteObject;
			this.name = NamingUtil.getName(object);
			objectRefs.add(new ObjectReference(object));
		}
		else if (chouetteObject instanceof AccessLink)
		{
			AccessLink object = (AccessLink) chouetteObject;
			this.name = NamingUtil.getName(object);
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getStopArea()));
		}
		else if (chouetteObject instanceof AccessPoint)
		{
			AccessPoint object = (AccessPoint) chouetteObject;
			this.name = NamingUtil.getName(object);
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getContainedIn()));
		}
		else if (chouetteObject instanceof StopArea)
		{
			StopArea object = (StopArea) chouetteObject;
			this.name = NamingUtil.getName(object);
			objectRefs.add(new ObjectReference(object));
		}
		else if (chouetteObject instanceof ConnectionLink)
		{
			ConnectionLink object = (ConnectionLink) chouetteObject;
			this.name = NamingUtil.getName(object);
			objectRefs.add(new ObjectReference(object));
		}
		else if (chouetteObject instanceof PTNetwork)
		{
			PTNetwork object = (PTNetwork) chouetteObject;
			this.name = NamingUtil.getName(object);
			objectRefs.add(new ObjectReference(object));
		}
		else if (chouetteObject instanceof Company)
		{
			Company object = (Company) chouetteObject;
			this.name = NamingUtil.getName(object);
			objectRefs.add(new ObjectReference(object));
		}
		else if (chouetteObject instanceof GroupOfLine)
		{
			GroupOfLine object = (GroupOfLine) chouetteObject;
			this.name = NamingUtil.getName(object);
			objectRefs.add(new ObjectReference(object));
		}
		else if (chouetteObject instanceof Timetable)
		{
			Timetable object = (Timetable) chouetteObject;
			this.name = NamingUtil.getName(object);
			objectRefs.add(new ObjectReference(object));
		}

	}

	public Location(FileLocation sourceLocation, String objectId) 
	{
		this.file = sourceLocation;
		this.objectId = objectId;
	}

}
