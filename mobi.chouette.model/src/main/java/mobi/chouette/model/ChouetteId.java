package mobi.chouette.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;

@EqualsAndHashCode(of = { "codeSpace", "objectId" }, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
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
	private String codeSpace;

	public void setCodeSpace(String value) {
		codeSpace = StringUtils.abbreviate(value, 255);
	}
	
	@Getter
	private String objectId;

	public void setObjectId(String value) {
		objectId = StringUtils.abbreviate(value, 255);
	}
	
	@Getter
	@Setter
	private boolean shared = false;
	
}
