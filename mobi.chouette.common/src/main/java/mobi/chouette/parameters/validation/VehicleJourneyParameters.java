package mobi.chouette.parameters.validation;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class VehicleJourneyParameters {

	@XmlElement(name = "objectid")
	private FieldParameters objectid;
	
	@XmlElement(name = "published_journey_name")
	private FieldParameters publishedJourneyName;
	
	@XmlElement(name = "number")
	private FieldParameters number;
	
	@XmlElement(name = "published_journey_identifier")
	private FieldParameters publishedJourneyIdentifier;
	
}
