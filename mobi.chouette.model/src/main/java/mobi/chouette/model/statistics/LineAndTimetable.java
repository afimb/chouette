package mobi.chouette.model.statistics;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mobi.chouette.model.Timetable;

@AllArgsConstructor
@Getter
@Setter
public class LineAndTimetable {

	private Long lineId;

	private Collection<Timetable> timetables;
}
