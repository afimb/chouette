package mobi.chouette.exchange.regtopp.importer.parser.v11;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.TransportSubModeNameEnum;

@ToString
@EqualsAndHashCode
public class TransportModePair {
	public TransportModeNameEnum transportMode;
	public TransportSubModeNameEnum subMode;
}
