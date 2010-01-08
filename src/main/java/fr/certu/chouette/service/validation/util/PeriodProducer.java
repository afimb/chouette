package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.Period;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.exolab.castor.types.Date;

class PeriodProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.PeriodProducer.class);
	private              ValidationException validationException;
	private              Period              period              = null;
	
	PeriodProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setPeriod(Period period) {
		this.period = period;
	}
	
	Period getPeriod() {
		return period;
	}
	
	Period getASG(chouette.schema.Period castorPeriod) {
		period = new Period();
		String[] params = null;
		
		// StartOfPeriod obligatoire
		Date castorStartOfPeriod = castorPeriod.getStartOfPeriod();
		if (castorStartOfPeriod == null) {
			LoggingManager.log(logger, "Le \"startOfPeriod\" est obligatoire dans une \"Period\".", Level.ERROR);
			validationException.add(TypeInvalidite.NOSTARTOFPERIOD_PERIOD, "Le \"startOfPeriod\" est obligatoire dans une \"Period\".");
		}
		else
			period.setStartOfPeriod(castorStartOfPeriod);
		
		// EndOfPeriod obligatoire
		Date castorEndOfPeriod = castorPeriod.getEndOfPeriod();
		if (castorEndOfPeriod == null) {
			LoggingManager.log(logger, "Le \"endOfPeriod\" est obligatoire dans une \"Period\".", Level.ERROR);
			validationException.add(TypeInvalidite.NOENDOFPERIOD_PERIOD, "Le \"endOfPeriod\" est obligatoire dans une \"Period\".");
		}
		else
			period.setEndOfPeriod(castorEndOfPeriod);
		
		if ((period.getStartOfPeriod() != null) && (period.getEndOfPeriod() != null))
			if (period.getStartOfPeriod().toDate().after(period.getEndOfPeriod().toDate())) {
				params = new String[]{period.getStartOfPeriod().toString(), period.getEndOfPeriod().toString()};
				LoggingManager.log(logger, "Le \"startOfPeriod\" () est avant le \"endOfPeriod\" () dans une \"Period\".", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDENDSTARTOFPERIOD_PERIOD, "Le \"startOfPeriod\" () est avant le \"endOfPeriod\" () dans une \"Period\".", params);
			}
		
		return period;
	}
}
