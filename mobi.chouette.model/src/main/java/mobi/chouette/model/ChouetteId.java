package mobi.chouette.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;

@Embeddable
@EqualsAndHashCode(of = { "codeSpace", "objectId" }, callSuper = false)
public class ChouetteId {
	/**
	 * Neptune object id <br/>
	 * composed of 3 items separated by a colon
	 * <ol>
	 * <li>codespace : an alphanumerical value (underscore accepted)</li>
	 * <li>technical id: an alphanumerical value (underscore and minus accepted)</li>
	 * <li>shared: a boolean value which determines if object id is in a shared or local referential</li>
	 * </ol>
	 * This data must be unique in dataset
	 * 
	 * @return The actual value
	 */

	@Getter
	@Column(name = "codespace", nullable = false)
	private String codeSpace;

	public void setCodeSpace(String value) {
		codeSpace = StringUtils.abbreviate(value, 255);
	}
	
	@Getter
	@Column(name = "objectid", nullable = false)
	private String objectId;

	public void setObjectId(String value) {
		objectId = StringUtils.abbreviate(value, 255);
	}
	
	@Getter
	@Setter
	@Column(name = "shared", nullable = false)
	private boolean shared = false;
}
