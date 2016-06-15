package mobi.chouette.exchange.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DataLocation {
	private String objectType; // Line route stop area..
	private String filename;
	private Integer lineNumber;
	private Integer columnNumber;
	private String objectId = "";
	private String name = "";
}
