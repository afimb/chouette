package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import java.util.HashMap;
import java.util.Map;

import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

public abstract class IndexFactory {

	public static Map<String, IndexFactory> factories = new HashMap<String, IndexFactory>();

	@SuppressWarnings("rawtypes")
	protected abstract Index create(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception;

	@SuppressWarnings("rawtypes")
	public static final Index build(RegtoppValidationReporter validationReporter, FileContentParser parser, String clazz)
			throws ClassNotFoundException, Exception

	{
		if (!factories.containsKey(clazz)) {
			Class.forName(clazz);
			if (!factories.containsKey(clazz))
				throw new ClassNotFoundException(clazz);
		}
		return ((IndexFactory) factories.get(clazz)).create(validationReporter, parser);
	}
}
