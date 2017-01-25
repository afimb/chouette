package mobi.chouette.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import mobi.chouette.model.Timetable;
import mobi.chouette.model.statistics.LineAndTimetable;

@Stateless
public class TimetableDAOImpl extends GenericDAOImpl<Timetable>implements TimetableDAO {

	public TimetableDAOImpl() {
		super(Timetable.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public Collection<LineAndTimetable> getAllTimetableForAllLines() {

		Query q = em.createNativeQuery("select distinct l.id as line_id, vjt.time_table_id as timetable_id "
				+ "from time_tables_vehicle_journeys as vjt "
				+ "left join vehicle_journeys vj on vjt.vehicle_journey_id=vj.id "
				+ "left join routes as r on vj.route_id=r.id "
				+ "right join lines as l on r.line_id=l.id order by line_id");

		@SuppressWarnings("unchecked")
		List<Object[]> resultList2 = q.getResultList();

		Set<Long> timetableIds = new HashSet<Long>();
		for (Object[] lineToTimetablesMap : resultList2) {
			if (lineToTimetablesMap.length > 0 && lineToTimetablesMap[1] != null) {
				timetableIds.add(Long.valueOf(((BigInteger) lineToTimetablesMap[1]).longValue()));
			}
		}

		List<Timetable> timetables = findAll(timetableIds);

		Map<Long, Timetable> timetableIdToTimetable = new HashMap<>();
		for (Timetable t : timetables) {
			timetableIdToTimetable.put(t.getId(), t);
		}

		Map<Long, LineAndTimetable> lineToTimetablesMap = new HashMap<>();

		for (Object[] lineTimetableIdPair : resultList2) {
			LineAndTimetable lat = lineToTimetablesMap.get(lineTimetableIdPair[0]);
			if (lat == null) {
				lat = new LineAndTimetable(Long.valueOf(((BigInteger) lineTimetableIdPair[0]).longValue()),
						new ArrayList<Timetable>());
				lineToTimetablesMap.put(lat.getLineId(), lat);
			}
			if (lineTimetableIdPair.length > 0 && lineTimetableIdPair[1] != null) {
			lat.getTimetables()
					.add(timetableIdToTimetable.get(Long.valueOf(((BigInteger) lineTimetableIdPair[1]).longValue())));
			}
		}

		return lineToTimetablesMap.values();

	}

}
