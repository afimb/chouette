package fr.certu.chouette.service.validation.amivif.util;

import fr.certu.chouette.service.validation.amivif.ProjectedPoint;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class ProjectedPointProducer {
	
	private ValidationException		validationException;

	public ProjectedPointProducer(ValidationException validationException) {
		setValidationException(validationException);
	}

	public void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	public ValidationException getValidationException() {
		return validationException;
	}

	public ProjectedPoint getASG(amivif.schema.ProjectedPoint castorProjectedPoint) {
		if (castorProjectedPoint == null)
			return null;
		ProjectedPoint projectedPoint = new ProjectedPoint();
		
		// x obligatoire
		if (castorProjectedPoint.getX() == null)
			validationException.add(TypeInvalidite.NoX_ProjectedPoint, "Le \"ProjectedPoint\" doit avoir un valeur \"X\".");
		else
			projectedPoint.setX(castorProjectedPoint.getX());
		
		// y obligatoire
		if (castorProjectedPoint.getY() == null)
			validationException.add(TypeInvalidite.NoY_ProjectedPoint, "Le \"ProjectedPoint\" doit avoir un valeur \"Y\".");
		else
			projectedPoint.setY(castorProjectedPoint.getY());
		
		// projectionType optionnel
		projectedPoint.setProjectionType(castorProjectedPoint.getProjectionType());
		
		return projectedPoint;
	}

}
