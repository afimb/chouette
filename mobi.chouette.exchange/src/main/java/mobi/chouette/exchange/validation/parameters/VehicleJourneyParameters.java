package mobi.chouette.exchange.validation.parameters;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import mobi.chouette.model.VehicleJourney;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlType(propOrder={"objectId", "number","publishedJourneyName","publishedJourneyIdentifier"})
public class VehicleJourneyParameters {

	@XmlTransient
	public static String[] fields = { "ObjectId", "Number","PublishedJourneyName","PublishedJourneyIdentifier"} ;
	
	static {
		ValidationParametersUtil.addFieldList(VehicleJourney.class.getSimpleName(), Arrays.asList(fields));
	}

	@XmlElement(name = "objectid")
	private FieldParameters objectId;

	@XmlElement(name = "number")
	private FieldParameters number;

	@XmlElement(name = "published_journey_name")
	private FieldParameters publishedJourneyName;

	@XmlElement(name = "published_journey_identifier")
	private FieldParameters publishedJourneyIdentifier;

}
