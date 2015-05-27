package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

@Log4j
public class StopAreaCheckPoints extends AbstractValidation<StopArea> implements Validator<StopArea> {

	@Override
	public ValidationConstraints validate(Context context, StopArea target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<StopArea> beans = new ArrayList<>(data.getStopAreas());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		if (isEmpty(beans))
			return null;
		// init checkPoints : add here all defined check points for this kind of
		// object
		// 3-StopArea-1 : check if all non ITL stopArea has geolocalization
		// 3-StopArea-2 : check distance of stop areas with different name
		// 3-StopArea-3 : check multiple occurrence of a stopArea
		// 3-StopArea-4 : check localization in a region
		// 3-StopArea-5 : check distance with parent
		initCheckPoint(report, STOP_AREA_1, CheckPoint.SEVERITY.ERROR);
		initCheckPoint(report, STOP_AREA_2, CheckPoint.SEVERITY.WARNING);
		initCheckPoint(report, STOP_AREA_3, CheckPoint.SEVERITY.WARNING);
		initCheckPoint(report, STOP_AREA_4, CheckPoint.SEVERITY.WARNING);
		initCheckPoint(report, STOP_AREA_5, CheckPoint.SEVERITY.WARNING);
		prepareCheckPoint(report, STOP_AREA_1);
		prepareCheckPoint(report, STOP_AREA_2);
		prepareCheckPoint(report, STOP_AREA_3);
		prepareCheckPoint(report, STOP_AREA_5);

		boolean test4_1 = (parameters.getCheckStopArea() != 0);
		if (test4_1) {
			initCheckPoint(report, L4_STOP_AREA_1, CheckPoint.SEVERITY.ERROR);
			prepareCheckPoint(report, L4_STOP_AREA_1);
		}
		boolean test4_2 = parameters.getCheckStopParent() == 1;
		if (test4_2) {
			initCheckPoint(report, L4_STOP_AREA_2, CheckPoint.SEVERITY.ERROR);
			prepareCheckPoint(report, L4_STOP_AREA_2);
		}

		Polygon enveloppe = null;
		try {
			enveloppe = getEnveloppe(parameters);
			prepareCheckPoint(report, STOP_AREA_4);
			
		} catch (Exception e) {
			log.error("cannot decode enveloppe "+parameters.getStopAreasArea());
		}

		for (int i = 0; i < beans.size(); i++) {
			StopArea stopArea = beans.get(i);
			// no test for ITL
			if (stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
				continue;
			check3StopArea1(context,report, stopArea);
			check3StopArea4(context,report, stopArea, enveloppe);
			check3StopArea5(context,report, stopArea, parameters);
			// 4-StopArea-1 : check columns constraints
			if (test4_1)
				check4Generic1(context, report, stopArea, L4_STOP_AREA_1, parameters, log);
			// 4-StopArea-2 : check parent
			if (test4_2)
				check4StopArea2(context,report, stopArea);

			for (int j = i + 1; j < beans.size(); j++) {
				check3StopArea2(context,report, i, stopArea, j, beans.get(j), parameters);
				check3StopArea3(context,report, i, stopArea, j, beans.get(j));
			}

		}
		return null;
	}

	private void check3StopArea1(Context context, ValidationReport report, StopArea stopArea) {
		// 3-StopArea-1 : check if all non ITL stopArea has geolocalization
		if (!stopArea.hasCoordinates()) {
			Location location = buildLocation(context,stopArea);

			Detail detail = new Detail(STOP_AREA_1, location);
			addValidationError(report, STOP_AREA_1, detail);
		}
	}

	private void check3StopArea2(Context context, ValidationReport report, int rank, StopArea stopArea, int rank2, StopArea stopArea2,
			ValidationParameters parameters) {
		// 3-StopArea-2 : check distance of stop areas with different name
		if (!stopArea.hasCoordinates())
			return;
		long distanceMin = parameters.getInterStopAreaDistanceMin();
		ChouetteAreaEnum type = stopArea.getAreaType();
		if (type.equals(ChouetteAreaEnum.BoardingPosition) || type.equals(ChouetteAreaEnum.Quay)) {
			if (!stopArea2.getAreaType().equals(type))
				return;
			if (!stopArea2.hasCoordinates())
				return;
			if (stopArea.getName().equals(stopArea2.getName()))
				return;
			double distance = distance(stopArea, stopArea2);
			if (distance < distanceMin) {
				Location source = buildLocation(context,stopArea);
				Location target = buildLocation(context,stopArea2);

				Detail detail = new Detail(STOP_AREA_2, source, Integer.toString((int) distance),
						Integer.toString((int) distanceMin), target);
				addValidationError(report, STOP_AREA_2, detail);
			}

		}

	}

	private void check3StopArea3(Context context, ValidationReport report, int rank, StopArea stopArea, int rank2, StopArea stopArea2) {
		// 3-StopArea-3 : check multiple occurrence of a stopArea of same type
		if (!stopArea2.getAreaType().equals(stopArea.getAreaType()))
			return;
		// same name; same code; same address ...
		if (!stopArea.getName().equals(stopArea2.getName()))
			return;
		if (stopArea.getStreetName() != null && !stopArea.getStreetName().equals(stopArea2.getStreetName()))
			return;
		if (stopArea.getCountryCode() != null && !stopArea.getCountryCode().equals(stopArea2.getCountryCode()))
			return;
		Collection<String> lines = getLines(context,stopArea);
		Collection<String> lines2 = getLines(context,stopArea2);
		if (lines.containsAll(lines2) && lines2.containsAll(lines)) {
			Location source = buildLocation(context,stopArea);
			Location target = buildLocation(context,stopArea2);

			Detail detail = new Detail(STOP_AREA_3, source, target);
			addValidationError(report, STOP_AREA_3, detail);
		}

	}

	private void check3StopArea4(Context context, ValidationReport report, StopArea stopArea, Polygon enveloppe) {
		// 3-StopArea-4 : check localization in a region
		if (enveloppe == null || !stopArea.hasCoordinates())
			return;
		Point p = buildPoint(stopArea);
		if (!enveloppe.contains(p)) {
			Location location = buildLocation(context,stopArea);

			Detail detail = new Detail(STOP_AREA_4, location);
			addValidationError(report, STOP_AREA_4, detail);
		}

	}

	private void check3StopArea5(Context context, ValidationReport report, StopArea stopArea, ValidationParameters parameters) {
		// 3-StopArea-5 : check distance with parents
		if (!stopArea.hasCoordinates())
			return;
		long distanceMax = parameters.getParentStopAreaDistanceMax();
		StopArea stopArea2 = stopArea.getParent();
		if (stopArea2 == null)
			return; // no parent
		if (!stopArea2.hasCoordinates())
			return;
		double distance = distance(stopArea, stopArea2);
		if (distance > distanceMax) {
			Location source = buildLocation(context,stopArea);
			Location target = buildLocation(context,stopArea2);

			Detail detail = new Detail(STOP_AREA_5, source, Integer.toString((int) distance),
					Integer.toString((int) distanceMax), target);
			addValidationError(report, STOP_AREA_5, detail);
		}
	}

	private void check4StopArea2(Context context, ValidationReport report, StopArea stopArea) {
		// 4-StopArea-2 : check if all physical stopArea has parent
		if (stopArea.getAreaType().equals(ChouetteAreaEnum.BoardingPosition)
				|| stopArea.getAreaType().equals(ChouetteAreaEnum.Quay)) {
			if (stopArea.getParent() == null) {
				Location location = buildLocation(context,stopArea);

				Detail detail = new Detail(L4_STOP_AREA_2, location);
				addValidationError(report, L4_STOP_AREA_2, detail);
			}
		}
	}

	private Collection<String> getLines(Context context, StopArea area ) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Set<String> lines = data.getLinesOfStopAreas().get(area.getObjectId());
		if (lines == null) lines = new HashSet<>();
		return lines;
		
	}

}
