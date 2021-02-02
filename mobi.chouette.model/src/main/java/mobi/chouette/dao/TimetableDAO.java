package mobi.chouette.dao;

import java.util.Collection;

import mobi.chouette.dao.exception.ChouetteStatisticsTimeoutException;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.statistics.LineAndTimetable;

public interface TimetableDAO extends GenericDAO<Timetable> {

	/**
	 *
	 * @return an aggregated view for all timetables in all lines for the current referential.
	 * @throws ChouetteStatisticsTimeoutException if the statistics query times out, most likely due to an import process locking the database tables (TRUNCATE operations block SELECT queries)
	 */
	Collection<LineAndTimetable> getAllTimetableForAllLines() throws ChouetteStatisticsTimeoutException;
}
