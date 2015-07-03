package mobi.chouette.model.util;

import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;

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
//		if (object.getContainedInStopArea() != null) {
//			if (isFilled(object.getContainedInStopArea().getName()))
//				return object.getContainedInStopArea().getName();
//		}
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

	private static boolean isFilled(String data) {
		return (data != null && !data.isEmpty());
	}
}
