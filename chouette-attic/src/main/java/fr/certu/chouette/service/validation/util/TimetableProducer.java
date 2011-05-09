package fr.certu.chouette.service.validation.util;

import java.util.HashSet;
import java.util.Set;

import fr.certu.chouette.service.validation.DayType;
import fr.certu.chouette.service.validation.Period;
import fr.certu.chouette.service.validation.Timetable;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.exolab.castor.types.Date;

class TimetableProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.TimetableProducer.class);
	private              ValidationException validationException;
	private              Timetable           timetable           = null;
	
	TimetableProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setTimetable(Timetable timetable) {
		this.timetable = timetable;
	}
	
	Timetable getTimetable() {
		return timetable;
	}
	
	Timetable getASG(chouette.schema.Timetable castorTimetable) {
		timetable = new Timetable();
		String[] params = null;
		
		// ObjectId obligatoire
		String castorObjectId = castorTimetable.getObjectId();
		if (castorObjectId == null) {
			LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"Timetable\".", Level.ERROR);
			validationException.add(TypeInvalidite.NOOBJECTID_TIMETABLE, "Pas de \"objectId\" pour ce \"Timetable\".");
		}
		else {
			castorObjectId = castorObjectId.trim();
			if (castorObjectId.length() == 0) {
				LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"Timetable\".", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOOBJECTID_TIMETABLE, "Pas de \"objectId\" pour ce \"Timetable\".");
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorObjectId)) {
					params = LoggingManager.getParams(castorObjectId);
					LoggingManager.log(logger, "L'\"objectId\" () pour ce \"Timetable\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOBJECTID_TIMETABLE, "L'\"objectId\" () pour ce \"Timetable\" est invalide.", params);
				}
				timetable.setObjectId(castorObjectId);		
			}
		}
		
		// ObjectVersion optionnel
		if (castorTimetable.hasObjectVersion()) {
			int castorObjectVersion = (int)castorTimetable.getObjectVersion();
			if (castorObjectVersion < 1) {
				params = LoggingManager.getParams(""+castorObjectVersion, castorTimetable.getObjectId());
				LoggingManager.log(logger, "La version () \"objectVersion\" du \"Timetable\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDOBJECTVERSION_TIMETABLE, "La version () \"objectVersion\" du \"Timetable\" () est invalide.", params);
			}
			else
				timetable.setObjectVersion(castorObjectVersion);
		}
		else {
			params = LoggingManager.getParams(castorTimetable.getObjectId());
			LoggingManager.log(logger, "Pas d'\"objectVersion\" pour ce \"Timetable\" ().", params, Level.INFO);
		}
		
		// CreationTime optionnel
		java.util.Date castorCreationTime = castorTimetable.getCreationTime();
		if (castorCreationTime == null) {
			params = LoggingManager.getParams(castorTimetable.getObjectId());
			LoggingManager.log(logger, "Pas de \"creationTime\" pour ce \"Timetable\" ().", params, Level.INFO);
		}
		else
			if (castorCreationTime.after(new java.util.Date(System.currentTimeMillis()))) {
				params = LoggingManager.getParams(castorCreationTime.toString(), castorTimetable.getObjectId());
				LoggingManager.log(logger, "La \"creationTime\" () de ce \"Timetable\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCREATIONTIME_TIMETABLE, "La \"creationTime\" () de ce \"Timetable\" () est invalide.", params);
			}
			else
				timetable.setCreationTime(castorCreationTime);
		
		// CreatorId optionnel
		String castorCreatorId = castorTimetable.getCreatorId();
		params = new String[]{castorTimetable.getObjectId()};			
		if (castorCreatorId == null)
			LoggingManager.log(logger, "Pas de \"creatorId\" pour ce \"Timetable\" ().", params, Level.INFO);
		else {
			castorCreatorId = castorCreatorId.trim();
			if (castorCreatorId.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"creatorId\" dans ce \"Timetable\" () est vide.", params, Level.WARN);
			else
				timetable.setCreatorId(castorCreatorId);
		}
		
		// Version optionnel
		String castorVersion = castorTimetable.getVersion();
		if (castorVersion == null)
			LoggingManager.log(logger, "Pas de \"version\" pour ce \"Timetable\" ().", params, Level.INFO);
		else {
			castorVersion = castorVersion.trim();
			if (castorVersion.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"version\" dans ce \"Timetable\" () est vide.", params, Level.WARN);
			else
				timetable.setVersion(castorVersion);
		}
		
		// Period [0..w]
		if (castorTimetable.getPeriod() == null)
			LoggingManager.log(logger, "Pas de \"period\" pour ce \"Timetable\" ().", params, Level.INFO);
		for (int i = 0; i < castorTimetable.getPeriodCount(); i++) {
			chouette.schema.Period castorPeriod = castorTimetable.getPeriod(i);
			if (castorPeriod == null)
				LoggingManager.log(logger, "Une \"Period\" de cette \"Timetable\" () est null.", params, Level.WARN);
			else {
				Period period = (new PeriodProducer(validationException)).getASG(castorPeriod);
				if (period == null)
					LoggingManager.log(logger, "Erreur lors de la construction d'une \"Period\" de cette \"Timetable\" ().", params, Level.ERROR);
				else {
					timetable.addPeriod(period);
					period.setTimetable(timetable);
				}
			}
		}
		
		// CalendarDay [0..w]
		if (castorTimetable.getCalendarDay() == null)
			LoggingManager.log(logger, "Pas de \"calendarDay\" pour ce \"Timetable\" ().", params, Level.INFO);
		for (int i = 0; i < castorTimetable.getCalendarDayCount(); i++) {
			Date castorCalendarDay = castorTimetable.getCalendarDay(i);
			if (castorCalendarDay == null)
				LoggingManager.log(logger, "Un \"calendarDay\" de cette \"Timetable\" () est null.", params, Level.WARN);
			else
				timetable.addCalendarDay(castorCalendarDay);
		}
		
		// DayType [0..w]
		if (castorTimetable.getDayType() == null)
			LoggingManager.log(logger, "Pas de \"dayType\" pour ce \"Timetable\" ().", params, Level.INFO);
		for (int i = 0; i < castorTimetable.getDayTypeCount(); i++)
		{
			if (castorTimetable.getDayType(i) == null)
			{
				LoggingManager.log(logger, "Un \"dayType\" de cette \"Timetable\" () est null.", params, Level.WARN);
			}
			else
			{
				switch (castorTimetable.getDayType(i)) 
				{
					case FRIDAY:
						timetable.addDayType(DayType.FRIDAY);
						break;
					case MARKETDAY:
						timetable.addDayType(DayType.MARKETDAY);
						break;
					case MONDAY:
						timetable.addDayType(DayType.MONDAY);
						break;
					case PUBLICHOLLIDAY:
						timetable.addDayType(DayType.PUBLICHOLLIDAY);
						break;
					case SATURDAY:
						timetable.addDayType(DayType.SATURDAY);
						break;
					case SCHOOLHOLLIDAY:
						timetable.addDayType(DayType.SCHOOLHOLLIDAY);
						break;
					case SUNDAY:
						timetable.addDayType(DayType.SUNDAY);
						break;
					case THURSDAY:
						timetable.addDayType(DayType.THURSDAY);
						break;
					case TUESDAY:
						timetable.addDayType(DayType.TUESDAY);
						break;
					case WEDNESDAY:
						timetable.addDayType(DayType.WEDNESDAY);
						break;
					case WEEKDAY:
						timetable.addDayType(DayType.WEEKDAY);
						break;
					case WEEKEND:
						timetable.addDayType(DayType.WEEKEND);
						break;
					default:
						LoggingManager.log(logger, "Un \"dayType\" de cette \"Timetable\" () est invalide.", params, Level.ERROR);
						validationException.add(TypeInvalidite.INVALIDDAYTYPE_TIMETABLE, "Un \"dayType\" de cette \"Timetable\" () est invalide.", params);
				}
			}
		}
		// VehicleJourney [0..w]
		String[] castorVehicleJourneyIds = castorTimetable.getVehicleJourneyId();
		if (castorVehicleJourneyIds == null)
			LoggingManager.log(logger, "Pas de \"vehicleJourneyId\" pour ce \"Timetable\" ().", params, Level.INFO);
		else
			if (castorVehicleJourneyIds.length == 0)
				LoggingManager.log(logger, "Le \"vehicleJourneyId\" pour ce \"Timetable\" () est vide.", params, Level.INFO);
			else {
				Set<String> vehicleJourneyIds = new HashSet<String>();
				for (int i = 0; i < castorVehicleJourneyIds.length; i++)
					if ((castorVehicleJourneyIds[i] != null) && (castorVehicleJourneyIds[i].trim().length() > 0))
						if (!vehicleJourneyIds.add(castorVehicleJourneyIds[i].trim()))
							LoggingManager.log(logger, "La liste des \"vehicleJourneyId\" pour ce \"Timetable\" () contient des \"objectsId\" en double.", params, Level.WARN);
				if (vehicleJourneyIds.size() == 0)
					LoggingManager.log(logger, "La liste des \"vehicleJourneyId\" pour ce \"Timetable\" () ne contient que des \"objectsId\" vide.", params, Level.WARN);
				else {
					for (String vehicleJourneyId : vehicleJourneyIds)
						if (!MainSchemaProducer.isTridentLike(vehicleJourneyId)) {
							params = new String[]{vehicleJourneyId, timetable.getObjectId()};
							LoggingManager.log(logger, "Le \"vehicleJourneyId\" () pour ce \"Timetable\" () est invalide.", params, Level.ERROR);
							//validationException.add(TypeInvalidite.INVALIDVEHICLEJOURNEYID_TIMETABLE, "Le \"vehicleJourneyId\" () pour ce \"Timetable\" () est invalide.", params);
						}
					timetable.setVehicleJourneyIds((String[])vehicleJourneyIds.toArray(new String[0]));
				}
			}
		
		// Comment optionnel
		String castorComment = castorTimetable.getComment();
		params = new String[]{timetable.getObjectId()};			
		if (castorComment == null)
			LoggingManager.log(logger, "Pas de \"comment\" pour ce \"Timetable\" ().", params, Level.INFO);
		else {
			castorComment = castorComment.trim();
			if (castorComment.length() == 0)
				LoggingManager.log(logger, "Le \"comment\" pour ce \"timetable\" () est vide.", params, Level.WARN);
			else
				timetable.setComment(castorComment);
		}
		
		return timetable;
	}
}
