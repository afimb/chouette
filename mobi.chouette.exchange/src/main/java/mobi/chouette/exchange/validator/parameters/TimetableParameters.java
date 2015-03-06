package mobi.chouette.exchange.validator.parameters;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;
import mobi.chouette.model.Timetable;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class TimetableParameters {

	@XmlTransient
	public static String[] fields = { "ObjectId", "Comment", "Version"} ;
	
	static {
		ValidationParametersUtil.addFieldList(Timetable.class.getSimpleName(), Arrays.asList(fields));
	}

	@XmlElement(name = "objectid")
	private FieldParameters objectId;

	@XmlElement(name = "comment")
	private FieldParameters comment;

	@XmlElement(name = "version")
	private FieldParameters version;

}
