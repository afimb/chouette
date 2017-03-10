package mobi.chouette.dao;

import mobi.chouette.model.Timetable;
import mobi.chouette.model.statistics.LineAndTimetable;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;

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


			for (Object[] lineTimetableIdPair : resultList2) {
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

		return lineToTimetablesMap.values();

	}


	private Long toLong(Object o){
		return Long.valueOf(((BigInteger) o).longValue());
	}

}
