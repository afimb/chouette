package mobi.chouette.exchange.regtopp.importer.parser;

import lombok.Setter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.Validator;

public abstract class LineSpecificParser implements Parser, Validator{
	@Setter
	protected String lineId;
}
