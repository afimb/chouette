package mobi.chouette.model.util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Period;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.DayTypeEnum;

public abstract class NamingUtil {

	public static String getName(Network object) {
		if (isFilled(object.getName()))
			return object.getName();
		return object.getObjectId();
	}

	public static String getName(Company object) {
		if (isFilled(object.getName()))
			return object.getName();
		return object.getObjectId();
	}

	public static String getName(GroupOfLine object) {
		if (isFilled(object.getName()))
			return object.getName();
		return object.getObjectId();
	}

	public static String getName(StopArea object) {
		if (isFilled(object.getName()))
			return object.getName();
		return object.getObjectId();
	}

	public static String getName(AccessPoint object) {
		if (isFilled(object.getName()))
			return object.getName();
		return object.getObjectId();
	}

	public static String getName(AccessLink object) {
		if (isFilled(object.getName()))
			return object.getName();
		return object.getObjectId();
	}

	public static String getName(ConnectionLink object) {
		if (isFilled(object.getName()))
			return object.getName();
		return object.getObjectId();
	}

	public static String getName(Timetable object) {
		if (isFilled(object.getComment()))
			return object.getComment();
		return object.getObjectId();
	}

	public static String getName(StopPoint object) {
		// if (object.getContainedInStopArea() != null) {
		// if (isFilled(object.getContainedInStopArea().getName()))
		// return object.getContainedInStopArea().getName();
		// }
		return object.getObjectId();
	}

	public static String getName(Line object) {
		if (isFilled(object.getName()))
			return object.getName();
		if (isFilled(object.getPublishedName()))
			return object.getPublishedName();
		if (isFilled(object.getNumber()))
			return object.getNumber();
		return object.getObjectId();
	}

	public static String getName(Route object) {
		if (isFilled(object.getName()))
			return object.getName();
		if (isFilled(object.getPublishedName()))
			return object.getPublishedName();
		if (isFilled(object.getNumber()))
			return object.getNumber();
		return object.getObjectId();
	}

	public static String getName(JourneyPattern object) {
		if (isFilled(object.getName()))
			return object.getName();
		if (isFilled(object.getPublishedName()))
			return object.getPublishedName();
		return object.getObjectId();
	}

	public static String getName(VehicleJourney object) {
		if (isFilled(object.getPublishedJourneyName()))
			return object.getPublishedJourneyName();
		if (object.getNumber() != null)
			return object.getNumber().toString();
		return object.getObjectId();
	}

	public static boolean isFilled(String data) {
		return (data != null && !data.trim().isEmpty());
	}

	public static boolean isEmpty(String data) {
		return (data == null || data.trim().isEmpty());
	}

	public static void setDefaultName(ConnectionLink link) {
		if (isFilled(link.getName()))
			return;
		if (link.getStartOfLink() == null || isEmpty(link.getStartOfLink().getName()))
			return;
		if (link.getEndOfLink() == null || isEmpty(link.getEndOfLink().getName()))
			return;

		link.setName(link.getStartOfLink().getName() + " -> " + link.getEndOfLink().getName());
	}

	public static void setDefaultName(AccessLink link) {
		if (isFilled(link.getName()))
			return;
		if (link.getStopArea() == null || isEmpty(link.getStopArea().getName()))
			return;
		if (link.getAccessPoint() == null || isEmpty(link.getAccessPoint().getName()))
			return;
		switch (link.getLinkOrientation()) {
		case AccessPointToStopArea:
			link.setName(link.getAccessPoint().getName() + " -> " + link.getStopArea().getName());
			break;
		case StopAreaToAccessPoint:
			link.setName(link.getStopArea().getName() + " -> " + link.getAccessPoint().getName());
			break;
		}
	}

	public static void setDefaultName(Timetable timetable) {
		if (isFilled(timetable.getComment()))
			return;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String monday = (timetable.getDayTypes().contains(DayTypeEnum.Monday)) ? "Mo" : "..";
		String tuesday = (timetable.getDayTypes().contains(DayTypeEnum.Tuesday)) ? "Tu" : "..";
		String wednesday = (timetable.getDayTypes().contains(DayTypeEnum.Wednesday)) ? "We" : "..";
		String thursday = (timetable.getDayTypes().contains(DayTypeEnum.Thursday)) ? "Th" : "..";
		String friday = (timetable.getDayTypes().contains(DayTypeEnum.Friday)) ? "Fr" : "..";
		String saturday = (timetable.getDayTypes().contains(DayTypeEnum.Saturday)) ? "Sa" : "..";
		String sunday = (timetable.getDayTypes().contains(DayTypeEnum.Sunday)) ? "Su" : "..";

		Date firstDate = null;
		Date lastDate = null;
		if (timetable.getPeriods() != null && !timetable.getPeriods().isEmpty()) {
			for (Period period : timetable.getPeriods()) {
				if (firstDate == null || period.getStartDate().before(firstDate))
					firstDate = period.getStartDate();
				if (lastDate == null || period.getEndDate().after(lastDate))
					lastDate = period.getEndDate();
			}
		}
		if (timetable.getCalendarDays() != null && !timetable.getCalendarDays().isEmpty()) {
			Calendar cal = Calendar.getInstance();
			for (Date date : timetable.getPeculiarDates()) {
				cal.setTime(date);
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
					monday = "Mo";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
					tuesday = "Tu";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
					wednesday = "We";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)
					thursday = "Th";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
					friday = "Fr";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
					saturday = "Sa";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
					sunday = "Su";
				if (firstDate == null || date.before(firstDate))
					firstDate = date;
				if (lastDate == null || date.after(lastDate))
					lastDate = date;
			}
		}

		// security if timetable is empty
		if (firstDate != null && lastDate != null) {
			String comment = timetable.objectIdSuffix() + " : " + format.format(firstDate) + " -> "
					+ format.format(lastDate) + " : " + monday + tuesday + wednesday + thursday + friday + saturday
					+ sunday;
			timetable.setComment(comment);
		} else {
			timetable.setComment(timetable.objectIdSuffix() + " : Empty timetable");
		}
	}
}
