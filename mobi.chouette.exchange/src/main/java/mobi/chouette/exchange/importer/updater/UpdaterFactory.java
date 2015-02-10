package mobi.chouette.exchange.importer.updater;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;

public abstract class UpdaterFactory {

	protected static Map<String, UpdaterFactory> factories = new HashMap<String, UpdaterFactory>();

	protected abstract <T> Updater<T> create(InitialContext context);

	public static final <T> Updater<T> create(InitialContext context, String name)
			throws ClassNotFoundException, IOException {
		if (!factories.containsKey(name)) {
			Class.forName(name);
			if (!factories.containsKey(name))
				throw new ClassNotFoundException(name);
		}
		return (Updater<T>) ((UpdaterFactory) factories.get(name)).create(context);
	}

	public static void register(String clazz, UpdaterFactory factory) {
		factories.put(clazz, factory);
	}
}
