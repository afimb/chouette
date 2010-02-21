package fr.certu.chouette.service.validation.amivif.util;

import java.util.HashSet;
import java.util.Set;

import fr.certu.chouette.service.validation.amivif.JourneyPattern;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class JourneyPatternProducer extends TridentObjectProducer {
    
	private RegistrationProducer	registrationProducer	= new RegistrationProducer(getValidationException());
	
    public JourneyPatternProducer(ValidationException validationException) {
		super(validationException);
	}

	public JourneyPattern getASG(amivif.schema.JourneyPattern castorJourneyPattern) {
		if (castorJourneyPattern == null)
			return null;
		JourneyPattern journeyPattern = (JourneyPattern)getASG(castorJourneyPattern);
		
		// name optionnel
		journeyPattern.setName(castorJourneyPattern.getName());
		
		// publishedName optionnel
		journeyPattern.setPublishedName(castorJourneyPattern.getPublishedName());
		
		// routeId obligatoire
		if (castorJourneyPattern.getRouteId() == null)
			getValidationException().add(TypeInvalidite.NoRoute_JourneyPattern, "La \"routeId\" du \"JourneyPattern\" ("+castorJourneyPattern.getObjectId()+") est null.");
		else {
			try {
				(new TridentObject()).new TridentId(castorJourneyPattern.getRouteId());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObjectLineEnd_Line, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorJourneyPattern.getRouteId()+" est invalid.");
			}
			journeyPattern.setRouteId(castorJourneyPattern.getRouteId());
		}
		
		// origin optionnel
		journeyPattern.setOrigin(castorJourneyPattern.getOrigin());
		if (journeyPattern.getOrigin() != null) {
			try {
				(new TridentObject()).new TridentId(journeyPattern.getOrigin());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObjectLineEnd_Line, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+journeyPattern.getOrigin()+" est invalid.");
			}
		}
		
		// destination optionnel
		journeyPattern.setDestination(castorJourneyPattern.getDestination());
		if (journeyPattern.getDestination() != null) {
			try {
				(new TridentObject()).new TridentId(journeyPattern.getDestination());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObjectLineEnd_Line, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+journeyPattern.getDestination()+" est invalid.");
			}
		}
		
		// stopPointList 2..w
		Set<String> aSet = new HashSet<String>();
		String[] castorStopPoints = castorJourneyPattern.getStopPointList();
		if ((castorStopPoints == null) || (castorStopPoints.length < 2))
			getValidationException().add(TypeInvalidite.InvalidNumberOfStopPoints_JourneyPattern, "La liste \"stopPointList\" du \"JourneyPattern\" ("+castorJourneyPattern.getObjectId()+") n'a pas plus de 2 membres.");
		else
			for (int i = 0; i < castorStopPoints.length; i++) {
				if (aSet.add(castorStopPoints[i])) {
					try {
						(new TridentObject()).new TridentId(castorStopPoints[i]);
					}
					catch(NullPointerException e) {
						getValidationException().add(TypeInvalidite.NullTridentObjectRouteId_Line, "Un \"objectId\" ne peut etre null.");
					}
					catch(IndexOutOfBoundsException e) {
						getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorStopPoints[i]+" est invalid.");
					}
					journeyPattern.addStopPointId(castorStopPoints[i]);
				}
				else
					getValidationException().add(TypeInvalidite.MultipleTridentObject, "La liste \"stopPointId\" de la \"JourneyPattern\" contient plusieur fois le meme identifiant ("+castorStopPoints[i]+").");
			}
		
		// registration optionnel
		journeyPattern.setRegistration(registrationProducer.getASG(castorJourneyPattern.getRegistration()));
		
		// lineIdShortcut optionnel
		journeyPattern.setLineIdShortcut(castorJourneyPattern.getLineIdShortcut());
		if (journeyPattern.getLineIdShortcut() != null) {
			try {
				(new TridentObject()).new TridentId(journeyPattern.getLineIdShortcut());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObjectLineEnd_Line, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+journeyPattern.getLineIdShortcut()+" est invalid.");
			}
		}
		
		// comment optionnel
		journeyPattern.setComment(castorJourneyPattern.getComment());
		
		return journeyPattern;
	}
}
