package fr.certu.chouette.service.validation.amivif.util;

import fr.certu.chouette.service.validation.amivif.LocationTridentObject;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class LocalTridentObjectProducer extends TridentObjectProducer {

	public LocalTridentObjectProducer(ValidationException validationException) {
		super(validationException);
	}
	
	public LocationTridentObject getASG(amivif.schema.LocationTypeType castorLocationType) {
		if (castorLocationType == null)
			return null;
		TridentObject tridentObject = super.getASG(castorLocationType);
		LocationTridentObject locationTridentObject = new LocationTridentObject();
		locationTridentObject.setTridentObject(tridentObject);
		
		// referencingMethod optionnel
		if (castorLocationType.getReferencingMethod() != null)
			switch (castorLocationType.getReferencingMethod()) 
			{
				case VALUE_1:
					locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_1);
					break;
				case VALUE_2:
					locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_2);
					break;
				case VALUE_3:
					locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_3);
					break;
				case VALUE_4:
					locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_4);
					break;
				case VALUE_5:
					locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_5);
					break;
				case VALUE_6:
					locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_6);
					break;
				case VALUE_7:
					locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_7);
					break;
				case VALUE_8:
					locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_8);
					break;
				case VALUE_9:
					locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_9);
					break;
				case VALUE_10:
					locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_10);
					break;
				case VALUE_11:
					locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_11);
					break;
				default:
					getValidationException().add(TypeInvalidite.InvalidReferencingMethod_StopPoint, "La \"ReferencingMethod\" du \"StopPoint\" ("+castorLocationType.getObjectId()+") est inconnue."); 
			}
		
		return locationTridentObject;
	}
}
