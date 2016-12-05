package mobi.chouette.exchange.netexprofile.importer.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProfileValidatorCodespace {
	@Getter
	@Setter
	String xmlns;
	@Getter
	@Setter
	String xmlnsurl;
}

