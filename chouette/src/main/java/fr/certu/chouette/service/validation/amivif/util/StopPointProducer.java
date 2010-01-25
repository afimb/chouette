package fr.certu.chouette.service.validation.amivif.util;

import fr.certu.chouette.service.validation.amivif.Point;
import fr.certu.chouette.service.validation.amivif.StopPoint;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class StopPointProducer extends PointProducer {
        
    public StopPointProducer(ValidationException validationException) {
		super(validationException);
	}

	public StopPoint getASG(amivif.schema.StopPoint castorStopPoint) {
		if (castorStopPoint == null)
			return null;
		Point point = super.getASG(castorStopPoint);
		StopPoint stopPoint = new StopPoint();
		stopPoint.setPoint(point);
		
		// name obligatoire
		if (castorStopPoint.getName() == null)
			getValidationException().add(TypeInvalidite.NoName_StopPoint, "Le \"name\" du \"StopPoint\" est obligatoire.");
		else
			stopPoint.setName(castorStopPoint.getName());
		
		// lineIdShortcut optionnel
		stopPoint.setLineIdShortcut(castorStopPoint.getLineIdShortcut());
		if (stopPoint.getLineIdShortcut() != null) {
			try {
				(new TridentObject()).new TridentId(stopPoint.getLineIdShortcut());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObjectLineIdShortcut_StopPoint, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObjectLineIdShortcut_StopPoint, "L'\"objectId\" "+stopPoint.getLineIdShortcut()+" est invalid.");
			}
		}
		
		// ptNetworkIdShortcut optionel
		stopPoint.setPTNetworkIdShortcut(castorStopPoint.getPtNetworkIdShortcut());
		if (stopPoint.getPTNetworkIdShortcut() != null) {
			try {
				(new TridentObject()).new TridentId(stopPoint.getPTNetworkIdShortcut());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObjectPTNetworkIdShortcut_StopPoint, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObjectPTNetworkIdShortcut_StopPoint, "L'\"objectId\" "+stopPoint.getPTNetworkIdShortcut()+" est invalid.");
			}
		}
		
		// comment optionnel
		stopPoint.setComment(castorStopPoint.getComment());
		
		// AMIVIF_StopPointExtension
		if (castorStopPoint.getAMIVIF_StopPoint_Extension() != null) {
			
			// codeUIC optionnel
			stopPoint.setCodeUIC(castorStopPoint.getAMIVIF_StopPoint_Extension().getCodeUIC());
			
			// upFareZone optionnel
			if (castorStopPoint.getAMIVIF_StopPoint_Extension().hasUpFarZone())
				stopPoint.setUpFareZone((int)castorStopPoint.getAMIVIF_StopPoint_Extension().getUpFarZone());
			else
				stopPoint.setUpFareZone(-1);
			
			// downFareZone optionnel
			if (castorStopPoint.getAMIVIF_StopPoint_Extension().hasDownFarZone())
				stopPoint.setDownFareZone((int)castorStopPoint.getAMIVIF_StopPoint_Extension().getDownFarZone());
			else
				stopPoint.setDownFareZone(-1);
			
			if ((stopPoint.getUpFareZone() >= 0) && (stopPoint.getDownFareZone() >= 0) && (stopPoint.getUpFareZone() < stopPoint.getDownFareZone()))
				getValidationException().add(TypeInvalidite.InvalidUpDownFareZone_StopPoint, "Le \"upFarZone\" ("+stopPoint.getUpFareZone()+") est inferieur au \"downFarZone\" ("+stopPoint.getDownFareZone()+") dans ce \"StopPoint\" ("+stopPoint.getObjectId().toString()+").");
				
		}
		
		return stopPoint;
	}
}
