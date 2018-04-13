package mobi.chouette.exchange.netexprofile.exporter.producer;

import java.util.List;
import java.util.stream.Collectors;

import mobi.chouette.model.KeyValue;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;

/**
 * Transform local KeyValue lists to NeTEx KeyListStructure.
 */
public class KeyListStructureProducer {

	public KeyListStructure produce(List<KeyValue> chouetteKeyValues) {
		if (CollectionUtils.isEmpty(chouetteKeyValues)) {
			return null;
		}

		List<KeyValueStructure> netexKeyValues = chouetteKeyValues.stream().map(kv -> produce(kv)).collect(Collectors.toList());
		return new KeyListStructure().withKeyValue(netexKeyValues);
	}

	private KeyValueStructure produce(KeyValue chouetteKeyValue) {
		KeyValueStructure netexKeyValue = new KeyValueStructure();
		netexKeyValue.setKey(chouetteKeyValue.getKey());
		netexKeyValue.setTypeOfKey(chouetteKeyValue.getTypeOfKey());
		netexKeyValue.setValue(chouetteKeyValue.getValue());
		return netexKeyValue;
	}

}
