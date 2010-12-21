package fr.certu.chouette.service.validation.amivif.util;

import java.util.HashSet;
import java.util.Set;

import fr.certu.chouette.service.validation.amivif.LocationTridentObject;
import fr.certu.chouette.service.validation.amivif.StopArea;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class StopAreaProducer extends LocalTridentObjectProducer {
    
    private ProjectedPointProducer	projectedPointProducer	= new ProjectedPointProducer(getValidationException());
    
    public StopAreaProducer(ValidationException validationException) {
    	super(validationException);
	}

	public StopArea getASG(amivif.schema.StopArea castorStopArea) {
		if (castorStopArea == null)
			return null;
		
		// TridentObject obligatoire
		LocationTridentObject locationTridentObject = super.getASG(castorStopArea);
		StopArea stopArea = new StopArea();
		stopArea.setLocationTridentObject(locationTridentObject);
		
		// name optionnel
		stopArea.setName(castorStopArea.getName());
		
		// contains 1..w
		Set<String> aSet = new HashSet<String>();
		String[] contains = castorStopArea.getContains();
		if ((contains == null) || (contains.length < 1))
			getValidationException().add(TypeInvalidite.NoContains_StopArea, "La liste \"contains\" du \"StopArea\" ("+castorStopArea.getObjectId()+") est vide.");
		else
			for (int i = 0; i < contains.length; i++)
				if (aSet.add(contains[i])) {
					try {
						(new TridentObject()).new TridentId(contains[i]);
					}
					catch(NullPointerException e) {
						getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
					}
					catch(IndexOutOfBoundsException e) {
						getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+contains[i]+" est invalid.");
					}
					stopArea.addContain(contains[i]);
				}
				else
					getValidationException().add(TypeInvalidite.MultipleTridentObject, "La liste \"contains\" du \"StopArea\" ("+stopArea.getObjectId().toString()+") contient plusieurs fois le meme identifiant ("+contains[i]+").");
		
		// boundaryPoints 0..w
		aSet = new HashSet<String>();
		String[] castorBoundaryPoints = castorStopArea.getBoundaryPoint();
		if (castorBoundaryPoints != null)
			for (int i = 0; i < castorBoundaryPoints.length; i++)
				if (aSet.add(castorBoundaryPoints[i])) {
					try {
						(new TridentObject()).new TridentId(castorBoundaryPoints[i]);
					}
					catch(NullPointerException e) {
						getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
					}
					catch(IndexOutOfBoundsException e) {
						getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorBoundaryPoints[i]+" est invalid.");
					}
					stopArea.addBoundaryPointId(castorBoundaryPoints[i]);
				}
				else
					getValidationException().add(TypeInvalidite.MultipleTridentObject, "La liste \"boundaryPoint\" du \"StopArea\" ("+stopArea.getObjectId().toString()+") contient plusieurs fois le meme identifiant ("+castorBoundaryPoints[i]+").");
		
		// centroidOfAreas optionnel
		stopArea.setCentroidOfAreaId(castorStopArea.getCentroidOfArea());
		if (stopArea.getCentroidOfAreaId() != null) {
			try {
				(new TridentObject()).new TridentId(stopArea.getCentroidOfAreaId());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+stopArea.getCentroidOfAreaId()+" est invalid.");
			}
		}
		
		// comment optionnel
		stopArea.setComment(castorStopArea.getComment());
		
		// AMIVIF_StopArea_Extension obligatoire
		if (castorStopArea.getAMIVIF_StopArea_Extension() == null)
			getValidationException().add(TypeInvalidite.NoAMIVIFStopAreaExtension_StopArea, "La \"StopArea\" ("+castorStopArea.getObjectId()+") n'a pas de \"AMIVIF_StopArea_Extension\".");
		else {
			// nearestTopicName optionnel
			stopArea.setNearestTopicName(castorStopArea.getAMIVIF_StopArea_Extension().getNearestTopicName());
			
			// upFareZone obligatoire
			if (castorStopArea.getAMIVIF_StopArea_Extension().hasUpFarZone())
				stopArea.setUpFareCode((int)castorStopArea.getAMIVIF_StopArea_Extension().getUpFarZone());
			else
				getValidationException().add(TypeInvalidite.NoUpFareZone_StopArea, "La \"StopArea\" ("+castorStopArea.getObjectId()+") n'a pas de \"upFarZone\".");
			
			// downFarZone optionnel
			if (castorStopArea.getAMIVIF_StopArea_Extension().hasDownFarZone())
				stopArea.setUpFareCode((int)castorStopArea.getAMIVIF_StopArea_Extension().getDownFarZone());
			else
				stopArea.setUpFareCode(-1);
			
			// projectedPoint optionnel
			if (castorStopArea.getAMIVIF_StopArea_Extension().getProjectedPoint() != null)
				stopArea.setProjectedPoint(projectedPointProducer.getASG(castorStopArea.getAMIVIF_StopArea_Extension().getProjectedPoint()));
		}
		
		return stopArea;
	}
}
