package fr.certu.chouette.service.validation.amivif.util;

import fr.certu.chouette.service.validation.amivif.Point;
import fr.certu.chouette.service.validation.amivif.StopPointInConnection;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class StopPointInConnectionProducer extends StopPointProducer {
    
    public StopPointInConnectionProducer(ValidationException validationException) {
		super(validationException);
	}

	public StopPointInConnection getASG(amivif.schema.StopPointInConnection castorStopPointInConnection) {
		if (castorStopPointInConnection == null)
			return null;
		Point point = super.getASG(castorStopPointInConnection);
		StopPointInConnection stopPointInConnection = new StopPointInConnection();
		stopPointInConnection.setPoint(point);
		
		// name obligatoire
		if (castorStopPointInConnection.getName() == null)
			getValidationException().add(TypeInvalidite.NoName_StopPointInConnection, "Le \"name\" du \"StopPointInConnection\" est obligatoire.");
		else
			stopPointInConnection.setName(castorStopPointInConnection.getName());
		
		// lineIdShortcut optionnel
		stopPointInConnection.setLineIdShortcut(castorStopPointInConnection.getLineIdShortcut());
		if (stopPointInConnection.getLineIdShortcut() != null) {
			try {
				(new TridentObject()).new TridentId(stopPointInConnection.getLineIdShortcut());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObjectLineIdShortcut_StopPointInConnection, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObjectLineIdShortcut_StopPointInConnection, "L'\"objectId\" "+stopPointInConnection.getLineIdShortcut()+" est invalid.");
			}
		}
		
		// ptNetworkIdShortcut optionel
		stopPointInConnection.setPTNetworkIdShortcut(castorStopPointInConnection.getPtNetworkIdShortcut());
		if (stopPointInConnection.getPTNetworkIdShortcut() != null) {
			try {
				(new TridentObject()).new TridentId(stopPointInConnection.getPTNetworkIdShortcut());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObjectPTNetworkIdShortcut_StopPointInConnection, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObjectPTNetworkIdShortcut_StopPointInConnection, "L'\"objectId\" "+stopPointInConnection.getPTNetworkIdShortcut()+" est invalid.");
			}
		}
		
		// comment optionnel
		stopPointInConnection.setComment(castorStopPointInConnection.getComment());
		
		// AMIVIF_StopPointExtension
		if (castorStopPointInConnection.getAMIVIF_StopPoint_Extension() != null) {
			
			// codeUIC optionnel
			stopPointInConnection.setCodeUIC(castorStopPointInConnection.getAMIVIF_StopPoint_Extension().getCodeUIC());
			
			// upFareZone optionnel
			if (castorStopPointInConnection.getAMIVIF_StopPoint_Extension().hasUpFarZone())
				stopPointInConnection.setUpFareZone((int)castorStopPointInConnection.getAMIVIF_StopPoint_Extension().getUpFarZone());
			else
				stopPointInConnection.setUpFareZone(-1);
			
			// downFareZone optionnel
			if (castorStopPointInConnection.getAMIVIF_StopPoint_Extension().hasDownFarZone())
				stopPointInConnection.setDownFareZone((int)castorStopPointInConnection.getAMIVIF_StopPoint_Extension().getDownFarZone());
			else
				stopPointInConnection.setDownFareZone(-1);
			
			if ((stopPointInConnection.getUpFareZone() >= 0) && (stopPointInConnection.getDownFareZone() >= 0) && (stopPointInConnection.getUpFareZone() < stopPointInConnection.getDownFareZone()))
				getValidationException().add(TypeInvalidite.InvalidUpDownFareZone_StopPointInConnection, "Le \"upFarZone\" ("+stopPointInConnection.getUpFareZone()+") est inferieur au \"downFarZone\" ("+stopPointInConnection.getDownFareZone()+") dans ce \"StopPointInConnection\" ("+stopPointInConnection.getObjectId().toString()+").");
				
		}
		
		return stopPointInConnection;
	}
}
