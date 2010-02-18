package fr.certu.chouette.service.validation.amivif.util;

import java.util.HashSet;
import java.util.Set;

import fr.certu.chouette.service.validation.amivif.SubLine;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class SubLineProducer extends TridentObjectProducer {
	
	private RegistrationProducer	registrationProducer	= new RegistrationProducer(getValidationException());
	
    public SubLineProducer(ValidationException validationException) {
		super(validationException);
	}

	public SubLine getASG(amivif.schema.SubLine castorSubLine) {
		if (castorSubLine == null)
			return null;
		TridentObject tridentObject = super.getASG(castorSubLine);
		SubLine subLine = new SubLine();
		subLine.setTridentObject(tridentObject);
		
		// sublineName obligatoire
		if (castorSubLine.getSublineName() == null)
			getValidationException().add(TypeInvalidite.NoSubline_SubLine, "Le \"sublineName\" de la \"SubLine\" ("+castorSubLine.getObjectId()+") est null.");
		else
			subLine.setSubLineName(castorSubLine.getSublineName());
		
		// lineName obligatoire
		if (castorSubLine.getLineName() == null)
			getValidationException().add(TypeInvalidite.NoLineName_SubLine, "Le \"lineName\" de la \"SubLine\" ("+castorSubLine.getObjectId()+") est null.");
		else
			subLine.setLineName(castorSubLine.getLineName());
		
		// registration optionnel
		subLine.setRegistration(registrationProducer.getASG(castorSubLine.getRegistration()));
		
		// routeId 1..w
		Set<String> aSet = new HashSet<String>();
		String[] castorRouteIds = castorSubLine.getRouteId();
		if ((castorRouteIds == null) || (castorRouteIds.length < 1))
			getValidationException().add(TypeInvalidite.NoRoute_SubLine, "La \"SubLine\" ("+castorSubLine.getObjectId()+") ne contient aucun \"routeId\".");
		else
			for (int i = 0; i < castorRouteIds.length; i++)
				if (aSet.add(castorRouteIds[i])) {
					try {
						(new TridentObject()).new TridentId(castorRouteIds[i]);
					}
					catch(NullPointerException e) {
						getValidationException().add(TypeInvalidite.NullTridentObject, "Le \"routeId\" d'une \"SubLine\" ("+castorSubLine.getObjectId().toString()+") est null.");
					}
					catch(IndexOutOfBoundsException e) {
						getValidationException().add(TypeInvalidite.InvalidTridentObject, "Le \"routeId\" ("+castorRouteIds[i]+") d'une \"SubLine\" ("+castorSubLine.getObjectId().toString()+") est invalid.");
					}
					subLine.addRouteId(castorRouteIds[i]);
				}
				else
					getValidationException().add(TypeInvalidite.MultipleTridentObject, "La liste \"routeId\" de la \"SubLine\" ("+castorSubLine.getObjectId().toString()+") contient plusieurs fois le meme identifiant ("+castorRouteIds[i]+").");
		
		// lineId obligatoire
		if (castorSubLine.getLineId() == null)
			getValidationException().add(TypeInvalidite.NoLineId_SubLine, "La \"SubLine\" ("+castorSubLine.getObjectId()+") ne contient pas de \"lineId\".");
		else {
			try {
				(new TridentObject()).new TridentId(castorSubLine.getLineId());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Le \"lineId\" d'une \"SubLine\" ("+castorSubLine.getObjectId().toString()+") est null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "Le \"lineId\" ("+castorSubLine.getLineId()+") d'une \"SubLine\" ("+castorSubLine.getObjectId().toString()+") est invalid.");
			}
			subLine.setLineId(castorSubLine.getLineId());
		}
		
		// comment optionnel
		subLine.setComment(castorSubLine.getComment());
		
		return subLine;
	}
}
