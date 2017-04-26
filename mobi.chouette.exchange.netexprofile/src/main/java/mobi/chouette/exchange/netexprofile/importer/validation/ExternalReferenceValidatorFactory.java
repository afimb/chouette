package mobi.chouette.exchange.netexprofile.importer.validation;

import mobi.chouette.common.Context;

import java.util.HashMap;
import java.util.Map;

public abstract class ExternalReferenceValidatorFactory {

    public static Map<String, ExternalReferenceValidatorFactory> factories = new HashMap<String, ExternalReferenceValidatorFactory>();

    protected abstract ExternalReferenceValidator create(Context context);

    public static final ExternalReferenceValidator create(String name, Context context) throws ClassNotFoundException {
        if (!factories.containsKey(name)) {
            Class.forName(name);
            if (!factories.containsKey(name)) {
                throw new ClassNotFoundException(name);
            }
        }
        return ((ExternalReferenceValidatorFactory) factories.get(name)).create(context);
    }
}
