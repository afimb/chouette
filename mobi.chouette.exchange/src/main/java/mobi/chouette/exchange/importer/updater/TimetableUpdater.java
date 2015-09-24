package mobi.chouette.exchange.importer.updater;

import java.util.Collection;
import java.util.Comparator;

import javax.ejb.Stateless;

import mobi.chouette.common.CollectionUtil;
import mobi.chouette.common.Context;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.NamingUtil;

@Stateless(name = TimetableUpdater.BEAN_NAME)
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
		
		newValue.computeLimitOfPeriods();
		// default processings
		if (newValue.getComment() == null) {
			NamingUtil.setDefaultName(newValue);
		}
		
		if (newValue.getDayTypes().contains(DayTypeEnum.WeekDay))
		{
			newValue.addDayType(DayTypeEnum.Monday);
			newValue.addDayType(DayTypeEnum.Tuesday);
			newValue.addDayType(DayTypeEnum.Wednesday);
			newValue.addDayType(DayTypeEnum.Thursday);
			newValue.addDayType(DayTypeEnum.Friday);
			newValue.removeDayType(DayTypeEnum.WeekDay);
		}
		if (newValue.getDayTypes().contains(DayTypeEnum.WeekEnd))
		{
			newValue.addDayType(DayTypeEnum.Saturday);
			newValue.addDayType(DayTypeEnum.Sunday);
			newValue.removeDayType(DayTypeEnum.WeekEnd);
		}

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
		Collection<Period> addedPeriod = CollectionUtil
				.substract(newValue.getPeriods(), oldValue.getPeriods(),
						PERIOD_COMPARATOR);
		for (Period item : addedPeriod) {
			oldValue.getPeriods().add(item);
		}

		Collection<Period> removedPeriod = CollectionUtil
				.substract(oldValue.getPeriods(), newValue.getPeriods(),
						PERIOD_COMPARATOR);
		for (Period item : removedPeriod) {
			oldValue.getPeriods().remove(item);
		}

		// Calendar Days
		Collection<CalendarDay> addedCalendarDays = CollectionUtil.substract(
				newValue.getCalendarDays(), oldValue.getCalendarDays(),
				CALENDAR_DAY_COMPARATOR);
		for (CalendarDay item : addedCalendarDays) {
			oldValue.getCalendarDays().add(item);
		}

		Collection<CalendarDay> removedCalendarDays = CollectionUtil
				.substract(oldValue.getCalendarDays(),
						newValue.getCalendarDays(), CALENDAR_DAY_COMPARATOR);
		for (CalendarDay item : removedCalendarDays) {
			oldValue.getCalendarDays().remove(item);
		}

	}
}
