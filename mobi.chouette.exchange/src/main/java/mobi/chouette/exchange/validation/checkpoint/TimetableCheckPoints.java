package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.model.Timetable;

@Log4j
public class TimetableCheckPoints extends AbstractValidation<Timetable> implements Validator<Timetable> {

	@Override
	public void validate(Context context, Timetable target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<Timetable> beans = new ArrayList<>(data.getTimetables());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		if (isEmpty(beans))
			return ;
		boolean test4_1 = (parameters.getCheckTimetable() != 0);
		if (test4_1) {
			initCheckPoint(context, L4_TIME_TABLE_1, SEVERITY.E);
			prepareCheckPoint(context, L4_TIME_TABLE_1);
		} else // no other tests for this object
		{
			return ;
		}
		for (int i = 0; i < beans.size(); i++) {
			Timetable bean = beans.get(i);

			// 4-Timetable-1 : check columns constraints
			if (test4_1)
				check4Generic1(context, bean, L4_TIME_TABLE_1, parameters, log);

		}
		return ;
	}

}
