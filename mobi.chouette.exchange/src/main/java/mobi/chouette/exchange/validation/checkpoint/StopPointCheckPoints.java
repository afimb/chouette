package mobi.chouette.exchange.validation.checkpoint;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.DeadRunAtStop;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.type.ChouetteAreaEnum;

@Log4j
public class StopPointCheckPoints extends AbstractValidation<StopPoint> implements Validator<StopPoint> {

    @Override
    public void validate(Context context, StopPoint target) throws ValidationException {


        ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
        

        if (data.getStopPoints().isEmpty()) {
            return;
        }
        initCheckPoint(context, STOP_POINT_1, SEVERITY.E);
        initCheckPoint(context, STOP_POINT_2, SEVERITY.E);
        initCheckPoint(context, STOP_POINT_3, SEVERITY.W);
        prepareCheckPoint(context, STOP_POINT_1);
        prepareCheckPoint(context, STOP_POINT_2);
        prepareCheckPoint(context, STOP_POINT_3);

        data.getStopPoints().forEach(stopPoint -> validateStopPointIsContainedInStopArea(context, stopPoint));
        data.getStopPoints().forEach(stopPoint -> validateStopPointIsBoardingPosition(context, stopPoint));
        data.getStopPoints().forEach(stopPoint -> validateStopPointBoardingAlighting(context, stopPoint));


    }

    private void validateStopPointIsContainedInStopArea(Context context, StopPoint stopPoint) {
        if (stopPoint != null && stopPoint.getScheduledStopPoint() != null && stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObject() == null && !isDeadRunStopPoint(context, stopPoint)) {
            log.info("Registering error for : " + stopPoint);
            ValidationReporter reporter = ValidationReporter.Factory.getInstance();
            DataLocation locationStopPoint = buildLocation(context, stopPoint);
            DataLocation locationRoute = buildLocation(context, stopPoint.getRoute());
           
            reporter.addCheckPointReportError(context, STOP_POINT_1, locationStopPoint,stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObjectId(),null,locationRoute);
        }
    }

    /**
     * Return true if the stop point is used in at least one DeadRun and no VehicleJourney
     * @param context
     * @param currentStopPoint
     * @return
     */
    private boolean isDeadRunStopPoint(Context context, StopPoint currentStopPoint) {
        ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
        boolean isStopPointUsedInDeadRuns = data.getJourneyPatterns()
                .stream()
                .flatMap(journeyPattern -> journeyPattern.getDeadRuns().stream())
                .flatMap(deadRun -> deadRun.getDeadRunAtStops().stream())
                .map(DeadRunAtStop::getStopPoint).anyMatch(stopPoint -> stopPoint.getId().equals(currentStopPoint.getId()));
        if (!isStopPointUsedInDeadRuns) {
            return false;
        } else {
            boolean isStopPointUsedInVehicleJourney = data.getJourneyPatterns()
                    .stream()
                    .flatMap(journeyPattern -> journeyPattern.getVehicleJourneys().stream())
                    .flatMap(vehicleJourney -> vehicleJourney.getVehicleJourneyAtStops().stream())
                    .map(VehicleJourneyAtStop::getStopPoint).anyMatch(stopPoint -> stopPoint.getId().equals(currentStopPoint.getId()));
            return !isStopPointUsedInVehicleJourney;
        }
    }

    private void validateStopPointIsBoardingPosition(Context context, StopPoint stopPoint) {
        if (stopPoint != null && stopPoint.getScheduledStopPoint() != null && stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObject() != null && stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObject() .getAreaType() != ChouetteAreaEnum.BoardingPosition) {
            ValidationReporter reporter = ValidationReporter.Factory.getInstance();
            DataLocation locationStopPoint = buildLocation(context, stopPoint);
            DataLocation locationStopArea = buildLocation(context, stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObject() );
           
            reporter.addCheckPointReportError(context, STOP_POINT_2, locationStopPoint,stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObjectId(),null,locationStopArea);
        }
    }


    private void validateStopPointBoardingAlighting(Context context, StopPoint stopPoint) {
        if (stopPoint != null && stopPoint.getScheduledStopPoint() != null && stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObject() != null && BoardingPossibilityEnum.forbidden.equals(stopPoint.getForBoarding()) && AlightingPossibilityEnum.forbidden.equals(stopPoint.getForAlighting())) {
            ValidationReporter reporter = ValidationReporter.Factory.getInstance();
            DataLocation locationStopPoint = buildLocation(context, stopPoint);
            DataLocation locationRoute = buildLocation(context, stopPoint.getRoute());
            
            DataLocation locationStopArea = buildLocation(context, stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObject() );
            reporter.addCheckPointReportError(context, STOP_POINT_3, locationStopPoint,null,null,locationStopArea,locationRoute);
        }
    }
}
