package mobi.chouette.importer.updater;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class UpdaterFactory {
	
	protected static Map<String, UpdaterFactory> factories = new HashMap<String, UpdaterFactory>();

	protected abstract <T> Updater<T> create();

	public static final <T> Updater<T> create(String name)
			throws ClassNotFoundException, IOException {
		if (!factories.containsKey(name)) {
			Class.forName(name);
			if (!factories.containsKey(name))
				throw new ClassNotFoundException(name);
		}
		return (Updater<T>) ((UpdaterFactory) factories.get(name)).create();
	}

	public static void register(String clazz, UpdaterFactory factory) {
		factories.put(clazz, factory);
	}
}
