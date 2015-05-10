package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.StopPoint;

@Log4j
public class JourneyPatternCheckPoints extends AbstractValidation<JourneyPattern> implements Validator<JourneyPattern> {
	@Setter
	private VehicleJourneyCheckPoints vehicleJourneyCheckPoints;

	@Override
	public ValidationConstraints validate(Context context, JourneyPattern target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<JourneyPattern> beans = new ArrayList<>(data.getJourneyPatterns());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		if (isEmpty(beans))
			return null;
		// init checkPoints : add here all defined check points for this kind of
		// object

		initCheckPoint(report, JOURNEY_PATTERN_1, CheckPoint.SEVERITY.WARNING);
		boolean test4_1 = (parameters.getCheckJourneyPattern() != 0);
		if (test4_1) {
			initCheckPoint(report, L4_JOURNEY_PATTERN_1, CheckPoint.SEVERITY.ERROR);
			prepareCheckPoint(report, L4_JOURNEY_PATTERN_1);
		}

		// checkPoint is applicable
		for (int i = 0; i < beans.size(); i++) {
			JourneyPattern jp = beans.get(i);

			// 3-JourneyPattern-1 : check if two journey patterns use same stops
			check3JourneyPattern1(context,report, beans, i, jp);

			// 4-JourneyPattern-1 : check columns constraints
			if (test4_1)
				check4Generic1(context,report, jp, L4_JOURNEY_PATTERN_1, parameters, log);

		}
		return null;

	}

	private void check3JourneyPattern1(Context context, ValidationReport report, List<JourneyPattern> beans, int jpRank,
			JourneyPattern jp) {
		// 3-JourneyPattern-1 : check if two journey patterns use same stops
		if (beans.size() <= 1)
			return;
		prepareCheckPoint(report, JOURNEY_PATTERN_1);
		int pointCount = jp.getStopPoints().size();
		List<StopPoint> sp1 = new ArrayList<>(jp.getStopPoints());
		List<StopPoint> sp2 = new ArrayList<>();
		for (int j = jpRank + 1; j < beans.size(); j++) {
			JourneyPattern jp2 = beans.get(j);
			sp2 .clear();
			sp2.addAll(jp2.getStopPoints());
			if (sp1.equals(sp2)) {
				Location location = buildLocation(context,jp);
				Location targetLocation = buildLocation(context,jp2);

				Detail detail = new Detail(JOURNEY_PATTERN_1, location, Integer.toString(pointCount), targetLocation);
				addValidationError(report, JOURNEY_PATTERN_1, detail);
			}
		}

	}

}
