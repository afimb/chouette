package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class IndexFactory {

	public static Map<String, IndexFactory> factories = new HashMap<String, IndexFactory>();

	@SuppressWarnings("rawtypes")
	protected abstract Index create(String path) throws IOException;

	@SuppressWarnings("rawtypes")
	public static final Index build(String path, String clazz)
			throws ClassNotFoundException, IOException

	{
		if (!factories.containsKey(clazz)) {
			Class.forName(clazz);
			if (!factories.containsKey(clazz))
				throw new ClassNotFoundException(clazz);
		}
		return ((IndexFactory) factories.get(clazz)).create(path);
	}
}
