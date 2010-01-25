package fr.certu.chouette.service.validation.amivif.util;

import fr.certu.chouette.service.validation.amivif.AccessPoint;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class AccessPointProducer extends PointProducer {
    
    public AccessPointProducer(ValidationException validationException) {
		super(validationException);
	}
	
	public AccessPoint getASG(amivif.schema.AccessPoint castorAccessPoint) {
		if (castorAccessPoint == null)
			return null;
		AccessPoint accessPoint = (AccessPoint)super.getASG(castorAccessPoint);
		
		// name optionnel
		accessPoint.setName(castorAccessPoint.getName());
		
		// type optionnel
		if (castorAccessPoint.getType() != null)
		{
			switch (castorAccessPoint.getType()) 
			{
				case IN:
					accessPoint.setAccessPointType(AccessPoint.AccessPointType.In);
					break;
				case OUT:
					accessPoint.setAccessPointType(AccessPoint.AccessPointType.Out);
					break;
				case INOUT:
					accessPoint.setAccessPointType(AccessPoint.AccessPointType.InOut);
					break;
				default:
					getValidationException().add(TypeInvalidite.InvalidAccessPointType_AccessPoint, "Le \"type\" de l'\"AccessPoint\" ("+castorAccessPoint.getObjectId()+") est invalid.");
			}
		}
		// openningTime optionnel
		if (castorAccessPoint.getOpeningTime() == null)
			getValidationException().add(TypeInvalidite.NoOpenningTime_AccessPoint, "L'\"openningTime\" de l'\"AccessPoint\" ("+castorAccessPoint.getObjectId()+") est null.");
		else
			accessPoint.setOpenningTime(castorAccessPoint.getOpeningTime().toDate());
		
		// closingTime optionnel
		if (castorAccessPoint.getClosingTime() == null)
			getValidationException().add(TypeInvalidite.NoClosingPoint_AccessPoint, "Le \"closingTime\" de l'\"AccessPoint\" ("+castorAccessPoint.getObjectId()+") est null.");
		else
			accessPoint.setClosingTime(castorAccessPoint.getClosingTime().toDate());
		
		if ((accessPoint.getOpenningTime() != null) && (accessPoint.getClosingTime() !=null) && (accessPoint.getOpenningTime().after(accessPoint.getClosingTime())))
			getValidationException().add(TypeInvalidite.InvalidOpenningClosingTimes_AccessPoint, "L'\"AccessPoint\" ("+accessPoint.getObjectId().toString()+") dispose d'un \"openningTime\" ("+accessPoint.getOpenningTime()+") posterieur au \"closingTime\" ("+accessPoint.getClosingTime()+").");
		
		// mobilityRestrictedSuitability optionnel
		accessPoint.setMobilityRestrictedSuitability(castorAccessPoint.getMobilityRestrictedSuitability());
		
		// stairsAvailability optionnel
		accessPoint.setStairsAvailability(castorAccessPoint.getStairsAvailability());
		
		// liftAvailability optionnel
		accessPoint.setLiftAvailability(castorAccessPoint.getLiftAvailability());
		
		// comment optionnel
		accessPoint.setComment(castorAccessPoint.getComment());
		
		// AMIVIF_AccessPoint_extension obligatoire
		if (castorAccessPoint.getAMIVIF_AccessPoint_Extension() == null)
			getValidationException().add(TypeInvalidite.NoAMIVIFAccessPointExtension_AccessPoint, "L'\"AMIVIF_AccessPoint_Extension\" de l'\"AccessPoint\" ("+castorAccessPoint.getObjectId()+") est null.");
		else
			if (castorAccessPoint.getAMIVIF_AccessPoint_Extension().getAccessType() == null)
				getValidationException().add(TypeInvalidite.NoAccessType_AMIVIFAccessPointExtension, "L'\"AMIVIF_AccessPoint_Extension\" de l'\"AccessPoint\" ("+castorAccessPoint.getObjectId()+") ne possede pas d'\"accessType\".");
			else
				accessPoint.setAccessType(castorAccessPoint.getAMIVIF_AccessPoint_Extension().getAccessType());
		
		return accessPoint;
	}
}
