package mobi.chouette.exchange.validator.checkpoint;

import java.math.BigDecimal;
import java.util.Collection;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validator.ValidationConstraints;
import mobi.chouette.exchange.validator.ValidationData;
import mobi.chouette.exchange.validator.Validator;
import mobi.chouette.exchange.validator.parameters.ValidationParameters;
import mobi.chouette.exchange.validator.report.CheckPoint;
import mobi.chouette.exchange.validator.report.Detail;
import mobi.chouette.exchange.validator.report.Location;
import mobi.chouette.exchange.validator.report.ValidationReport;
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
			check3AccessLink1_2(report, accessLink, parameters);
			check3AccessLink3(report, accessLink, parameters);

			// 4-AccessLink-1 : check columns constraints
			if (test4_1)
				check4Generic1(report, accessLink, L4_ACCESS_LINK_1, parameters, context, log);

		}
		return null;
	}

	private void check3AccessLink1_2(ValidationReport report, AccessLink accessLink, ValidationParameters parameters) {
		// 3-AccessLink-1 : check distance between stops of accessLink
		StopArea start = accessLink.getStopArea();
		AccessPoint end = accessLink.getAccessPoint();
		if (start == null || end == null)
			return;
		if (!start.hasCoordinates() || !end.hasCoordinates())
			return;
		long distanceMax = parameters.getInterAccessLinkDistanceMax();

		double distance = distance(start, end);
		if (distance > distanceMax) {
			Location location = new Location(accessLink);
			Location startTarget = new Location(start);
			Location endTarget = new Location(end);
			Detail detail = new Detail(ACCESS_LINK_1, location, Integer.valueOf((int) distance).toString(), Integer
					.valueOf((int) distanceMax).toString(), startTarget, endTarget);
			addValidationError(report, ACCESS_LINK_1, detail);
		} else {
			// 3-AccessLink-2 : check distance of link against distance between
			// stops of accessLink
			if (accessLink.getLinkDistance() != null && !accessLink.getLinkDistance().equals(BigDecimal.ZERO)) {
				if (distance > accessLink.getLinkDistance().doubleValue()) {
					Location location = new Location(accessLink);
					Location startTarget = new Location(start);
					Location endTarget = new Location(end);
					Detail detail = new Detail(ACCESS_LINK_2, location, Integer.valueOf((int) distance).toString(),
							Integer.valueOf(accessLink.getLinkDistance().intValue()).toString(), startTarget, endTarget);
					addValidationError(report, ACCESS_LINK_2, detail);
				}
			}
		}

	}

	private void check3AccessLink3(ValidationReport report, AccessLink accessLink, ValidationParameters parameters) {
		// 3-AccessLink-3 : check speeds in accessLink
		double distance = 1; // meters
		if (accessLink.getLinkDistance() != null && !accessLink.getLinkDistance().equals(BigDecimal.ZERO)) {
			distance = accessLink.getLinkDistance().doubleValue();
		}
		int maxDefaultSpeed = parameters.getWalkDefaultSpeedMax();
		int maxFrequentSpeed = parameters.getWalkFrequentTravellerSpeedMax();
		int maxMobilitySpeed = parameters.getWalkMobilityRestrictedTravellerSpeedMax();
		int maxOccasionalSpeed = parameters.getWalkOccasionalTravellerSpeedMax();

		checkLinkSpeed(report, accessLink, accessLink.getDefaultDuration(), distance, maxDefaultSpeed, ACCESS_LINK_3,
				"_1");
		checkLinkSpeed(report, accessLink, accessLink.getOccasionalTravellerDuration(), distance, maxOccasionalSpeed,
				ACCESS_LINK_3, "_2");
		checkLinkSpeed(report, accessLink, accessLink.getFrequentTravellerDuration(), distance, maxFrequentSpeed,
				ACCESS_LINK_3, "_3");
		checkLinkSpeed(report, accessLink, accessLink.getMobilityRestrictedTravellerDuration(), distance,
				maxMobilitySpeed, ACCESS_LINK_3, "_4");

	}

}
