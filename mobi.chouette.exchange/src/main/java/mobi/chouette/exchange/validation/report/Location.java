package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
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
public class Location {

	@XmlAttribute(name = "filename")
	private String filename;

	@XmlAttribute(name = "line_number")
	private Integer lineNumber;

	@XmlAttribute(name = "column_number")
	private Integer columnNumber;
	
	@XmlAttribute(name = "object_id")
	private String objectId;

	@XmlAttribute(name = "name")
	private String name;

	@XmlElement(name = "object_path")
	private List<ObjectReference> objectRefs = new ArrayList<>();


	   public Location(String fileName)
	   {
	      this.filename = fileName;
	   }

	   public Location(String fileName, int lineNumber, int columnNumber)
	   {
	      this.filename = fileName;
	      this.lineNumber = Integer.valueOf(lineNumber);
	      this.columnNumber = Integer.valueOf(columnNumber);
	   }

	   public Location(String fileName, int lineNumber, int columnNumber, String objectId)
	   {
	      this.filename = fileName;
	      this.lineNumber = Integer.valueOf(lineNumber);
	      this.columnNumber = Integer.valueOf(columnNumber);
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

}
