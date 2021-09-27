package mobi.chouette.exchange.validation.checkpoint;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.DatedServiceJourney;
import mobi.chouette.model.VehicleJourney;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j
public class DatedServiceJourneyCheckPoints extends AbstractValidation<DatedServiceJourney> implements Validator<DatedServiceJourney> {

    @Override
    public void validate(Context context, DatedServiceJourney target) throws ValidationException {
        ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
        List<VehicleJourney> vehicleJourneys = new ArrayList<>(data.getVehicleJourneys());
        List<DatedServiceJourney> datedServiceJourneys = vehicleJourneys.stream().map(VehicleJourney::getDatedServiceJourneys).flatMap(Collection::stream).collect(Collectors.toList());
        if (datedServiceJourneys.isEmpty()) {
            return;
        }

        initCheckPoint(context, DATED_SERVICE_JOURNEY_1, SEVERITY.E);
        prepareCheckPoint(context, DATED_SERVICE_JOURNEY_1);

        ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
        datedServiceJourneys.forEach(datedServiceJourney -> check3DatedServiceJourney1(context, datedServiceJourney, parameters));
    }

    private void check3DatedServiceJourney1(Context context, DatedServiceJourney dsj, ValidationParameters parameters) {
        Optional<DatedServiceJourney> referredDsjWithMismatchedOperatingDay = dsj.getOriginalDatedServiceJourneys()
                .stream()
                .filter(datedServiceJourney -> !datedServiceJourney.getOperatingDay().equals(dsj.getOperatingDay()))
                .findFirst();
        if (referredDsjWithMismatchedOperatingDay.isPresent()) {
            DataLocation location = buildLocation(context, dsj);
            DataLocation target = buildLocation(context, referredDsjWithMismatchedOperatingDay.get());
            ValidationReporter reporter = ValidationReporter.Factory.getInstance();
            reporter.addCheckPointReportError(context, DATED_SERVICE_JOURNEY_1, location, null, null, target);
        }
    }
}
