package fr.certu.chouette.service.amivif.base;

public class RegistrationConverter {
	
	public chouette.schema.Registration  atc(amivif.schema.Registration amivifRegistration) {
		if (amivifRegistration == null)
			return null ;
		 chouette.schema.Registration  chouetteRegistration = new chouette.schema.Registration();
		 chouetteRegistration.setRegistrationNumber(amivifRegistration.getRegistrationNumber());
		return chouetteRegistration;
	}
	
	public amivif.schema.Registration  cta(chouette.schema.Registration chouetteRegistration) {
		if (chouetteRegistration == null)
			return null ;
		 amivif.schema.Registration  amivifRegistration = new amivif.schema.Registration();
		 amivifRegistration.setRegistrationNumber(chouetteRegistration.getRegistrationNumber());
		 //TODO: amivifRegistration.setCompanyId(companyId); setLineId(lineIds[]); setPtNetworkID(ptNetworkID[]);
		return amivifRegistration;
	}
}
