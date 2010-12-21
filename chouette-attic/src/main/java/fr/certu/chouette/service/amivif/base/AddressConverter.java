package fr.certu.chouette.service.amivif.base;

public class AddressConverter {

	public chouette.schema.Address atc(amivif.schema.Address amivifAddress) {
		chouette.schema.Address chouetteAdress = new chouette.schema.Address();
		chouetteAdress.setCountryCode(amivifAddress.getPostalCode());
		chouetteAdress.setStreetName(amivifAddress.getStreetName());
		return chouetteAdress;
	}

	public amivif.schema.Address cta(chouette.schema.Address chouetteAddress) {
		if ( chouetteAddress==null) return null;
		
		amivif.schema.Address amivifAdress = new amivif.schema.Address();
		amivifAdress.setPostalCode(chouetteAddress.getCountryCode());
		amivifAdress.setStreetName(chouetteAddress.getStreetName());
		return amivifAdress;
	}
}
