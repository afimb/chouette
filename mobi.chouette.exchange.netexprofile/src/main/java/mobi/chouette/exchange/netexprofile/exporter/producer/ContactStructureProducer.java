package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.ContactStructure;

import org.rutebanken.netex.model.MultilingualString;

/**
 * Transform local ContactStructure to NeTEx ContactStructure.
 */
public class ContactStructureProducer {

	public org.rutebanken.netex.model.ContactStructure produce(ContactStructure chouetteObject) {
		if (chouetteObject == null) {
			return null;
		}
		org.rutebanken.netex.model.ContactStructure netexObject = new org.rutebanken.netex.model.ContactStructure();
		if (chouetteObject.getContactPerson() != null) {
			netexObject.setContactPerson(new MultilingualString().withValue(chouetteObject.getContactPerson()));
		}
		netexObject.setUrl(chouetteObject.getUrl());
		netexObject.setPhone(chouetteObject.getPhone());
		netexObject.setFax(chouetteObject.getFax());
		netexObject.setEmail(chouetteObject.getEmail());
		if (chouetteObject.getFurtherDetails() != null) {
			netexObject.setFurtherDetails(new MultilingualString().withValue(chouetteObject.getFurtherDetails()));
		}

		return netexObject;
	}
}
