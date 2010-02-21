package fr.certu.chouette.service.validation.amivif.util;

import fr.certu.chouette.service.validation.amivif.GroupOfLine;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class GroupOfLineProducer extends TridentObjectProducer {
    
    public GroupOfLineProducer(ValidationException validationException) {
		super(validationException);
	}

	public GroupOfLine getASG(amivif.schema.GroupOfLine castorGroupOfLine) {
		if (castorGroupOfLine == null)
			return null;
		
		// TridentObject obligatoire
		TridentObject tridentObject = super.getASG(castorGroupOfLine);
		GroupOfLine groupOfLine = new GroupOfLine();
		groupOfLine.setTridentObject(tridentObject);
		
		// name obligatoire
		String castorName = castorGroupOfLine.getName();
		if ((castorName == null) || (castorName.length() == 0))
			getValidationException().add(TypeInvalidite.NoName_GroupOfLine, "Une \"GroupOfLine\" doit toujours avoir un \"name\".");
		else
			groupOfLine.setName(castorName);
		
		// lineId 1..w
		String[] castorLineIds = castorGroupOfLine.getLineId();
		if ((castorLineIds == null) || (castorLineIds.length < 1))
			getValidationException().add(TypeInvalidite.NoLineIds_GroupOfLine, "La liste \"lineId\" du \"GroupOfLine\" ("+castorGroupOfLine.getObjectId()+") est vide.");
		else
			for (int i = 0; i < castorLineIds.length; i++)
				groupOfLine.addLineId(castorLineIds[i]);
		
		// comment optionnel
		groupOfLine.setComment(castorGroupOfLine.getComment());
		
		return groupOfLine;
	}
}
