package mobi.chouette.exchange.regtopp.importer.parser;

import lombok.Setter;
import mobi.chouette.exchange.importer.Parser;

public abstract class LineSpecificParser implements Parser{
	@Setter
	protected String lineId;
}
