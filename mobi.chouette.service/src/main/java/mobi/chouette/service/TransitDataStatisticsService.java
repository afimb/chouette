package mobi.chouette.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.extern.log4j.Log4j;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.TimetableDAO;
import mobi.chouette.model.statistics.Line;
import mobi.chouette.model.statistics.LineAndTimetable;
import mobi.chouette.model.statistics.LineStatistics;
import mobi.chouette.model.statistics.Period;
import mobi.chouette.model.statistics.PublicLine;
import mobi.chouette.model.statistics.Timetable;
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
	 * Returns a list of Lines grouped by Line "number". Create merged timetable
	 * periods. Not supporting frequency based yet
	 * 
	 * @param referential
	 * @return
	 * @throws ServiceException
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public LineStatistics getLineStatisticsByLineNumber(String referential) throws ServiceException {

		long methodStart = System.currentTimeMillis();
		ContextHolder.setContext(referential);

		Map<String, PublicLine> publicLines = new HashMap<String, PublicLine>();

		// Load list of lineIds with corresponding Timetables
		long now = System.currentTimeMillis();
		Collection<LineAndTimetable> allTimetableForAllLines = timetableDAO.getAllTimetableForAllLines();
		log.info("Timetables took " + (System.currentTimeMillis() - now) + "ms");

		// Find all ids and load all Chouette Lines
		Set<Long> lineIds = new HashSet<>();
		for (LineAndTimetable lat : allTimetableForAllLines) {
			lineIds.add(lat.getLineId());
		}
		now = System.currentTimeMillis();
		List<mobi.chouette.model.Line> lines = lineDAO.findAll(lineIds);
		log.info("Lines took " + (System.currentTimeMillis() - now) + "ms");

		Map<Long, mobi.chouette.model.Line> lineIdToLine = new HashMap<>();
		for (mobi.chouette.model.Line l : lines) {
			lineIdToLine.put(l.getId(), l);
		}

		for (LineAndTimetable lat : allTimetableForAllLines) {
			mobi.chouette.model.Line l = lineIdToLine.get(lat.getLineId());

			PublicLine publicLine = publicLines.get(l.getNumber());
			if (publicLine == null) {
				publicLine = new PublicLine(l.getNumber());
				publicLines.put(l.getNumber(), publicLine);
			}

			Line line = new Line(l.getId(), l.getObjectId(), l.getName());
			publicLine.getLines().add(line);

			for (mobi.chouette.model.Timetable t : lat.getTimetables()) {
				Timetable timetable = new Timetable(t.getId(),t.getObjectId());

				line.getTimetables().add(timetable);

				// TODO work must be done here to include all periods, including
				// separate dates (inclusion and exclusion)
				if (t.getPeriods() != null && t.getPeriods().size() > 0) {
					// Use periods
					for (mobi.chouette.model.Period p : t.getPeriods()) {
						Period period = new Period(p.getStartDate(),p.getEndDate());
						if (!period.isEmpty()) {
//							log.info("Adding normal period " + p);
							timetable.getPeriods().add(period);
						}
					}
				} else {
					// Use timetable from/to as period
					t.computeLimitOfPeriods();
					Period period = new Period(t.getStartOfPeriod(),t.getEndOfPeriod());

					// TODO could be separate days here as well that should be included
					if (!period.isEmpty()) {
//						log.info("Adding timetable period " + period);
						timetable.getPeriods().add(period);
					} else {
						log.warn("No from/to in timetable objectId=" + t.getObjectId() + " id=" + t.getId());
					}

				}

			}
		}

		for (PublicLine pl : publicLines.values()) {
			Set<Period> uniquePeriods = new TreeSet<Period>();
			for (Line l : pl.getLines()) {
				for (Timetable t : l.getTimetables()) {
					t.setPeriods(mergeOverlappingPeriods(t.getPeriods()));
					uniquePeriods.addAll(t.getPeriods());
				}
			}

			pl.getEffectivePeriods().addAll(uniquePeriods);
			pl.setEffectivePeriods(mergeOverlappingPeriods(pl.getEffectivePeriods()));

		}

		LineStatistics lineStats = new LineStatistics();
		List<PublicLine> pL = new ArrayList<PublicLine>(publicLines.values());
		Collections.sort(pL);
		lineStats.setPublicLines(pL);

		log.info("Everything took " + (System.currentTimeMillis() - methodStart) + "ms");

		return lineStats;
	}

	public List<Period> mergeOverlappingPeriods(List<Period> periods) {
		return merge(periods);
	}

	

	public List<Period> merge(List<Period> intervals) {

		if (intervals.size() == 0 || intervals.size() == 1)
			return intervals;

		Collections.sort(intervals, new Comparator<Period>() {

			@Override
			public int compare(Period o1, Period o2) {
				return o1.getFrom().compareTo(o2.getFrom());
			}
		});

		Period first = intervals.get(0);
		Date start = first.getFrom();
		Date end = first.getTo();

		List<Period> result = new ArrayList<Period>();

		for (int i = 1; i < intervals.size(); i++) {
			Period current = intervals.get(i);
			if (!current.getFrom().after(end)) {
				end = current.getTo().before(end) ? end : current.getTo();
			} else {
				result.add(new Period(start, end));
				start = current.getFrom();
				end = current.getTo();
			}
		}

		result.add(new Period(start, end));
		return result;
	}

}
