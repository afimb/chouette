package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.StopArea;

@Log4j
public class AccessPointCheckPoints extends AbstractValidation<AccessPoint> implements Validator<AccessPoint> {

	@Override
	public void validate(Context context, AccessPoint target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<AccessPoint> beans = new ArrayList<>(data.getAccessPoints());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		if (isEmpty(beans))
			return;
		boolean sourceFile = context.get(SOURCE).equals(SOURCE_FILE);
		// init checkPoints : add here all defined check points for this kind of
		// object
		// 3-AccessPoint-1 : check if all access points have geolocalization
		// (checked only on database)
		// 3-AccessPoint-2 : check distance of access points with different name
		// 3-AccessPoint-3 : check distance with parents
		if (!sourceFile)
		{
			initCheckPoint(context, ACCESS_POINT_1, SEVERITY.E);
			prepareCheckPoint(context, ACCESS_POINT_1);
		}
		initCheckPoint(context, ACCESS_POINT_2, SEVERITY.W);
		initCheckPoint(context, ACCESS_POINT_3, SEVERITY.W);

		prepareCheckPoint(context, ACCESS_POINT_2);
		prepareCheckPoint(context, ACCESS_POINT_3);
		boolean test4_1 = (parameters.getCheckAccessPoint() != 0);
		if (test4_1) {
			initCheckPoint(context, L4_ACCESS_POINT_1, SEVERITY.E);
			prepareCheckPoint(context, L4_ACCESS_POINT_1);
		}

		for (int i = 0; i < beans.size(); i++) {
			AccessPoint accessPoint = beans.get(i);
			if (!sourceFile)
				check3AccessPoint1(context, accessPoint);
			check3AccessPoint3(context, accessPoint, parameters);
			// 4-AccessPoint-1 : check columns constraints
			if (test4_1)
				check4Generic1(context, accessPoint, L4_ACCESS_POINT_1, parameters, log);

			// need spatial index for optimization
			for (int j = i + 1; j < beans.size(); j++) {
				check3AccessPoint2(context, i, accessPoint, j, beans.get(j), parameters);
			}

		}
		return;
	}

	private void check3AccessPoint1(Context context, AccessPoint accessPoint) {
		// 3-AccessPoint-1 : check if all access points have geolocalization

		if (!accessPoint.hasCoordinates()) {
			DataLocation location = buildLocation(context, accessPoint);
			DataLocation containedLocation = buildLocation(context, accessPoint.getContainedIn());
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, ACCESS_POINT_1, ACCESS_POINT_1, location, null, null,
					containedLocation);
		}
	}

	private void check3AccessPoint2(Context context, int i, AccessPoint accessPoint, int j, AccessPoint accessPoint2,
			ValidationParameters parameters) {
		// 3-AccessPoint-2 : check distance of access points with different name
		if (!accessPoint.hasCoordinates())
			return;
		long distanceMin = parameters.getInterAccessPointDistanceMin();
		if (!accessPoint2.hasCoordinates())
			return;
		if (accessPoint.getName().equals(accessPoint2.getName()))
			return;
		double distance = distance(accessPoint, accessPoint2);
		if (distance < distanceMin) {
			DataLocation location = buildLocation(context, accessPoint);
			DataLocation targetLocation = buildLocation(context, accessPoint2);
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, ACCESS_POINT_2, ACCESS_POINT_2, location,
					Integer.toString((int) distance), Integer.toString((int) distanceMin), targetLocation);
		}

	}

	private void check3AccessPoint3(Context context, AccessPoint accessPoint, ValidationParameters parameters) {
		// 3-AccessPoint-3 : check distance with parents
		if (!accessPoint.hasCoordinates())
			return;
		long distanceMax = parameters.getParentStopAreaDistanceMax();
		StopArea stopArea = accessPoint.getContainedIn();
		if (!stopArea.hasCoordinates())
			return;
		double distance = quickDistance(accessPoint, stopArea);
		if (distance > distanceMax) {
			DataLocation location = buildLocation(context, accessPoint);
			DataLocation targetLocation = buildLocation(context, stopArea);
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();

			reporter.addCheckPointReportError(context, ACCESS_POINT_3, ACCESS_POINT_3, location,
					Integer.toString((int) distance), Integer.toString((int) distanceMax), targetLocation);
		}

	}

}
