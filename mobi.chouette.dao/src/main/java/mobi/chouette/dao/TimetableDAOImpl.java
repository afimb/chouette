package mobi.chouette.dao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import mobi.chouette.model.Line;
import mobi.chouette.model.Timetable;

@Stateless
public class TimetableDAOImpl extends GenericDAOImpl<Timetable> implements TimetableDAO{

	public TimetableDAOImpl() {
		super(Timetable.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public Collection<Timetable> getTimetableForLine(Line l) {
		
		Query q  = em.createQuery("select distinct vj.timetables from VehicleJourney vj where vj.route.line.id = :lineId");
		q.setParameter("lineId"	, l.getId());
		
		@SuppressWarnings("unchecked")
		List<Timetable> resultList = q.getResultList();
		return resultList;
	}
	
	public class LineAndTimetable {
		Line line;
		
		Collection<Timetable> timetables;
	}
	
}
