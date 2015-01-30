package mobi.chouette.exchange.neptune.importer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.parameters.AbstractParameter;

@XmlRootElement(name = "")
@XmlType (name= "neptuneImport")
public class Parameters extends AbstractParameter{

	private Boolean noSave;
	
	private ValidationParameters validation;

	/**
	 * @return the noSave
	 */
	@XmlElement(name = "no_save")
	public Boolean getNoSave() {
		return noSave;
	}

	/**
	 * @param noSave the noSave to set
	 */
	public void setNoSave(Boolean noSave) {
		this.noSave = noSave;
	}

	/**
	 * @return the validation
	 */
	@XmlElement(name = "validation")
	public ValidationParameters getValidation() {
		return validation;
	}

	/**
	 * @param validation the validation to set
	 */
	public void setValidation(ValidationParameters validation) {
		this.validation = validation;
	}
	
}
