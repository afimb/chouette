package mobi.chouette.exchange.netexprofile.importer.util;

import org.rutebanken.netex.model.Common_VersionFrameStructure;

import java.util.HashMap;
import java.util.Map;

public class NetexFrameContext {

    private final Map<Class<? extends Common_VersionFrameStructure>, Object> dataObjectFrames = new HashMap<>();

    public <T> void put(Class<? extends Common_VersionFrameStructure> key, T value) {
        dataObjectFrames.put(key, key.cast(value));
    }

    // TODO fix key to conform to extended type (Common_VersionFrameStructure)
    public <T> T get(Class<T> key) {
        return key.cast(dataObjectFrames.get(key));
    }

    public void clear() {
        dataObjectFrames.clear();
    }

}
