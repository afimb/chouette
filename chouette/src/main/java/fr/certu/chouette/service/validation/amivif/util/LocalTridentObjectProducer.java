package fr.certu.chouette.service.validation.amivif.util;

import fr.certu.chouette.service.validation.amivif.LocationTridentObject;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class LocalTridentObjectProducer extends TridentObjectProducer {

	public LocalTridentObjectProducer(ValidationException validationException) {
		super(validationException);
	}
	
	public LocationTridentObject getASG(amivif.schema.LocationType castorLocationType) {
		if (castorLocationType == null)
			return null;
		TridentObject tridentObject = super.getASG(castorLocationType);
		LocationTridentObject locationTridentObject = new LocationTridentObject();
		locationTridentObject.setTridentObject(tridentObject);
		
		// referencingMethod optionnel
		if (castorLocationType.getReferencingMethod() != null)
			switch (castorLocationType.getReferencingMethod().getType()) {
			case amivif.schema.types.LocationReferencingMethodType.VALUE_1_TYPE:
				locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_1);
				break;
			case amivif.schema.types.LocationReferencingMethodType.VALUE_2_TYPE:
				locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_2);
				break;
			case amivif.schema.types.LocationReferencingMethodType.VALUE_3_TYPE:
				locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_3);
				break;
			case amivif.schema.types.LocationReferencingMethodType.VALUE_4_TYPE:
				locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_4);
				break;
			case amivif.schema.types.LocationReferencingMethodType.VALUE_5_TYPE:
				locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_5);
				break;
			case amivif.schema.types.LocationReferencingMethodType.VALUE_6_TYPE:
				locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_6);
				break;
			case amivif.schema.types.LocationReferencingMethodType.VALUE_7_TYPE:
				locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_7);
				break;
			case amivif.schema.types.LocationReferencingMethodType.VALUE_8_TYPE:
				locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_8);
				break;
			case amivif.schema.types.LocationReferencingMethodType.VALUE_9_TYPE:
				locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_9);
				break;
			case amivif.schema.types.LocationReferencingMethodType.VALUE_10_TYPE:
				locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_10);
				break;
			case amivif.schema.types.LocationReferencingMethodType.VALUE_11_TYPE:
				locationTridentObject.setReferencingMethod(LocationTridentObject.ReferencingMethod.VALUE_11);
				break;
			default:
				getValidationException().add(TypeInvalidite.InvalidReferencingMethod_StopPoint, "La \"ReferencingMethod\" du \"StopPoint\" ("+castorLocationType.getObjectId()+") est inconnue."); 
			}
		
		return locationTridentObject;
	}
}
