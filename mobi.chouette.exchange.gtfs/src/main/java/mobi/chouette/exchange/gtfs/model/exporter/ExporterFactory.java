package mobi.chouette.exchange.gtfs.model.exporter;

import mobi.chouette.exchange.gtfs.exporter.GtfsExportParameters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ExporterFactory {

	public static Map<String,ExporterFactory> factories = new HashMap<>();

	protected abstract Exporter<?> create(String path, GtfsExportParameters parameters) throws IOException;

	public static final Exporter<?> build(String path, String clazz, GtfsExportParameters parameters)
			throws ClassNotFoundException, IOException {
		if (!factories.containsKey(clazz)) {
			Class.forName(clazz);
			if (!factories.containsKey(clazz))
				throw new ClassNotFoundException(clazz);
		}
		return ((ExporterFactory) factories.get(clazz)).create(path, parameters);
	}

}
