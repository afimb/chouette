package mobi.chouette.exchange.netexprofile.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mobi.chouette.model.KeyValue;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;

/**
 * Parse NeTEx KeyValueStructures to local KeyValue objects.
 */
public class KeyValueParser {

	public List<KeyValue> parse(KeyListStructure netexListStructure) {
		if (netexListStructure == null || CollectionUtils.isEmpty(netexListStructure.getKeyValue())) {
			return new ArrayList<>();
		}
		return netexListStructure.getKeyValue().stream().map(kv -> parse(kv)).collect(Collectors.toList());
	}

	private KeyValue parse(KeyValueStructure netexKeyValue) {
		KeyValue chouetteKeyValue = new KeyValue();
		chouetteKeyValue.setKey(netexKeyValue.getKey());
		chouetteKeyValue.setTypeOfKey(netexKeyValue.getTypeOfKey());
		chouetteKeyValue.setValue(netexKeyValue.getValue());
		return chouetteKeyValue;
	}
}
