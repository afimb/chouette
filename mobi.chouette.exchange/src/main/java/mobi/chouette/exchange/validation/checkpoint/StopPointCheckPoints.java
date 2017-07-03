package mobi.chouette.exchange.validation.checkpoint;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.StopPoint;

@Log4j
public class StopPointCheckPoints extends AbstractValidation<StopPoint> implements Validator<StopPoint> {

    @Override
    public void validate(Context context, StopPoint target) throws ValidationException {


        ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
        

        if (data.getStopPoints().isEmpty()) {
            return;
        }
        initCheckPoint(context, STOP_POINT_1, SEVERITY.E);
        prepareCheckPoint(context, STOP_POINT_1);

        data.getStopPoints().forEach(stopPoint -> validateStopPointIsContainedInStopArea(context, stopPoint));


    }

    private void validateStopPointIsContainedInStopArea(Context context, StopPoint stopPoint) {
        if (stopPoint != null && stopPoint.getContainedInStopArea() == null) {
            log.info("Registering error for : " + stopPoint);
            ValidationReporter reporter = ValidationReporter.Factory.getInstance();
            DataLocation locationStopPoint = buildLocation(context, stopPoint);
            DataLocation locationRoute = buildLocation(context, stopPoint.getRoute());
           
            reporter.addCheckPointReportError(context, STOP_POINT_1, locationStopPoint,stopPoint.getContainedInStopAreaObjectId(),null,locationRoute);
        }
    }
}
