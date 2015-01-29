package mobi.chouette.exchange.importer.updater;

import java.util.Collection;
import java.util.Comparator;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.CollectionUtils;
import mobi.chouette.common.Context;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;

@Log4j
public class TimetableUpdater implements Updater<Timetable> {

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
				&& newValue.getObjectId().compareTo(oldValue.getObjectId()) != 0) {
			oldValue.setObjectId(newValue.getObjectId());
		}
		if (newValue.getObjectVersion() != null
				&& newValue.getObjectVersion().compareTo(
						oldValue.getObjectVersion()) != 0) {
			oldValue.setObjectVersion(newValue.getObjectVersion());
		}
		if (newValue.getCreationTime() != null
				&& newValue.getCreationTime().compareTo(
						oldValue.getCreationTime()) != 0) {
			oldValue.setCreationTime(newValue.getCreationTime());
		}
		if (newValue.getCreatorId() != null
				&& newValue.getCreatorId().compareTo(oldValue.getCreatorId()) != 0) {
			oldValue.setCreatorId(newValue.getCreatorId());
		}
		if (newValue.getName() != null
				&& newValue.getName().compareTo(oldValue.getName()) != 0) {
			oldValue.setName(newValue.getName());
		}
		if (newValue.getComment() != null
				&& newValue.getComment().compareTo(oldValue.getComment()) != 0) {
			oldValue.setComment(newValue.getComment());
		}

		if (newValue.getVersion() != null
				&& newValue.getVersion().compareTo(oldValue.getVersion()) != 0) {
			oldValue.setVersion(newValue.getVersion());
		}
		if (newValue.getIntDayTypes() != null
				&& newValue.getIntDayTypes().compareTo(
						oldValue.getIntDayTypes()) != 0) {
			oldValue.setIntDayTypes(newValue.getIntDayTypes());
		}

		if (newValue.getStartOfPeriod() != null
				&& newValue.getStartOfPeriod().compareTo(
						oldValue.getStartOfPeriod()) != 0) {
			oldValue.setStartOfPeriod(newValue.getStartOfPeriod());
		}
		if (newValue.getEndOfPeriod() != null
				&& newValue.getEndOfPeriod().compareTo(
						oldValue.getEndOfPeriod()) != 0) {
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
		UpdaterFactory.register(TimetableUpdater.class.getName(),
				new UpdaterFactory() {
					private TimetableUpdater INSTANCE = new TimetableUpdater();

					@Override
					protected Updater<Timetable> create() {
						return INSTANCE;
					}
				});
	}

}
