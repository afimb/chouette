package mobi.chouette.dao;

import java.util.Collection;

import mobi.chouette.model.Line;
import mobi.chouette.model.Timetable;

public interface TimetableDAO extends GenericDAO<Timetable> {

	Collection<Timetable> getTimetableForLine(Line l);

}
