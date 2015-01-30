package mobi.chouette.exchange.validation.parameters;

import javax.xml.bind.annotation.XmlAttribute;

public class FieldParameters {

	private Integer unique;
	
	private Integer pattern;
	
	private Integer minSize;
	
	private Integer maxSize;

	/**
	 * @return the unique
	 */
	@XmlAttribute(name = "unique")
	public Integer getUnique() {
		return unique;
	}

	/**
	 * @param unique the unique to set
	 */
	public void setUnique(Integer unique) {
		this.unique = unique;
	}

	/**
	 * @return the pattern
	 */
	@XmlAttribute(name = "pattern")
	public Integer getPattern() {
		return pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(Integer pattern) {
		this.pattern = pattern;
	}

	/**
	 * @return the minSize
	 */
	@XmlAttribute(name = "min_size")
	public Integer getMinSize() {
		return minSize;
	}

	/**
	 * @param minSize the minSize to set
	 */
	public void setMinSize(Integer minSize) {
		this.minSize = minSize;
	}

	/**
	 * @return the maxSize
	 */
	@XmlAttribute(name = "max_size")
	public Integer getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize the maxSize to set
	 */
	public void setMaxSize(Integer maxSize) {
		this.maxSize = maxSize;
	}
	
}
