package mobi.chouette.exchange.netexprofile.importer.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class IdVersion {
	@Getter
	@Setter
	String id;
	@Getter
	@Setter
	String version;
}

