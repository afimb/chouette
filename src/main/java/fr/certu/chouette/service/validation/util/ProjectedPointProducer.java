package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.ProjectedPoint;
import java.math.BigDecimal;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ProjectedPointProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.AreaCentroidProducer.class);
	private              ValidationException validationException;
	private              ProjectedPoint      projectedPoint      = null;
	
	ProjectedPointProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setProjectedPoint(ProjectedPoint projectedPoint) {
		this.projectedPoint = projectedPoint;
	}
	
	ProjectedPoint getProjectedPoint() {
		return projectedPoint;
	}
	
	ProjectedPoint getASG(chouette.schema.ProjectedPoint catorProjectedPoint) {
		projectedPoint = new ProjectedPoint();
		
		// X obligatoire
		BigDecimal castorX = catorProjectedPoint.getX();
		if (castorX == null) {
			LoggingManager.log(logger, "Pas d'objet de type \"X\" dans ce \"ProjectedPoint\".", Level.ERROR);
			validationException.add(TypeInvalidite.NULLX_PROJECTEDPOINT, "Pas d'objet de type \"X\" dans ce \"ProjectedPoint\".");
		}
		else
			projectedPoint.setX(castorX);
		
		// Y obligatoire
		BigDecimal castorY = catorProjectedPoint.getY();
		if (castorY == null) {
			LoggingManager.log(logger, "Pas d'objet de type \"Y\" dans ce \"ProjectedPoint\".", Level.ERROR);
			validationException.add(TypeInvalidite.NULLY_PROJECTEDPOINT, "Pas d'objet de type \"Y\" dans ce \"ProjectedPoint\".");
		}
		else
			projectedPoint.setY(castorY);
		
		// ProjectionType optionnel
		String castorProjectionType = catorProjectedPoint.getProjectionType();
		if (castorProjectionType == null)
			LoggingManager.log(logger, "Pas d'objet de type \"ProjectionType\" dans ce \"ProjectedPoint\".", Level.INFO);
		else {
			castorProjectionType = castorProjectionType.trim();
			if (castorProjectionType.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"ProjectionType\" dans ce \"ProjectedPoint\" est vide.", Level.WARN);
			else
				projectedPoint.setProjectionType(castorProjectionType);
		}
		
		return projectedPoint;
	}
}
