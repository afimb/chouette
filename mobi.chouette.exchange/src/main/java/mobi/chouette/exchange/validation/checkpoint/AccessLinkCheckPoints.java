package mobi.chouette.exchange.validation.checkpoint;

import java.math.BigDecimal;
import java.util.Collection;

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
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.StopArea;

@Log4j
public class AccessLinkCheckPoints extends AbstractValidation<AccessLink> implements Validator<AccessLink> {

	@Override
	public ValidationConstraints validate(Context context, AccessLink target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Collection<AccessLink> beans = data.getAccessLinks();
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		if (isEmpty(beans))
			return null;
		// init checkPoints : add here all defined check points for this kind of
		// object
		// 3-AccessLink-1 : check distance between ends of AccessLink
		// 3-AccessLink-2 : check distance of link against distance between ends
		// of AccessLink
		// 3-AccessLink-3 : check speeds in AccessLink
		initCheckPoint(report, ACCESS_LINK_1, CheckPoint.SEVERITY.WARNING);
		initCheckPoint(report, ACCESS_LINK_2, CheckPoint.SEVERITY.WARNING);
		initCheckPoint(report, ACCESS_LINK_3, CheckPoint.SEVERITY.WARNING);
		prepareCheckPoint(report, ACCESS_LINK_1);
		prepareCheckPoint(report, ACCESS_LINK_2);
		prepareCheckPoint(report, ACCESS_LINK_3);
		boolean test4_1 = (parameters.getCheckAccessLink() != 0);
		if (test4_1) {
			initCheckPoint(report, L4_ACCESS_LINK_1, CheckPoint.SEVERITY.ERROR);
			prepareCheckPoint(report, L4_ACCESS_LINK_1);
		}

		for (AccessLink accessLink : beans) {
			check3AccessLink1_2(context, report, accessLink, parameters);
			check3AccessLink3(context, report, accessLink, parameters);

			// 4-AccessLink-1 : check columns constraints
			if (test4_1)
				check4Generic1(context, report, accessLink, L4_ACCESS_LINK_1, parameters, log);

		}
		return null;
	}

	private void check3AccessLink1_2(Context context, ValidationReport report, AccessLink accessLink,
			ValidationParameters parameters) {
		// 3-AccessLink-1 : check distance between stops of accessLink
		StopArea start = accessLink.getStopArea();
		AccessPoint end = accessLink.getAccessPoint();
		if (start == null || end == null) {
			return;
		}
		if (!start.hasCoordinates() || !end.hasCoordinates()) {
			return;
		}
		long distanceMax = parameters.getInterAccessLinkDistanceMax();

		double distance = distance(start, end);
		if (distance > distanceMax) {
			Location location = buildLocation(context, accessLink);
			Location startTarget = buildLocation(context, start);
			Location endTarget = buildLocation(context, end);
			Detail detail = new Detail(ACCESS_LINK_1, location, Integer.valueOf((int) distance).toString(), Integer
					.valueOf((int) distanceMax).toString(), startTarget, endTarget);
			addValidationError(report, ACCESS_LINK_1, detail);
		} else {
			// 3-AccessLink-2 : check distance of link against distance between
			// stops of accessLink
			if (accessLink.getLinkDistance() != null && !accessLink.getLinkDistance().equals(BigDecimal.ZERO)) {
				if (distance > accessLink.getLinkDistance().doubleValue()) {
					Location location = buildLocation(context, accessLink);
					Location startTarget = buildLocation(context, start);
					Location endTarget = buildLocation(context, end);
					Detail detail = new Detail(ACCESS_LINK_2, location, Integer.valueOf((int) distance).toString(),
							Integer.valueOf(accessLink.getLinkDistance().intValue()).toString(), startTarget, endTarget);
					addValidationError(report, ACCESS_LINK_2, detail);
				}
			}
		}

	}

	private void check3AccessLink3(Context context, ValidationReport report, AccessLink accessLink,
			ValidationParameters parameters) {
		// 3-AccessLink-3 : check speeds in accessLink
		double distance = 1; // meters
		if (accessLink.getLinkDistance() != null && !accessLink.getLinkDistance().equals(BigDecimal.ZERO)) {
			distance = accessLink.getLinkDistance().doubleValue();
		}
		int maxDefaultSpeed = parameters.getWalkDefaultSpeedMax();
		int maxFrequentSpeed = parameters.getWalkFrequentTravellerSpeedMax();
		int maxMobilitySpeed = parameters.getWalkMobilityRestrictedTravellerSpeedMax();
		int maxOccasionalSpeed = parameters.getWalkOccasionalTravellerSpeedMax();

		checkLinkSpeed(context, report, accessLink, accessLink.getDefaultDuration(), distance, maxDefaultSpeed,
				ACCESS_LINK_3, "_1");
		checkLinkSpeed(context, report, accessLink, accessLink.getOccasionalTravellerDuration(), distance,
				maxOccasionalSpeed, ACCESS_LINK_3, "_2");
		checkLinkSpeed(context, report, accessLink, accessLink.getFrequentTravellerDuration(), distance,
				maxFrequentSpeed, ACCESS_LINK_3, "_3");
		checkLinkSpeed(context, report, accessLink, accessLink.getMobilityRestrictedTravellerDuration(), distance,
				maxMobilitySpeed, ACCESS_LINK_3, "_4");

	}

}
