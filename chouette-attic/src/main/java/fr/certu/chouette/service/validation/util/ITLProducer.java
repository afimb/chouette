package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.ITL;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ITLProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.JourneyPatternProducer.class);
	private              ValidationException validationException;
	private              ITL                 iTL                 = null;
	
	ITLProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setITL(ITL iTL) {
		this.iTL = iTL;
	}
	
	ITL getITL() {
		return iTL;
	}
	
	ITL getASG(chouette.schema.ITL castorITL) {
		iTL = new ITL();
		String[] params = null;
		
		// Name obligatoire 
		String castorName = castorITL.getName();
		if ((castorName == null) || (castorName.trim().length() == 0)) {
			params = new String[]{castorITL.getAreaId()};
			LoggingManager.log(logger, "Le \"Name\" du \"ITL\" () est indispendable.", params, Level.ERROR);
			validationException.add(TypeInvalidite.NONAME_ITL, "Le \"Name\" du \"ITL\" () est indispendable.", params);
		}
		else
			iTL.setName(castorName);
		
		// AreaId obligatoire
		String castorAreaId = castorITL.getAreaId();
		if (castorAreaId == null) {
			params = new String[]{iTL.getName()};
			LoggingManager.log(logger, "Le \"AreaId\" du \"ITL\" () est indispendable.", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOAREAID_ITL, "Le \"AreaId\" du \"ITL\" () est indispendable.", params);
		}
		else {
			if (!MainSchemaProducer.isTridentLike(castorAreaId)) {
				params = LoggingManager.getParams(castorAreaId);
				LoggingManager.log(logger, "Le \"areaId\" () pour ce \"ITL\" est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDAREAID_ITL, "Le \"areaId\" () pour ce \"ITL\" est invalide.", params);
			}
			iTL.setAreaId(castorAreaId);
		}
		
		// LineIdShortCut optionel
		String castorLineIdShortcut = castorITL.getLineIdShortCut();
		params = new String[]{iTL.getName()};
		if (castorLineIdShortcut == null)
			LoggingManager.log(logger, "Le \"lineIdShortcut\" de cet  \"ITL\" () est null.", params, Level.INFO);
		else {
			if (!MainSchemaProducer.isTridentLike(castorLineIdShortcut)) {
				params = LoggingManager.getParams(castorLineIdShortcut);
				LoggingManager.log(logger, "Le \"lineIdShortcut\" () pour ce \"ITL\" est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDLINEIDSHORTCUT_ITL, "Le \"lineIdShortcut\" () pour ce \"ITL\" est invalide.", params);
			}
			iTL.setLineIdShortcut(castorLineIdShortcut);
		}
		
		return iTL;
	}
}
