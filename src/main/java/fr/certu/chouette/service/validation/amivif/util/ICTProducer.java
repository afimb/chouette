package fr.certu.chouette.service.validation.amivif.util;

import fr.certu.chouette.service.validation.amivif.ICT;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class ICTProducer extends TridentObjectProducer {
    
    public ICTProducer(ValidationException validationException) {
		super(validationException);
	}

	public ICT getASG(amivif.schema.ICT castorICT) {
		if (castorICT == null)
			return null;
		ICT ict = (ICT)super.getASG(castorICT);
		
		// typeICT obligatoire
		if (castorICT.getTypeICT() == null)
			getValidationException().add(TypeInvalidite.NoTypeICT_ICT, "Le \"TypeICT\" du \"ICT\" ("+castorICT.getObjectId()+") est null.");
		else
			switch (castorICT.getTypeICT()) {
			case ITL:
				ict.setICTType(ICT.ICTType.ITL);
				break;
			case SECTION:
				ict.setICTType(ICT.ICTType.Section);
				break;
			default:
				getValidationException().add(TypeInvalidite.InvalidICTType_ICT, "Le \"TypeICT\" ("+castorICT.getTypeICT().toString()+") du \"ICT\" ("+castorICT.getObjectId()+") est invalid.");
			}
		
		// section optionnel
		if (castorICT.hasSection())
			ict.setSection((int)castorICT.getSection());
		else {
			ict.setSection(-1);
			if (ict.getICTType().equals(ICT.ICTType.Section))
				getValidationException().add(TypeInvalidite.NoSection_ICT, "La \"section\" du \"ICT\" de type \"SECTION\" ("+castorICT.getObjectId()+") est null.");
		}
		
		// routeId obligatoire
		if (castorICT.getRouteId() == null)
			getValidationException().add(TypeInvalidite.NoRoute_ICT, "Le \"routeId\" du \"ICT\" ("+castorICT.getObjectId()+") est null.");
		else {
			ict.setRouteId(castorICT.getRouteId());
			try {
				(new TridentObject()).new TridentId(castorICT.getRouteId());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Le \"routeId\" d'une \"ICT\" ("+castorICT.getObjectId().toString()+") est null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "Le \"routeId\" ("+castorICT.getRouteId()+") d'une \"ICT\" ("+castorICT.getObjectId().toString()+") est invalid.");
			}
		}
		
		// stopPointId 2
		String[] castorStopPointIds = castorICT.getStopPointId();
		if ((castorStopPointIds == null) || (castorStopPointIds.length != 2))
			getValidationException().add(TypeInvalidite.InvalidNumberOfStopPoints_ICT, "Le nombre de \"stopPointd\" dans l'\"ICT\" ("+castorICT.getObjectId()+") est different de 2.");
		else {
			if (castorStopPointIds[1].equals(castorStopPointIds[0]))
				getValidationException().add(TypeInvalidite.InvalidStopPoints_ICT, "Les deux \"stopPointId\" de l'\"ICT\" ("+castorICT.getObjectId()+") sont egaux ("+castorStopPointIds[0]+").");
			try {
				(new TridentObject()).new TridentId(castorStopPointIds[0]);
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"stopPointId\" d'une \"ICT\" ("+castorICT.getObjectId().toString()+") est null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "Un \"stopPointId\" ("+castorStopPointIds[0]+") d'une \"ICT\" ("+castorICT.getObjectId().toString()+") est invalid.");
			}
			try {
				(new TridentObject()).new TridentId(castorStopPointIds[1]);
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"stopPointId\" d'une \"ICT\" ("+castorICT.getObjectId().toString()+") est null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "Un \"stopPointId\" ("+castorStopPointIds[1]+") d'une \"ICT\" ("+castorICT.getObjectId().toString()+") est invalid.");
			}
			ict.setStopPointId(castorStopPointIds);
		}
		
		// vehicleJourneyId optionnel
		ict.setVehicleJourneyId(castorICT.getVehicleJourneyId());
		if (ict.getVehicleJourneyId() != null) {
			try {
				(new TridentObject()).new TridentId(ict.getVehicleJourneyId());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Le \"routeId\" d'une \"ICT\" ("+castorICT.getObjectId().toString()+") est null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "Le \"routeId\" ("+ict.getVehicleJourneyId()+") d'une \"ICT\" ("+castorICT.getObjectId().toString()+") est invalid.");
			}
		}
		
		return ict;
	}
}
