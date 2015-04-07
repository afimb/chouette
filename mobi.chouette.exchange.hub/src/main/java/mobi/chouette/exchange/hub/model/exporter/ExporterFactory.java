package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ExporterFactory {

	public static Map<String,ExporterFactory> factories = new HashMap<>();

	protected abstract Exporter<?> create(String path) throws IOException;

	public static final Exporter<?> build(String path, String clazz)
			throws ClassNotFoundException, IOException {
		if (!factories.containsKey(clazz)) {
			Class.forName(clazz);
			if (!factories.containsKey(clazz))
				throw new ClassNotFoundException(clazz);
		}
		return ((ExporterFactory) factories.get(clazz)).create(path);
	}

}
