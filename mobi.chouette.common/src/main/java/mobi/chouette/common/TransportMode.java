package mobi.chouette.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.apache.commons.lang.StringUtils;

@EqualsAndHashCode(of = { "mode", "subMode" }, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TransportMode {
	/**
	 * Transport Mode object <br/>
	 * composed of 2 items separated by a colon
	 * <ol>
	 * <li>mode : an alphanumerical value (underscore accepted)</li>
	 * <li>subMode: an alphanumerical value (underscore and minus accepted)</li>
	 * <li>pivotMode : an alphanumerical value (underscore accepted)</li>
	 * <li>pivotSubMode: an alphanumerical value (underscore and minus accepted)</li>
	 * </ol>
	 * This data must be unique in dataset
	 * 
	 * @return The actual value
	 */
	
	@Getter
	private String mode;

	public void setMode(String value) {
		mode = StringUtils.abbreviate(value, 255);
	}
	
	@Getter
	private String subMode;

	public void setSubMode(String value) {
		subMode = StringUtils.abbreviate(value, 255);
	}
	
	
	public String toString() {
		return mode + ":" + subMode;
	}
}
