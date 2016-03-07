package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;

public abstract class IndexFactory {

	public static Map<String, IndexFactory> factories = new HashMap<String, IndexFactory>();

	@SuppressWarnings("rawtypes")
	protected abstract Index create(FileContentParser parser) throws IOException;

	@SuppressWarnings("rawtypes")
	public static final Index build(FileContentParser parser, String clazz)
			throws ClassNotFoundException, IOException

	{
		if (!factories.containsKey(clazz)) {
			Class.forName(clazz);
			if (!factories.containsKey(clazz))
				throw new ClassNotFoundException(clazz);
		}
		return ((IndexFactory) factories.get(clazz)).create(parser);
	}
}
