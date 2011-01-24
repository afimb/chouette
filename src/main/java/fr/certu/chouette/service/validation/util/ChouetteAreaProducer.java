package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.util.AreaCentroidProducer;
import fr.certu.chouette.service.validation.util.ChouetteAreaProducer;
import fr.certu.chouette.service.validation.util.StopAreaProducer;
import fr.certu.chouette.service.validation.AreaCentroid;
import fr.certu.chouette.service.validation.ChouetteArea;
import fr.certu.chouette.service.validation.StopArea;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class ChouetteAreaProducer {
    
    private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.ChouetteAreaProducer.class);
    private              ValidationException validationException;
    private              ChouetteArea        chouetteArea        = null;

    ChouetteAreaProducer(ValidationException validationException) {
        setValidationException(validationException);
    }
    
    void setValidationException(ValidationException validationException) {
        this.validationException = validationException;
    }

    ValidationException getValidationException() {
        return validationException;
    }
    
    void setChouetteArea(ChouetteArea chouetteArea) {
        this.chouetteArea = chouetteArea;
    }

    ChouetteArea getChouetteArea() {
        return chouetteArea;
    }
    
    ChouetteArea getASG(chouette.schema.ChouetteArea castorChouetteArea) {
        chouetteArea = new ChouetteArea();
        
        // StopArea [0..w]
        int numberOfStopArea = castorChouetteArea.getStopAreaCount();
        if (numberOfStopArea <= 0)
            LoggingManager.log(logger, "Pas de \"StopArea\" pour ce \"ChouetteArea\".", Level.INFO);
        for (int i = 0; i < numberOfStopArea; i++) {
            chouette.schema.StopArea castorStopArea = castorChouetteArea.getStopArea(i);
            if (castorStopArea == null) {
                LoggingManager.log(logger, "Ce \"ChouetteArea\" contient des \"StopArea\" null.", Level.WARN);
                continue;
            }
            StopArea stopArea = (new StopAreaProducer(validationException)).getASG(castorStopArea);
            if (stopArea == null)
                LoggingManager.log(logger, "Une erreure s'est produite lors de la construction d'un objet \"StopArea\".", Level.ERROR);
            else {
                chouetteArea.addStopArea(stopArea);
                stopArea.setChouetteArea(chouetteArea);
            }
        }
        
        // AreaCentroid [0..w]
        int numberOfAreaCentroids = castorChouetteArea.getAreaCentroidCount();
        if (numberOfAreaCentroids <= 0)
            LoggingManager.log(logger, "Pas de \"AreaCentroid\" pour ce \"ChouetteArea\".", Level.INFO);
        for (int i = 0; i < numberOfAreaCentroids; i++) {
            chouette.schema.AreaCentroid castorAreaCentroid = castorChouetteArea.getAreaCentroid(i);
            if (castorAreaCentroid == null) {
                LoggingManager.log(logger, "Ce \"ChouetteArea\" contient des \"AreaCentroid\" null.", Level.WARN);
                continue;
            }
            AreaCentroid areaCentroid = (new AreaCentroidProducer(validationException)).getASG(castorAreaCentroid);
            if (areaCentroid == null)
                LoggingManager.log(logger, "Une erreure s'est produite lors de la construction d'un objet \"AreaCentroid\".", Level.ERROR);
            else {
                chouetteArea.addAreaCentroid(areaCentroid);
                areaCentroid.setChouetteArea(chouetteArea);
            }
        }
        return chouetteArea;
    }
}
