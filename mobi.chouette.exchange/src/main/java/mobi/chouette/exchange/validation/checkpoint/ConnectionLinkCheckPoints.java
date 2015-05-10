package mobi.chouette.exchange.validation.checkpoint;

import java.math.BigDecimal;
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
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

@Log4j
public class ConnectionLinkCheckPoints extends AbstractValidation<ConnectionLink> implements Validator<ConnectionLink> {

	@Override
	public ValidationConstraints validate(Context context, ConnectionLink target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<ConnectionLink> beans = new ArrayList<>(data.getConnectionLinks());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		if (isEmpty(beans))
			return null;
		// init checkPoints : add here all defined check points for this kind of
		// object
		// 3-ConnectionLink-1 : check distance between stops of connectionLink
		// 3-ConnectionLink-2 : check distance of link against distance between
		// stops of connectionLink
		// 3-ConnectionLink-3 : check speeds in connectionLink
		initCheckPoint(report, CONNECTION_LINK_1, CheckPoint.SEVERITY.WARNING);
		initCheckPoint(report, CONNECTION_LINK_2, CheckPoint.SEVERITY.WARNING);
		initCheckPoint(report, CONNECTION_LINK_3, CheckPoint.SEVERITY.WARNING);
		prepareCheckPoint(report, CONNECTION_LINK_1);
		prepareCheckPoint(report, CONNECTION_LINK_2);
		prepareCheckPoint(report, CONNECTION_LINK_3);

		boolean test4_1 = (parameters.getCheckConnectionLink() == 1);
		if (test4_1) {
			initCheckPoint(report, L4_CONNECTION_LINK_1, CheckPoint.SEVERITY.ERROR);
			prepareCheckPoint(report, L4_CONNECTION_LINK_1);
		}
		boolean test4_2 = parameters.getCheckConnectionLinkOnPhysical() == 1;
		if (test4_2) {
			initCheckPoint(report, L4_CONNECTION_LINK_2, CheckPoint.SEVERITY.ERROR);
			prepareCheckPoint(report, L4_CONNECTION_LINK_2);
		}
		for (int i = 0; i < beans.size(); i++) {
			ConnectionLink connectionLink = beans.get(i);
			check3ConnectionLink1_2(context,report, connectionLink, parameters);
			check3ConnectionLink3(context,report, connectionLink, parameters);
			// 4-ConnectionLink-1 : check columns constraints
			if (test4_1)
				check4Generic1(context,report, connectionLink, L4_CONNECTION_LINK_1, parameters, log);
			// 4-ConnectionLink-2 : check linked stop areas
			if (test4_2)
				check4ConnectionLink2(context,report, connectionLink);

		}
		return null;
	}

	private void check3ConnectionLink1_2(Context context, ValidationReport report, ConnectionLink connectionLink,
			ValidationParameters parameters) {
		// 3-ConnectionLink-1 : check distance between stops of connectionLink
		StopArea start = connectionLink.getStartOfLink();
		StopArea end = connectionLink.getEndOfLink();
		if (start == null | end == null)
			return;
		if (!start.hasCoordinates() || !end.hasCoordinates())
			return;
		long distanceMax = parameters.getInterConnectionLinkDistanceMax();

		double distance = distance(start, end);
		if (distance > distanceMax) {
			Location location = buildLocation(context,connectionLink);
			Location startLocation = buildLocation(context,start);
			Location endLocation = buildLocation(context,end);

			Detail detail = new Detail(CONNECTION_LINK_1, location, Integer.toString((int) distance),
					Integer.toString((int) distanceMax), startLocation, endLocation);
			addValidationError(report, CONNECTION_LINK_1, detail);
		} else {
			// 3-ConnectionLink-2 : check distance of link against distance
			// between stops of connectionLink
			if (connectionLink.getLinkDistance() != null && !connectionLink.getLinkDistance().equals(BigDecimal.ZERO)) {
				if (distance > connectionLink.getLinkDistance().doubleValue()) {
					Location location = buildLocation(context,connectionLink);
					Location startLocation = buildLocation(context,start);
					Location endLocation = buildLocation(context,end);

					Detail detail = new Detail(CONNECTION_LINK_2, location, Integer.toString((int) distance),
							Integer.toString(connectionLink.getLinkDistance().intValue()), startLocation, endLocation);
					addValidationError(report, CONNECTION_LINK_2, detail);
				}
			}
		}

	}

	private void check3ConnectionLink3(Context context, ValidationReport report, ConnectionLink connectionLink,
			ValidationParameters parameters) {
		// 3-ConnectionLink-3 : check speeds in connectionLink
		double distance = 1; // meters
		if (connectionLink.getLinkDistance() != null && !connectionLink.getLinkDistance().equals(BigDecimal.ZERO)) {
			distance = connectionLink.getLinkDistance().doubleValue();
		}
		int maxDefaultSpeed = parameters.getWalkDefaultSpeedMax();
		int maxFrequentSpeed = parameters.getWalkFrequentTravellerSpeedMax();
		int maxMobilitySpeed = parameters.getWalkMobilityRestrictedTravellerSpeedMax();
		int maxOccasionalSpeed = parameters.getWalkOccasionalTravellerSpeedMax();

		checkLinkSpeed(context,report, connectionLink, connectionLink.getDefaultDuration(), distance, maxDefaultSpeed,
				CONNECTION_LINK_3, "_1");
		checkLinkSpeed(context,report, connectionLink, connectionLink.getOccasionalTravellerDuration(), distance,
				maxOccasionalSpeed, CONNECTION_LINK_3, "_2");
		checkLinkSpeed(context,report, connectionLink, connectionLink.getFrequentTravellerDuration(), distance,
				maxFrequentSpeed, CONNECTION_LINK_3, "_3");
		checkLinkSpeed(context,report, connectionLink, connectionLink.getMobilityRestrictedTravellerDuration(), distance,
				maxMobilitySpeed, CONNECTION_LINK_3, "_4");

	}

	private void check4ConnectionLink2(Context context, ValidationReport report, ConnectionLink connectionLink) {
		StopArea start = connectionLink.getStartOfLink();
		StopArea end = connectionLink.getEndOfLink();
		if (start == null | end == null)
			return;
		if (start.getAreaType().ordinal() > ChouetteAreaEnum.BoardingPosition.ordinal()
				|| end.getAreaType().ordinal() > ChouetteAreaEnum.BoardingPosition.ordinal()) {
			Location location = buildLocation(context,connectionLink);
			Location startLocation = buildLocation(context,start);
			Location endLocation = buildLocation(context,end);

			Detail detail = new Detail(L4_CONNECTION_LINK_2, location, startLocation, endLocation);
			addValidationError(report, L4_CONNECTION_LINK_2, detail);
		}
	}

}
