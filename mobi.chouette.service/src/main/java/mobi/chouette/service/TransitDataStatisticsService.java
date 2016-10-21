package mobi.chouette.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.TimetableDAO;
import mobi.chouette.persistence.hibernate.ContextHolder;

@Singleton(name = TransitDataStatisticsService.BEAN_NAME)
@Log4j
public class TransitDataStatisticsService {

	public static final String BEAN_NAME = "TransitDataStatisticsService";

	@EJB
	LineDAO lineDAO;

	@EJB
	TimetableDAO timetableDAO;

	/**
	 * Returns a list of Lines grouped by Line "number". Create merged timetable periods. Not supporting frequency based yet
	 * @param referential
	 * @return
	 * @throws ServiceException
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public LineStatistics getLineStatistics(String referential) throws ServiceException {

		ContextHolder.setContext(referential);

		Map<String, PublicLine> publicLines = new HashMap<String, PublicLine>();

		List<mobi.chouette.model.Line> lines = lineDAO.findAll();

		for (mobi.chouette.model.Line l : lines) {
			// TODO replace with 1 combined query, this one creates 1 query pr line
			
			log.info("Finding timetables for line " + l.getObjectId());

			PublicLine publicLine = publicLines.get(l.getNumber());
			if (publicLine == null) {
				publicLine = new PublicLine();
				publicLine.number = l.getNumber();
				publicLine.lines = new ArrayList<>();
				publicLine.effectivePeriods = new ArrayList<>();
				publicLines.put(l.getNumber(), publicLine);
			}

			Line line = new Line();
			line.objectId = l.getObjectId();
			line.id = l.getId();
			line.name = l.getName();
			line.timetables = new ArrayList<Timetable>();

			publicLine.lines.add(line);

			Collection<mobi.chouette.model.Timetable> timetableForLine = timetableDAO.getTimetableForLine(l);

			for (mobi.chouette.model.Timetable t : timetableForLine) {
				Timetable timetable = new Timetable();
				timetable.periods = new ArrayList<Period>();
				timetable.id = t.getId();
				timetable.objectId = t.getObjectId();

				line.timetables.add(timetable);

				if(t.getPeriods() != null && t.getPeriods().size() > 0) {
					// Use periods
					for (mobi.chouette.model.Period p : t.getPeriods()) {
						
						Period period = new Period();
						period.from = p.getStartDate();
						period.to = p.getEndDate();

						timetable.periods.add(period);
					}
				} else {
					// Use timetable from/to as period
					Period period = new Period();
					period.from = t.getStartOfPeriod();
					period.to = t.getEndOfPeriod();

					timetable.periods.add(period);
					
				}
				
			}
		}

		for (PublicLine pl : publicLines.values()) {
			Set<Period> uniquePeriods = new TreeSet<Period>();
			for (Line l : pl.lines) {
				for (Timetable t : l.timetables) {
					t.periods = mergeOverlappingPeriods(t.periods);
					uniquePeriods.addAll(t.periods);
				}
				
				// TODO removing timetables from output for now - eats space
				//l.timetables = null;
			}

			pl.effectivePeriods.addAll(uniquePeriods);
			pl.effectivePeriods = mergeOverlappingPeriods(pl.effectivePeriods);
			
			
		}

		LineStatistics lineStats = new LineStatistics();
		lineStats.publicLines = new ArrayList<PublicLine>(publicLines.values());
		Collections.sort(lineStats.publicLines);

		return lineStats;
	}

	public List<Period> mergeOverlappingPeriods(List<Period> periods) {
		return merge(periods);
	}

	@Deprecated
	public List<Period> breakOverlappingIntervals(List<Period> sourceList) {

		TreeMap<Date, Integer> endPoints = new TreeMap<>();

		// Fill the treeMap from the TimeInterval list. For each start point,
		// increment
		// the value in the map, and for each end point, decrement it.

		for (Period interval : sourceList) {
			Date start = interval.from;
			if (endPoints.containsKey(start)) {
				endPoints.put(start, endPoints.get(start) + 1);
			} else {
				endPoints.put(start, 1);
			}
			Date end = interval.to;
			if (endPoints.containsKey(end)) {
				endPoints.put(end, endPoints.get(start) - 1);
			} else {
				endPoints.put(end, -1);
			}
		}

		int curr = 0;
		Date currStart = null;

		// Iterate over the (sorted) map. Note that the first iteration is used
		// merely to initialize curr and currStart to meaningful values, as no
		// interval precedes the first point.

		List<Period> targetList = new ArrayList<>();
		for (Map.Entry<Date, Integer> e : endPoints.entrySet()) {
			if (curr > 0) {
				targetList.add(new Period(currStart, e.getKey()));
			}
			curr += e.getValue();
			currStart = e.getKey();
		}
		return targetList;
	}

	public List<Period> merge(List<Period> intervals) {

		if (intervals.size() == 0 || intervals.size() == 1)
			return intervals;

		Collections.sort(intervals, new Comparator<Period>() {

			@Override
			public int compare(Period o1, Period o2) {
				return o1.from.compareTo(o2.from);
			}
		});

		Period first = intervals.get(0);
		Date start = first.from;
		Date end = first.to;

		List<Period> result = new ArrayList<Period>();

		for (int i = 1; i < intervals.size(); i++) {
			Period current = intervals.get(i);
			if (!current.from.after(end)) {
				end = current.to.before(end) ? end : current.to;
			} else {
				result.add(new Period(start, end));
				start = current.from;
				end = current.to;
			}
		}

		result.add(new Period(start, end));
		return result;
	}

	@NoArgsConstructor
	@XmlRootElement(name = "lineStatistics")
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(propOrder = { "publicLines" })
	public class LineStatistics {
		List<PublicLine> publicLines;
	}

	@NoArgsConstructor
	@XmlRootElement(name = "lineStatistics")
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(propOrder = { "number", "effectivePeriods", "lines" })
	public class PublicLine implements Comparable<PublicLine> {
		String number;
		List<Line> lines;
		List<Period> effectivePeriods;

		@Override
		public int compareTo(PublicLine o) {
			return number.compareTo(o.number);
		}
	}

	@NoArgsConstructor
	@XmlRootElement(name = "line")
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(propOrder = { "id", "timetables" })
	public class Line {
		Long id;
		String objectId;
		String name;
		List<Timetable> timetables;
	}

	@NoArgsConstructor
	@XmlRootElement(name = "timetable")
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(propOrder = { "id", "periods" })
	public class Timetable {
		Long id;
		String objectId;
		List<Period> periods;
	}

	@NoArgsConstructor
	@XmlRootElement(name = "period")
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(propOrder = { "from", "to" })
	public class Period implements Comparable<Period> {
		Date from;

		Date to;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((from == null) ? 0 : from.hashCode());
			result = prime * result + ((to == null) ? 0 : to.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Period other = (Period) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (from == null) {
				if (other.from != null)
					return false;
			} else if (!from.equals(other.from))
				return false;
			if (to == null) {
				if (other.to != null)
					return false;
			} else if (!to.equals(other.to))
				return false;
			return true;
		}

		@Override
		public int compareTo(Period o) {
			int f = from.compareTo(o.from);
			if (f == 0) {
				f = to.compareTo(o.to);
			}

			return f;
		}

		private TransitDataStatisticsService getOuterType() {
			return TransitDataStatisticsService.this;
		}

		public Period(Date from, Date to) {
			super();
			this.from = from;
			this.to = to;
		}
	}
}
