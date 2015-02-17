package mobi.chouette.exchange.validation;

import java.util.HashMap;
import java.util.Map;

import mobi.chouette.common.Context;

public abstract class ValidatorFactory {

	public static Map<String, ValidatorFactory> factories = new HashMap<String, ValidatorFactory>();

	protected abstract Validator<?> create(Context context);

	public static final Validator<?> create(String name,Context context)
			throws ClassNotFoundException

	{
		if (!factories.containsKey(name)) {
			Class.forName(name);
			if (!factories.containsKey(name))
				throw new ClassNotFoundException(name);
		}
		return ((ValidatorFactory) factories.get(name)).create(context);
	}
}
