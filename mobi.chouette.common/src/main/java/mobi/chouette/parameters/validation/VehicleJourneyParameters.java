package mobi.chouette.parameters.validation;

import javax.xml.bind.annotation.XmlElement;

public class VehicleJourneyParameters {

	private FieldParameters objectid;
	
	private FieldParameters publishedJourneyName;
	
	private FieldParameters number;
	
	private FieldParameters publishedJourneyIdentifier;

	/**
	 * @return the objectid
	 */
	@XmlElement(name = "objectid")
	public FieldParameters getObjectid() {
		return objectid;
	}

	/**
	 * @param objectid the objectid to set
	 */
	public void setObjectid(FieldParameters objectid) {
		this.objectid = objectid;
	}

	/**
	 * @return the publishedJourneyName
	 */
	@XmlElement(name = "published_journey_name")
	public FieldParameters getPublishedJourneyName() {
		return publishedJourneyName;
	}

	/**
	 * @param publishedJourneyName the publishedJourneyName to set
	 */
	public void setPublishedJourneyName(FieldParameters publishedJourneyName) {
		this.publishedJourneyName = publishedJourneyName;
	}

	/**
	 * @return the number
	 */
	@XmlElement(name = "number")
	public FieldParameters getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(FieldParameters number) {
		this.number = number;
	}

	/**
	 * @return the publishedJourneyIdentifier
	 */
	@XmlElement(name = "published_journey_identifier")
	public FieldParameters getPublishedJourneyIdentifier() {
		return publishedJourneyIdentifier;
	}

	/**
	 * @param publishedJourneyIdentifier the publishedJourneyIdentifier to set
	 */
	public void setPublishedJourneyIdentifier(
			FieldParameters publishedJourneyIdentifier) {
		this.publishedJourneyIdentifier = publishedJourneyIdentifier;
	}
	
}
