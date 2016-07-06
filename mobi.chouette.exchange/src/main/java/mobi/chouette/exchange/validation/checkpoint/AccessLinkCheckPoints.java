package mobi.chouette.exchange.validation.checkpoint;

import java.math.BigDecimal;
import java.util.Collection;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.StopArea;

@Log4j
public class AccessLinkCheckPoints extends AbstractValidation<AccessLink> implements Validator<AccessLink> {

	@Override
	public void validate(Context context, AccessLink target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Collection<AccessLink> beans = data.getAccessLinks();
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		if (isEmpty(beans))
			return ;
		// init checkPoints : add here all defined check points for this kind of
		// object
		// 3-AccessLink-1 : check distance between ends of AccessLink
		// 3-AccessLink-2 : check distance of link against distance between ends
		// of AccessLink
		// 3-AccessLink-3 : check speeds in AccessLink
		initCheckPoint(context, ACCESS_LINK_1, SEVERITY.W);
		initCheckPoint(context, ACCESS_LINK_2, SEVERITY.W);
		initCheckPoint(context, ACCESS_LINK_3, SEVERITY.W);
		prepareCheckPoint(context, ACCESS_LINK_1);
		prepareCheckPoint(context, ACCESS_LINK_2);
		prepareCheckPoint(context, ACCESS_LINK_3);
		boolean test4_1 = (parameters.getCheckAccessLink() != 0);
		if (test4_1) {
			initCheckPoint(context, L4_ACCESS_LINK_1, SEVERITY.E);
			prepareCheckPoint(context, L4_ACCESS_LINK_1);
		}

		for (AccessLink accessLink : beans) {
			check3AccessLink1_2(context, accessLink, parameters);
			check3AccessLink3(context, accessLink, parameters);

			// 4-AccessLink-1 : check columns constraints
			if (test4_1)
				check4Generic1(context,  accessLink, L4_ACCESS_LINK_1, parameters, log);

		}
		return ;
	}

	private void check3AccessLink1_2(Context context,  AccessLink accessLink,
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

		double distance = quickDistance(start, end);
		if (distance > distanceMax) {
			
			DataLocation location = buildLocation(context, accessLink);
			DataLocation startTarget = buildLocation(context, start);
			DataLocation endTarget = buildLocation(context, end);
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, ACCESS_LINK_1, location, Integer.valueOf((int) distance).toString(), Integer
					.valueOf((int) distanceMax).toString(), startTarget, endTarget);
		} else {
			// 3-AccessLink-2 : check distance of link against distance between
			// stops of accessLink
			if (accessLink.getLinkDistance() != null && !accessLink.getLinkDistance().equals(BigDecimal.ZERO)) {
				if (distance > accessLink.getLinkDistance().doubleValue()) {
					DataLocation location = buildLocation(context, accessLink);
					DataLocation startTarget = buildLocation(context, start);
					DataLocation endTarget = buildLocation(context, end);
					ValidationReporter reporter = ValidationReporter.Factory.getInstance();
					reporter.addCheckPointReportError(context,ACCESS_LINK_2, location, Integer.valueOf((int) distance).toString(),
							Integer.valueOf(accessLink.getLinkDistance().intValue()).toString(), startTarget, endTarget);
				}
			}
		}

	}

	private void check3AccessLink3(Context context,  AccessLink accessLink,
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

		checkLinkSpeed(context, accessLink, accessLink.getDefaultDuration(), distance, maxDefaultSpeed,
				ACCESS_LINK_3, "1");
		checkLinkSpeed(context,  accessLink, accessLink.getOccasionalTravellerDuration(), distance,
				maxOccasionalSpeed, ACCESS_LINK_3, "2");
		checkLinkSpeed(context, accessLink, accessLink.getFrequentTravellerDuration(), distance,
				maxFrequentSpeed, ACCESS_LINK_3, "3");
		checkLinkSpeed(context,  accessLink, accessLink.getMobilityRestrictedTravellerDuration(), distance,
				maxMobilitySpeed, ACCESS_LINK_3, "4");

	}

}
