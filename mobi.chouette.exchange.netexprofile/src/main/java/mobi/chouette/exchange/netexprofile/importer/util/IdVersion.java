package mobi.chouette.exchange.netexprofile.importer.util;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode(of = {"id","version"})
@ToString
public class IdVersion {
	@Getter
	String id;
	@Getter
	String version;
	@Getter
	String elementName;
	@Getter
	List<String> parentElementNames;
	@Getter
	String filename;
	@Getter
	int lineNumber;
	@Getter
	int columnNumber;
	

}
