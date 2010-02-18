package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.RouteExtension;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class RouteExtensionProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.ChouetteRouteProducer.class);
	private              ValidationException validationException;
	private              RouteExtension      routeExtension      = null;
	
	RouteExtensionProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setRouteExtension(RouteExtension routeExtension) {
		this.routeExtension = routeExtension;
	}
	
	RouteExtension getRouteExtension() {
		return routeExtension;
	}
	
	RouteExtension getASG(chouette.schema.RouteExtension castorRouteExtension) {
		if (castorRouteExtension == null)
			return null;
		if (castorRouteExtension.getWayBack() == null) {
			LoggingManager.log(logger, "La \"routeExtension\" pour ce \"ChouetteRoute\" est null.", Level.WARN);
			return null;
		}
		if (castorRouteExtension.getWayBack().trim().length() == 0) {
			LoggingManager.log(logger, "La \"routeExtension\" pour ce \"ChouetteRoute\" est vide.", Level.WARN);
			return null;
		}
		routeExtension = new RouteExtension();
		routeExtension.setWayBack(castorRouteExtension.getWayBack().trim());
		return routeExtension;
	}
}
