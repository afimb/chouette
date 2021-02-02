package mobi.chouette.dao;

import lombok.extern.log4j.Log4j;
import mobi.chouette.dao.exception.ChouetteStatisticsTimeoutException;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.statistics.LineAndTimetable;
import org.hibernate.exception.GenericJDBCException;
import org.joda.time.LocalDate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@Log4j
public class TimetableDAOImpl extends GenericDAOImpl<Timetable>implements TimetableDAO {

	/**
	 * see https://www.postgresql.org/docs/9.6/errcodes-appendix.html
	 */
	private static final String SQL_ERROR_CODE_QUERY_CANCELLED = "57014";

	/**
	 * Query timeout in second
	 */
	private static final int QUERY_TIMEOUT = 10;

	public TimetableDAOImpl() {
		super(Timetable.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public Collection<LineAndTimetable> getAllTimetableForAllLines() throws ChouetteStatisticsTimeoutException {

		Query q = em.createNativeQuery("select distinct l.id as line_id, vjt.time_table_id as timetable_id "
				+ "from time_tables_vehicle_journeys as vjt "
				+ "left join vehicle_journeys vj on vjt.vehicle_journey_id=vj.id "
				+ "left join routes as r on vj.route_id=r.id "
				+ "right join lines as l on r.line_id=l.id order by line_id");
		q.unwrap(org.hibernate.Query.class).setTimeout(QUERY_TIMEOUT);

		List<Object[]> resultList;
		try {
			resultList = q.getResultList();
		} catch (PersistenceException e) {
			if (e.getCause() instanceof GenericJDBCException) {
				GenericJDBCException genericJDBCException = (GenericJDBCException) e.getCause();
				if (SQL_ERROR_CODE_QUERY_CANCELLED.equals(genericJDBCException.getSQLState())) {
					throw new ChouetteStatisticsTimeoutException(e);
				} else {
					throw e;
				}
			} else {
				throw e;
			}
		}

		Set<Long> timetableIds = new HashSet<Long>();
		for (Object[] lineToTimetablesMap : resultList) {
			if (lineToTimetablesMap.length > 0 && lineToTimetablesMap[1] != null) {
				timetableIds.add(toLong(lineToTimetablesMap[1]));
			}
		}
		Map<Long, LineAndTimetable> lineToTimetablesMap = new HashMap<>();

		if (timetableIds.size() > 0) {
			List<Timetable> timetables = findAll(timetableIds);

			Map<Long, Timetable> timetableIdToTimetable = new HashMap<>();
			for (Timetable t : timetables) {
				timetableIdToTimetable.put(t.getId(), t);
			}


			for (Object[] lineTimetableIdPair : resultList) {
				Long lineId = toLong(lineTimetableIdPair[0]);
				LineAndTimetable lat = lineToTimetablesMap.get(lineId);
				if (lat == null) {
					lat = new LineAndTimetable(lineId,
							                          new ArrayList<>());
					lineToTimetablesMap.put(lat.getLineId(), lat);
				}
				if (lineTimetableIdPair.length > 0 && lineTimetableIdPair[1] != null) {
					lat.getTimetables()
							.add(timetableIdToTimetable.get(toLong( lineTimetableIdPair[1])));
				}
			}
		}

		// For lines that define DatedServiceJourneys: create an additional TimeTable that collects the operating days for the DatedServiceJourneys
		Map<Long, Collection<LocalDate>> dsjOperatingDays = getDSJOperatingDaysPerLine();
		for (Long lineID : dsjOperatingDays.keySet()) {
			Timetable dsjTimeTable = new Timetable();
			dsjTimeTable.setId(-1L);
			dsjTimeTable.setObjectId("Dated Service Journeys");
			dsjTimeTable.setCalendarDays(dsjOperatingDays.get(lineID).stream().map(operatingDay -> new CalendarDay(operatingDay, true)).collect(Collectors.toList()));
			lineToTimetablesMap.computeIfAbsent(lineID, k -> new LineAndTimetable(k, new ArrayList<>())).getTimetables().add(dsjTimeTable);
		}

		return lineToTimetablesMap.values();

	}

	/**
	 * Return a map that matches the line ID with the DSJ operating days for that line.
	 * DatedServiceJourney of type Cancellation and Replaced are filtered out.
	 *
	 * @return a map that matches the line ID with the DSJ operating days for that line.
	 */
	private Map<Long, Collection<LocalDate>> getDSJOperatingDaysPerLine() {

		Query q = em.createNativeQuery("select distinct line.id, dsj.operating_day   from dated_service_journeys dsj " +
				"    inner join vehicle_journeys vj on dsj.vehicle_journey_id = vj.id " +
				"    inner join routes r on vj.route_id = r.id " +
				"    inner join  lines line on r.line_id = line.id" +
				"    where dsj.service_alteration is null or dsj.service_alteration not in ('Cancellation','Replaced') ");

		List<Object[]> resultList = q.getResultList();
		Map<Long, Collection<LocalDate>> lineToDSJOperatingDaysMap = new HashMap<>();
		for (Object[] lineOperatingDayPair : resultList) {
			Long lineId = toLong(lineOperatingDayPair[0]);
			lineToDSJOperatingDaysMap.computeIfAbsent(lineId, k -> new ArrayList<>()).add(new LocalDate(lineOperatingDayPair[1]));
		}

		return lineToDSJOperatingDaysMap;
	}


	private Long toLong(Object o){
		return Long.valueOf(((BigInteger) o).longValue());
	}

}
