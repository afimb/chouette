package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.StopPoint;

@Log4j
public class JourneyPatternCheckPoints extends AbstractValidation<JourneyPattern> implements Validator<JourneyPattern> {
	@Setter
	private VehicleJourneyCheckPoints vehicleJourneyCheckPoints;

	@Override
	public void validate(Context context, JourneyPattern target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<JourneyPattern> beans = new ArrayList<>(data.getJourneyPatterns());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		if (isEmpty(beans))
			return;
		// init checkPoints : add here all defined check points for this kind of
		// object

		initCheckPoint(context, JOURNEY_PATTERN_1, SEVERITY.W);
		// initCheckPoint(context, JOURNEY_PATTERN_2, SEVERITY.E);
		// 3-JourneyPattern-1 : check if two journey patterns use same stops
		// 3-JourneyPattern-2 : Check if journey section routes count equals to
		// journey stops count minus 1 

		boolean test4_1 = (parameters.getCheckJourneyPattern() != 0);
		if (test4_1) {
			initCheckPoint(context, L4_JOURNEY_PATTERN_1, SEVERITY.E);
			prepareCheckPoint(context, L4_JOURNEY_PATTERN_1);
		}

		// checkPoint is applicable
		for (int i = 0; i < beans.size(); i++) {
			JourneyPattern jp = beans.get(i);

			// 3-JourneyPattern-1 : check if two journey patterns use same stops
			check3JourneyPattern1(context, beans, i, jp);

			// 3-JourneyPattern-2 : Check if journey section route count equals
			// to journey stops count minus 1
			// check3JourneyPattern2(context, jp);

			// 4-JourneyPattern-1 : check columns constraints
			if (test4_1)
				check4Generic1(context, jp, L4_JOURNEY_PATTERN_1, parameters, log);

		}
		return;

	}

	private void check3JourneyPattern1(Context context, List<JourneyPattern> beans, int jpRank, JourneyPattern jp) {
		// 3-JourneyPattern-1 : check if two journey patterns use same stops
		if (beans.size() <= 1)
			return;
		prepareCheckPoint(context, JOURNEY_PATTERN_1);
		int pointCount = jp.getStopPoints().size();
		List<StopPoint> sp1 = new ArrayList<>(jp.getStopPoints());
		List<StopPoint> sp2 = new ArrayList<>();
		for (int j = jpRank + 1; j < beans.size(); j++) {
			JourneyPattern jp2 = beans.get(j);
			sp2.clear();
			sp2.addAll(jp2.getStopPoints());
			if (sp1.equals(sp2)) {
				DataLocation location = buildLocation(context, jp);
				DataLocation targetLocation = buildLocation(context, jp2);

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, JOURNEY_PATTERN_1, location, Integer.toString(pointCount),
						null, targetLocation);
			}
		}

	}

	// 3-JourneyPattern-2 : Check if journey section route count equals to
	// journey stops count minus 1
	private void check3JourneyPattern2(Context context, JourneyPattern jp) {
		int routeSectionCount = jp.getRouteSections().size();
		if (routeSectionCount > 0) {
			prepareCheckPoint(context, JOURNEY_PATTERN_2);
			// If journey section route count not equals to journey stops count
			// minus 1
			if (routeSectionCount != jp.getStopPoints().size() - 1) {
				DataLocation location = buildLocation(context, jp);
				DataLocation targetLocation = buildLocation(context, jp);

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, JOURNEY_PATTERN_2, location,
						Integer.toString(routeSectionCount), Integer.toString(jp.getStopPoints().size() - 1),
						targetLocation);
			}

		}
	}

}
