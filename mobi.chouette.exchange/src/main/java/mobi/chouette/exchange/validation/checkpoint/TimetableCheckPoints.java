package mobi.chouette.exchange.validation.checkpoint;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Timetable;

import java.util.ArrayList;
import java.util.List;

@Log4j
public class TimetableCheckPoints extends AbstractValidation<Timetable> implements Validator<Timetable> {


    @Override
    public void validate(Context context, Timetable target) {
        ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
        List<Timetable> timetables = new ArrayList<>(data.getTimetables());
        ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
        if (isEmpty(timetables))
            return;
        boolean test4_1 = (parameters.getCheckTimetable() != 0);
        if (test4_1) {
            initCheckPoint(context, L4_TIME_TABLE_1, SEVERITY.E);
            prepareCheckPoint(context, L4_TIME_TABLE_1);
            for (int i = 0; i < timetables.size(); i++) {
                Timetable bean = timetables.get(i);

                // 4-Timetable-1 : check columns constraints
                if (test4_1)
                    check4Generic1(context, bean, L4_TIME_TABLE_1, parameters, log);
            }
        }

        // Check for timetables without active dates
        initCheckPoint(context, L4_TIME_TABLE_2, SEVERITY.W);
        prepareCheckPoint(context, L4_TIME_TABLE_2);
        timetables.forEach(timetable -> validateInactiveTimetable(context, timetable));

    }

    private void validateInactiveTimetable(Context context, Timetable timetable) {
        if (timetable != null && timetable.getActiveDates().isEmpty()) {
            ValidationReporter reporter = ValidationReporter.Factory.getInstance();
            DataLocation locationTimetable = buildLocation(context, timetable);
            reporter.addCheckPointReportError(context, L4_TIME_TABLE_2, locationTimetable);
            if(log.isDebugEnabled()) {
            		log.debug("Empty timetable detected: "  + timetable.getObjectId());
			}
        }

    }

}
