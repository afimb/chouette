package mobi.chouette.exchange.regtopp.importer.parser;

import java.util.List;

import lombok.Setter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.regtopp.importer.parser.v11.TransportModePair;

public abstract class LineSpecificParser implements Parser{
	@Setter
	protected String lineId;

	@Setter
	protected List<TransportModePair> transportModes;
}
