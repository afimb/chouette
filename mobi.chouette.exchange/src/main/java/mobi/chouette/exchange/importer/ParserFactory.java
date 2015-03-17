package mobi.chouette.exchange.importer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ParserFactory {

	protected static Map<String, ParserFactory> factories = new HashMap<String, ParserFactory>();

	protected abstract Parser create();

	public static final Parser create(String name)
			throws ClassNotFoundException, IOException {
		if (!factories.containsKey(name)) {
			// log.info("[DSU] load : " + name);
			Class.forName(name);
			if (!factories.containsKey(name))
				throw new ClassNotFoundException(name);
		}
		return ((ParserFactory) factories.get(name)).create();
	}

	public static void register(String clazz, ParserFactory factory) {
		factories.put(clazz, factory);
	}
}
