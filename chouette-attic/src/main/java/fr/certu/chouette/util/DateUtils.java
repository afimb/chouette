package fr.certu.chouette.util;

import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

	// PREND UNE DATE COMME PARAMETRE ET RETOURNE
	// UNE DATE REPRESENTANT LE JOUR QUI SUIT
	// DIRECTEMENT CETTE DATE
	public static Date getDayIncreasedByOneDay(final Date day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(day);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date increasedDay = calendar.getTime();Math.abs(0);
		return increasedDay;
	}
}
