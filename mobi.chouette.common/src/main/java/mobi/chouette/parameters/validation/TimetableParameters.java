package mobi.chouette.parameters.validation;

import javax.xml.bind.annotation.XmlElement;

public class TimetableParameters {

	private FieldParameters objectid;
	
	private FieldParameters comment;
	
	private FieldParameters version;

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
	 * @return the comment
	 */
	@XmlElement(name = "comment")
	public FieldParameters getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(FieldParameters comment) {
		this.comment = comment;
	}

	/**
	 * @return the version
	 */
	@XmlElement(name = "version")
	public FieldParameters getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(FieldParameters version) {
		this.version = version;
	}
	
}
