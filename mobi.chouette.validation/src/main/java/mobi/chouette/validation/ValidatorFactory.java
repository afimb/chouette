package mobi.chouette.validation;

import java.util.HashMap;
import java.util.Map;

public abstract class ValidatorFactory {

	public static Map<String, ValidatorFactory> factories = new HashMap<String, ValidatorFactory>();

	protected abstract Validator<?> create();

	public static final Validator<?> create(String name)
			throws ClassNotFoundException

	{
		if (!factories.containsKey(name)) {
			Class.forName(name);
			if (!factories.containsKey(name))
				throw new ClassNotFoundException(name);
		}
		return ((ValidatorFactory) factories.get(name)).create();
	}
}
