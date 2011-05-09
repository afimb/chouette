package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.TimeSlot;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.exolab.castor.types.Time;

class TimeSlotProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.TimeSlotProducer.class);
	private              ValidationException validationException;
	private              TimeSlot            timeSlot            = null;
	
	TimeSlotProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setTimeSlot(TimeSlot timeSlot) {
		this.timeSlot = timeSlot;
	}
	
	TimeSlot getTimeSlot() {
		return timeSlot;
	}
	
	TimeSlot getASG(chouette.schema.TimeSlot castorTimeSlot) {
		timeSlot = new TimeSlot();
		String[] params = null;
		
		// ObjectId obligatoire
		String castorObjectId = castorTimeSlot.getObjectId();
		if (castorObjectId == null) {
			LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"TimeSlot\".", Level.ERROR);
			validationException.add(TypeInvalidite.NOOBJECTID_TIMESLOT, "Pas de \"objectId\" pour ce \"TimeSlot\".");
		}
		else {
			castorObjectId = castorObjectId.trim();
			if (castorObjectId.length() == 0) {
				LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"TimeSlot\".", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOOBJECTID_TIMESLOT, "Pas de \"objectId\" pour ce \"TimeSlot\".");
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorObjectId)) {
					params = LoggingManager.getParams(castorObjectId);
					LoggingManager.log(logger, "L'\"objectId\" () pour ce \"TimeSlot\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOBJECTID_TIMESLOT, "L'\"objectId\" () pour ce \"TimeSlot\" est invalide.", params);
				}
				timeSlot.setObjectId(castorObjectId);		
			}
		}
		
		// ObjectVersion optionnel
		if (castorTimeSlot.hasObjectVersion()) {
			int castorObjectVersion = (int)castorTimeSlot.getObjectVersion();
			if (castorObjectVersion < 1) {
				params = LoggingManager.getParams(""+castorObjectVersion, castorTimeSlot.getObjectId());
				LoggingManager.log(logger, "La version () \"objectVersion\" du \"TimeSlot\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDOBJECTVERSION_TIMESLOT, "La version () \"objectVersion\" du \"TimeSlot\" () est invalide.", params);
			}
			else
				timeSlot.setObjectVersion(castorObjectVersion);
		}
		else {
			params = LoggingManager.getParams(castorTimeSlot.getObjectId());
			LoggingManager.log(logger, "Pas d'\"objectVersion\" pour ce \"TimeSlot\" ().", params, Level.INFO);
		}
		
		// CreationTime optionnel
		java.util.Date castorCreationTime = castorTimeSlot.getCreationTime();
		if (castorCreationTime == null) {
			params = LoggingManager.getParams(castorTimeSlot.getObjectId());
			LoggingManager.log(logger, "Pas de \"creationTime\" pour ce \"TimeSlot\" ().", params, Level.INFO);
		}
		else
			if (castorCreationTime.after(new java.util.Date(System.currentTimeMillis()))) {
				params = LoggingManager.getParams(castorCreationTime.toString(), castorTimeSlot.getObjectId());
				LoggingManager.log(logger, "La \"creationTime\" () de ce \"TimeSlot\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCREATIONTIME_TIMESLOT, "La \"creationTime\" () de ce \"TimeSlot\" () est invalide.", params);
			}
			else
				timeSlot.setCreationTime(castorCreationTime);
		
		// CreatorId optionnel
		String castorCreatorId = castorTimeSlot.getCreatorId();
		params = new String[]{castorTimeSlot.getObjectId()};			
		if (castorCreatorId == null)
			LoggingManager.log(logger, "Pas de \"creatorId\" pour ce \"TimeSlot\" ().", params, Level.INFO);
		else {
			castorCreatorId = castorCreatorId.trim();
			if (castorCreatorId.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"creatorId\" dans ce \"TimeSlot\" () est vide.", params, Level.WARN);
			else
				timeSlot.setCreatorId(castorCreatorId);
		}
		
		// BeginningSlotTime obligatoire
		Time castorBeginningSlotTime = castorTimeSlot.getBeginningSlotTime();
		if (castorBeginningSlotTime == null) {
			LoggingManager.log(logger, "Pas de \"beginningSlotTime\" pour ce \"TimeSlot\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOBEGINNINGSLOTTIME_TIMESLOT, "Pas de \"beginningSlotTime\" pour ce \"TimeSlot\" ().", params);
		}
		else
			timeSlot.setBeginningSlotTime(castorBeginningSlotTime);
		
		// EndSlotTime obligatoire
		Time castorEndSlotTime = castorTimeSlot.getEndSlotTime();
		if (castorEndSlotTime == null) {
			LoggingManager.log(logger, "Pas de \"endSlotTime\" pour ce \"TimeSlot\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOENDSLOTTIME_TIMESLOT, "Pas de \"endSlotTime\" pour ce \"TimeSlot\" ().", params);
		}
		else
			timeSlot.setEndSlotTime(castorEndSlotTime);
		
		// FirstDepartureTimeInSlot obligatoire
		Time castorFirstDepartureTimeInSlot = castorTimeSlot.getFirstDepartureTimeInSlot();
		if (castorFirstDepartureTimeInSlot == null) {
			LoggingManager.log(logger, "Pas de \"firstDepartureTimeInSlot\" pour ce \"TimeSlot\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOFIRSTDEPARTURETIMEINSLOTSLOTTIME_TIMESLOT, "Pas de \"firstDepartureTimeInSlot\" pour ce \"TimeSlot\" ().", params);
		}
		else
			timeSlot.setFirstDepartureTimeInSlot(castorFirstDepartureTimeInSlot);
		
		// LastDepartureTimeInSlot obligatoire
		Time castorLastDepartureTimeInSlot = castorTimeSlot.getLastDepartureTimeInSlot();
		if (castorLastDepartureTimeInSlot == null) {
			LoggingManager.log(logger, "Pas de \"lastDepartureTimeInSlot\" pour ce \"TimeSlot\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOLASTDEPARTURETIMEINSLOTSLOTTIME_TIMESLOT, "Pas de \"lastDepartureTimeInSlot\" pour ce \"TimeSlot\" ().", params);
		}
		else
			timeSlot.setLastDepartureTimeInSlot(castorLastDepartureTimeInSlot);
		
		return timeSlot;
	}
}
