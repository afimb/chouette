package mobi.chouette.exchange.importer.updater;

import java.util.Collection;
import java.util.Comparator;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.CollectionUtils;
import mobi.chouette.common.Context;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;

@Log4j
@Stateless(name=TimetableUpdater.BEAN_NAME)
public class TimetableUpdater implements Updater<Timetable> {

	public static final String BEAN_NAME = "TimetableUpdater";

	
	private static final Comparator<Period> PERIOD_COMPARATOR = new Comparator<Period>() {
		@Override
		public int compare(Period left, Period right) {
			return left.equals(right) ? 0 : 1;
		}
	};

	private static final Comparator<CalendarDay> CALENDAR_DAY_COMPARATOR = new Comparator<CalendarDay>() {
		@Override
		public int compare(CalendarDay left, CalendarDay right) {
			return left.equals(right) ? 0 : 1;
		}
	};

	@Override
	public void update(Context context, Timetable oldValue, Timetable newValue)
			throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		if (newValue.getObjectId() != null
				&& !newValue.getObjectId().equals(oldValue.getObjectId())) {
			oldValue.setObjectId(newValue.getObjectId());
		}
		if (newValue.getObjectVersion() != null
				&& !newValue.getObjectVersion().equals(
						oldValue.getObjectVersion())) {
			oldValue.setObjectVersion(newValue.getObjectVersion());
		}
		if (newValue.getCreationTime() != null
				&& !newValue.getCreationTime().equals(
						oldValue.getCreationTime())) {
			oldValue.setCreationTime(newValue.getCreationTime());
		}
		if (newValue.getCreatorId() != null
				&& !newValue.getCreatorId().equals(oldValue.getCreatorId())) {
			oldValue.setCreatorId(newValue.getCreatorId());
		}
		if (newValue.getName() != null
				&& !newValue.getName().equals(oldValue.getName())) {
			oldValue.setName(newValue.getName());
		}
		if (newValue.getComment() != null
				&& !newValue.getComment().equals(oldValue.getComment())) {
			oldValue.setComment(newValue.getComment());
		}

		if (newValue.getVersion() != null
				&& !newValue.getVersion().equals(oldValue.getVersion())) {
			oldValue.setVersion(newValue.getVersion());
		}
		if (newValue.getIntDayTypes() != null
				&& !newValue.getIntDayTypes().equals(oldValue.getIntDayTypes())) {
			oldValue.setIntDayTypes(newValue.getIntDayTypes());
		}

		if (newValue.getStartOfPeriod() != null
				&& !newValue.getStartOfPeriod().equals(
						oldValue.getStartOfPeriod())) {
			oldValue.setStartOfPeriod(newValue.getStartOfPeriod());
		}
		if (newValue.getEndOfPeriod() != null
				&& !newValue.getEndOfPeriod().equals(oldValue.getEndOfPeriod())) {
			oldValue.setEndOfPeriod(newValue.getEndOfPeriod());
		}

		// Period
		Collection<Period> addedPeriod = CollectionUtils
				.substract(newValue.getPeriods(), oldValue.getPeriods(),
						PERIOD_COMPARATOR);
		for (Period item : addedPeriod) {
			oldValue.getPeriods().add(item);
		}

		Collection<Period> removedPeriod = CollectionUtils
				.substract(oldValue.getPeriods(), newValue.getPeriods(),
						PERIOD_COMPARATOR);
		for (Period item : removedPeriod) {
			oldValue.getPeriods().remove(item);
		}

		// Calendar Days
		Collection<CalendarDay> addedCalendarDays = CollectionUtils.substract(
				newValue.getCalendarDays(), oldValue.getCalendarDays(),
				CALENDAR_DAY_COMPARATOR);
		for (CalendarDay item : addedCalendarDays) {
			oldValue.getCalendarDays().add(item);
		}

		Collection<CalendarDay> removedCalendarDays = CollectionUtils
				.substract(oldValue.getCalendarDays(),
						newValue.getCalendarDays(), CALENDAR_DAY_COMPARATOR);
		for (CalendarDay item : removedCalendarDays) {
			oldValue.getCalendarDays().remove(item);
		}

	}

	static {
		UpdaterFactory.register(LineUpdater.class.getName(),
				new UpdaterFactory() {

					@Override
					protected <T> Updater<T> create(InitialContext context) {
						Updater result = null;
						try {
							result = (Updater) context
									.lookup("java:app/mobi.chouette.exchange/"
											+ BEAN_NAME);
						} catch (NamingException e) {
							log.error(e);
						}
						return result;
					}
				});
	}

}
