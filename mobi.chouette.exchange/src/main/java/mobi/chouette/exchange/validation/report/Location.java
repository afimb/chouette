package mobi.chouette.exchange.validation.report;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;
import mobi.chouette.model.NeptuneIdentifiedObject;

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

	   public Location(NeptuneIdentifiedObject chouetteObject)
	   {
	      this.objectId = chouetteObject.getObjectId();
	   }

}
