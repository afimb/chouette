package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.List;

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
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.StopArea;

@Log4j
public class AccessPointCheckPoints extends AbstractValidation<AccessPoint> implements Validator<AccessPoint> {

	@Override
	public ValidationConstraints validate(Context context, AccessPoint target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<AccessPoint> beans = new ArrayList<>(data.getAccessPoints());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		if (isEmpty(beans))
			return null;
		// init checkPoints : add here all defined check points for this kind of
		// object
		// 3-AccessPoint-1 : check if all access points have geolocalization
		// 3-AccessPoint-2 : check distance of access points with different name
		// 3-AccessPoint-3 : check distance with parents
		initCheckPoint(report, ACCESS_POINT_1, CheckPoint.SEVERITY.ERROR);
		initCheckPoint(report, ACCESS_POINT_2, CheckPoint.SEVERITY.WARNING);
		initCheckPoint(report, ACCESS_POINT_3, CheckPoint.SEVERITY.WARNING);
		prepareCheckPoint(report, ACCESS_POINT_1);
		prepareCheckPoint(report, ACCESS_POINT_2);
		prepareCheckPoint(report, ACCESS_POINT_3);
		boolean test4_1 = (parameters.getCheckAccessPoint() != 0);
		if (test4_1) {
			initCheckPoint(report, L4_ACCESS_POINT_1, CheckPoint.SEVERITY.ERROR);
			prepareCheckPoint(report, L4_ACCESS_POINT_1);
		}

		for (int i = 0; i < beans.size(); i++) {
			AccessPoint accessPoint = beans.get(i);
			check3AccessPoint1(context,report, accessPoint);
			check3AccessPoint3(context,report, accessPoint, parameters);
			// 4-AccessPoint-1 : check columns constraints
			if (test4_1)
				check4Generic1(context,report, accessPoint, L4_ACCESS_POINT_1, parameters, log);
			for (int j = i + 1; j < beans.size(); j++) {
				check3AccessPoint2(context,report, i, accessPoint, j, beans.get(j), parameters);
			}

		}
		return null;
	}

	private void check3AccessPoint1(Context context, ValidationReport report, AccessPoint accessPoint) {
		// 3-AccessPoint-1 : check if all access points have geolocalization

		if (!accessPoint.hasCoordinates()) {
			Location location = buildLocation(context,accessPoint);
			Location containedLocation = buildLocation(context,accessPoint.getContainedIn());
			Detail detail = new Detail(ACCESS_POINT_1, location, containedLocation);
			addValidationError(report, ACCESS_POINT_1, detail);
		}
	}

	private void check3AccessPoint2(Context context, ValidationReport report, int i, AccessPoint accessPoint, int j,
			AccessPoint accessPoint2, ValidationParameters parameters) {
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
			Location location = buildLocation(context,accessPoint);
			Location targetLocation = buildLocation(context,accessPoint2);
			Detail detail = new Detail(ACCESS_POINT_2, location, Integer.toString((int) distance),
					Integer.toString((int) distanceMin), targetLocation);
			addValidationError(report, ACCESS_POINT_2, detail);
		}

	}

	private void check3AccessPoint3(Context context, ValidationReport report, AccessPoint accessPoint, ValidationParameters parameters) {
		// 3-AccessPoint-3 : check distance with parents
		if (!accessPoint.hasCoordinates())
			return;
		long distanceMax = parameters.getParentStopAreaDistanceMax();
		StopArea stopArea = accessPoint.getContainedIn();
		if (!stopArea.hasCoordinates())
			return;
		double distance = distance(accessPoint, stopArea);
		if (distance > distanceMax) {
			Location location = buildLocation(context,accessPoint);
			Location targetLocation = buildLocation(context,stopArea);

			Detail detail = new Detail(ACCESS_POINT_3, location, Integer.toString((int) distance),
					Integer.toString((int) distanceMax), targetLocation);
			addValidationError(report, ACCESS_POINT_3, detail);
		}

	}

}
