package mobi.chouette.exchange.netexprofile.importer.validation;

import mobi.chouette.common.Context;

import java.util.HashMap;
import java.util.Map;

public abstract class NetexProfileValidatorFactory {

    public static Map<String, NetexProfileValidatorFactory> factories = new HashMap<String, NetexProfileValidatorFactory>();

    protected abstract NetexProfileValidator create(Context context);

    public static final NetexProfileValidator create(String name, Context context) throws ClassNotFoundException {
        if (!factories.containsKey(name)) {
            Class.forName(name);
            if (!factories.containsKey(name)) {
                throw new ClassNotFoundException(name);
            }
        }
        return ((NetexProfileValidatorFactory) factories.get(name)).create(context);
    }
}
