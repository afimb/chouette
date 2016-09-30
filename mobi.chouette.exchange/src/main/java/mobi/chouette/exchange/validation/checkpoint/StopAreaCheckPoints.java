package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.index.quadtree.Quadtree;

@Log4j
public class StopAreaCheckPoints extends AbstractValidation<StopArea> implements Validator<StopArea> {

	private double minLat;
	private double maxLat;
	private double minLon;
	private double maxLon;
	private Envelope searchEnv;

	private Quadtree spatialIndex;

	@Override
	public void validate(Context context, StopArea target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<StopArea> beans = new ArrayList<>(data.getStopAreas());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		if (isEmpty(beans))
			return;
		boolean sourceFile = context.get(SOURCE).equals(SOURCE_FILE);
		// init checkPoints : add here all defined check points for this kind of
		// object
		// 3-StopArea-1 : check if all non ITL stopArea has geolocalization
		// 3-StopArea-2 : check distance of stop areas with different name
		// 3-StopArea-3 : check multiple occurrence of a stopArea
		// 3-StopArea-4 : check localization in a region
		// 3-StopArea-5 : check distance with parent
		if (!sourceFile) {
			initCheckPoint(context, STOP_AREA_1, SEVERITY.E);
			prepareCheckPoint(context, STOP_AREA_1);
		}
		initCheckPoint(context, STOP_AREA_2, SEVERITY.W);
		initCheckPoint(context, STOP_AREA_3, SEVERITY.W);
		initCheckPoint(context, STOP_AREA_4, SEVERITY.W);
		initCheckPoint(context, STOP_AREA_5, SEVERITY.W);
		prepareCheckPoint(context, STOP_AREA_2);
		prepareCheckPoint(context, STOP_AREA_3);
		prepareCheckPoint(context, STOP_AREA_5);

		boolean test4_1 = (parameters.getCheckStopArea() != 0);
		if (test4_1) {
			initCheckPoint(context, L4_STOP_AREA_1, SEVERITY.E);
			prepareCheckPoint(context, L4_STOP_AREA_1);
		}
		boolean test4_2 = parameters.getCheckStopParent() == 1;
		if (test4_2) {
			initCheckPoint(context, L4_STOP_AREA_2, SEVERITY.E);
			prepareCheckPoint(context, L4_STOP_AREA_2);
		}

		Polygon enveloppe = null;
		try {
			enveloppe = getEnveloppe(parameters);
			prepareCheckPoint(context, STOP_AREA_4);

		} catch (Exception e) {
			log.error("cannot decode enveloppe " + parameters.getStopAreasArea());
		}

		// prepare quadtree index
		spatialIndex = new Quadtree();
		for (StopArea stopArea : beans) {
			ChouetteAreaEnum type = stopArea.getAreaType();
			if (type.equals(ChouetteAreaEnum.BoardingPosition) || type.equals(ChouetteAreaEnum.Quay)) {
				if (stopArea.hasCoordinates()) {

					Coordinate c = new Coordinate(stopArea.getLongitude().doubleValue(), stopArea.getLatitude()
							.doubleValue());
					spatialIndex.insert(Quadtree.ensureExtent(new Envelope(c), 0.00001), stopArea);
				}
			}
		}

		for (int i = 0; i < beans.size(); i++) {
			StopArea stopArea = beans.get(i);
			// no test for ITL
			if (stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
				continue;

			if (!sourceFile) {
				check3StopArea1(context, stopArea);
			}
			check3StopArea4(context, stopArea, enveloppe);
			check3StopArea5(context, stopArea, parameters);
			// 4-StopArea-1 : check columns constraints
			if (test4_1)
				check4Generic1(context, stopArea, L4_STOP_AREA_1, parameters, log);
			// 4-StopArea-2 : check parent
			if (test4_2)
				check4StopArea2(context, stopArea);

			check3StopArea2(context, stopArea, parameters);

			for (int j = i + 1; j < beans.size(); j++) {
				check3StopArea3(context, i, stopArea, j, beans.get(j));
			}

		}
		return;
	}

	protected void updateSquare(StopArea stopArea, ValidationParameters parameters) {

		if (!stopArea.hasCoordinates())
			return;
		long distanceMin = parameters.getInterStopAreaDistanceMin();

		double rLat = A * Math.cos(stopArea.getLatitude().doubleValue() * toRad);
		minLon = stopArea.getLongitude().doubleValue() - (distanceMin * 1.2 / rLat);
		maxLon = stopArea.getLongitude().doubleValue() + (distanceMin * 1.2 / rLat);
		minLat = stopArea.getLatitude().doubleValue() - (distanceMin * 1.2 / A);
		maxLat = stopArea.getLatitude().doubleValue() + (distanceMin * 1.2 / A);

		searchEnv = new Envelope(minLon, maxLon, minLat, maxLat);
	}

	protected boolean near(StopArea area) {
		if (area.getLongitude().doubleValue() < minLon || area.getLongitude().doubleValue() > maxLon) {
			return false;
		}
		if (area.getLatitude().doubleValue() < minLat || area.getLatitude().doubleValue() > maxLat) {
			return false;
		}
		return true;

	}

	private void check3StopArea1(Context context, StopArea stopArea) {
		// 3-StopArea-1 : check if all non ITL stopArea has geolocalization
		if (!stopArea.hasCoordinates()) {
			DataLocation location = buildLocation(context, stopArea);

			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, STOP_AREA_1, location);
		}
	}

	@SuppressWarnings("rawtypes")
	private void check3StopArea2(Context context, StopArea stopArea, ValidationParameters parameters) {
		// 3-StopArea-2 : check distance of stop areas with different name
		if (!stopArea.hasCoordinates())
			return;
		long distanceMin = parameters.getInterStopAreaDistanceMin();
		ChouetteAreaEnum type = stopArea.getAreaType();
		if (type.equals(ChouetteAreaEnum.BoardingPosition) || type.equals(ChouetteAreaEnum.Quay)) {
			updateSquare(stopArea, parameters);

			Coordinate c = new Coordinate(stopArea.getLongitude().doubleValue(), stopArea.getLatitude().doubleValue());
			spatialIndex.remove(Quadtree.ensureExtent(new Envelope(c), 0.00001), stopArea);
			List areas = spatialIndex.query(searchEnv);
			for (Object object : areas) {
				StopArea stopArea2 = (StopArea) object;
				if (stopArea2.equals(stopArea))
					continue;
				if (!stopArea2.getAreaType().equals(type))
					continue;
				if (stopArea.getName().equals(stopArea2.getName()))
					continue;
				double distance = quickDistance(stopArea, stopArea2);
				if (distance < distanceMin) {
					DataLocation source = buildLocation(context, stopArea);
					DataLocation target = buildLocation(context, stopArea2);

					ValidationReporter reporter = ValidationReporter.Factory.getInstance();
					reporter.addCheckPointReportError(context, STOP_AREA_2, source, Integer.toString((int) distance),
							Integer.toString((int) distanceMin), target);
				}

			}

		}

	}

	private void check3StopArea3(Context context, int rank, StopArea stopArea, int rank2, StopArea stopArea2) {
		// 3-StopArea-3 : check multiple occurrence of a stopArea of same type
		if (!stopArea2.getAreaType().equals(stopArea.getAreaType()))
			return;
		// same name; same code; same address ...
		if (!stopArea.getName().equals(stopArea2.getName())) {
			return;
		}
		if (stopArea.getStreetName() != null && !stopArea.getStreetName().equals(stopArea2.getStreetName())) {
			return;
		}
		if (stopArea.getCountryCode() != null && !stopArea.getCountryCode().equals(stopArea2.getCountryCode())) {
			return;
		}
		Collection<String> lines = getLines(context, stopArea);
		Collection<String> lines2 = getLines(context, stopArea2);
		if (lines.containsAll(lines2) && lines2.containsAll(lines)) {
			DataLocation source = buildLocation(context, stopArea);
			DataLocation target = buildLocation(context, stopArea2);

			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, STOP_AREA_3, source, null, null, target);
		}

	}

	private void check3StopArea4(Context context, StopArea stopArea, Polygon enveloppe) {
		// 3-StopArea-4 : check localization in a region
		if (enveloppe == null || !stopArea.hasCoordinates())
			return;
		Point p = buildPoint(stopArea);
		if (!enveloppe.contains(p)) {
			DataLocation location = buildLocation(context, stopArea);

			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, STOP_AREA_4, location);
		}

	}

	private void check3StopArea5(Context context, StopArea stopArea, ValidationParameters parameters) {
		// 3-StopArea-5 : check distance with parents
		if (!stopArea.hasCoordinates())
			return;
		long distanceMax = parameters.getParentStopAreaDistanceMax();
		StopArea stopArea2 = stopArea.getParent();
		if (stopArea2 == null)
			return; // no parent
		if (!stopArea2.hasCoordinates())
			return;
		double distance = quickDistance(stopArea, stopArea2);
		if (distance > distanceMax) {
			DataLocation source = buildLocation(context, stopArea);
			DataLocation target = buildLocation(context, stopArea2);

			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, STOP_AREA_5, source, Integer.toString((int) distance),
					Integer.toString((int) distanceMax), target);
		}
	}

	private void check4StopArea2(Context context, StopArea stopArea) {
		// 4-StopArea-2 : check if all physical stopArea has parent
		if (stopArea.getAreaType().equals(ChouetteAreaEnum.BoardingPosition)
				|| stopArea.getAreaType().equals(ChouetteAreaEnum.Quay)) {
			if (stopArea.getParent() == null) {
				DataLocation location = buildLocation(context, stopArea);

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, L4_STOP_AREA_2, location);
			}
		}
	}

	private Collection<String> getLines(Context context, StopArea area) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Set<String> lines = data.getLinesOfStopAreas().get(area.getObjectId());
		if (lines == null)
			lines = new HashSet<>();
		return lines;

	}

}
