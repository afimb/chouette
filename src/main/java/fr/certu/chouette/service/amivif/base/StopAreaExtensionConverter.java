package fr.certu.chouette.service.amivif.base;

public class StopAreaExtensionConverter {
	
	public chouette.schema.StopAreaExtension atc(amivif.schema.AMIVIF_StopPoint_Extension amivifStopPointExtension) {
		if (amivifStopPointExtension == null)
			return null;
		chouette.schema.StopAreaExtension chouetteStopExtension = new chouette.schema.StopAreaExtension();
		chouetteStopExtension.setAreaType(chouette.schema.types.ChouetteAreaType.QUAY);
		chouetteStopExtension.setFareCode((int)amivifStopPointExtension.getUpFarZone());
		if ( amivifStopPointExtension.getCodeUIC()!=null && !amivifStopPointExtension.getCodeUIC().isEmpty())	{
			chouette.schema.Registration registration = new chouette.schema.Registration();
			registration.setRegistrationNumber(amivifStopPointExtension.getCodeUIC());
			chouetteStopExtension.setRegistration(registration);
		}
		return chouetteStopExtension;
	}
	
	public amivif.schema.AMIVIF_StopPoint_Extension cta(chouette.schema.StopAreaExtension chouetteStopExtension) {
		if (chouetteStopExtension == null)
			return null;
		amivif.schema.AMIVIF_StopPoint_Extension amivifStopPointExtension = new amivif.schema.AMIVIF_StopPoint_Extension();
		if (chouetteStopExtension.getRegistration() != null)
			amivifStopPointExtension.setCodeUIC(chouetteStopExtension.getRegistration().getRegistrationNumber());
		int fareCode = chouetteStopExtension.getFareCode();
		amivifStopPointExtension.setDownFarZone(0);
		amivifStopPointExtension.setUpFarZone(fareCode);
		return amivifStopPointExtension;
	}
}
