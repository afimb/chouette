package mobi.chouette.exchange.validation.checkpoint;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

@Log4j
public class ConnectionLinkCheckPoints extends AbstractValidation<ConnectionLink> implements Validator<ConnectionLink> {

	@Override
	public void validate(Context context, ConnectionLink target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<ConnectionLink> beans = new ArrayList<>(data.getConnectionLinks());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		if (isEmpty(beans))
			return ;
		// init checkPoints : add here all defined check points for this kind of
		// object
		// 3-ConnectionLink-1 : check distance between stops of connectionLink
		// 3-ConnectionLink-2 : check distance of link against distance between
		// stops of connectionLink
		// 3-ConnectionLink-3 : check speeds in connectionLink
		initCheckPoint(context, CONNECTION_LINK_1, SEVERITY.W);
		initCheckPoint(context, CONNECTION_LINK_2, SEVERITY.W);
		initCheckPoint(context, CONNECTION_LINK_3, SEVERITY.W);
		prepareCheckPoint(context, CONNECTION_LINK_1);
		prepareCheckPoint(context, CONNECTION_LINK_2);
		prepareCheckPoint(context, CONNECTION_LINK_3);

		boolean test4_1 = (parameters.getCheckConnectionLink() == 1);
		if (test4_1) {
			initCheckPoint(context, L4_CONNECTION_LINK_1, SEVERITY.E);
			prepareCheckPoint(context, L4_CONNECTION_LINK_1);
		}
		boolean test4_2 = parameters.getCheckConnectionLinkOnPhysical() == 1;
		if (test4_2) {
			initCheckPoint(context, L4_CONNECTION_LINK_2, SEVERITY.E);
			prepareCheckPoint(context, L4_CONNECTION_LINK_2);
		}
		for (int i = 0; i < beans.size(); i++) {
			ConnectionLink connectionLink = beans.get(i);
			check3ConnectionLink1_2(context, connectionLink, parameters);
			check3ConnectionLink3(context, connectionLink, parameters);
			// 4-ConnectionLink-1 : check columns constraints
			if (test4_1)
				check4Generic1(context, connectionLink, L4_CONNECTION_LINK_1, parameters, log);
			// 4-ConnectionLink-2 : check linked stop areas
			if (test4_2)
				check4ConnectionLink2(context, connectionLink);

		}
		return ;
	}

	private void check3ConnectionLink1_2(Context context,  ConnectionLink connectionLink,
			ValidationParameters parameters) {
		// 3-ConnectionLink-1 : check distance between stops of connectionLink
		StopArea start = connectionLink.getStartOfLink();
		StopArea end = connectionLink.getEndOfLink();
		if (start == null | end == null)
			return;
		if (!start.hasCoordinates() || !end.hasCoordinates())
			return;
		long distanceMax = parameters.getInterConnectionLinkDistanceMax();

		double distance = quickDistance(start, end);
		if (distance > distanceMax) {
			DataLocation location = buildLocation(context,connectionLink);
			DataLocation startLocation = buildLocation(context,start);
			DataLocation endLocation = buildLocation(context,end);

			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context,CONNECTION_LINK_1, location, Integer.toString((int) distance),
					Integer.toString((int) distanceMax), startLocation, endLocation);
		} else {
			// 3-ConnectionLink-2 : check distance of link against distance
			// between stops of connectionLink
			if (connectionLink.getLinkDistance() != null && !connectionLink.getLinkDistance().equals(BigDecimal.ZERO)) {
				if (distance > connectionLink.getLinkDistance().doubleValue()) {
					DataLocation location = buildLocation(context,connectionLink);
					DataLocation startLocation = buildLocation(context,start);
					DataLocation endLocation = buildLocation(context,end);

					ValidationReporter reporter = ValidationReporter.Factory.getInstance();
					reporter.addCheckPointReportError(context,CONNECTION_LINK_2, location, Integer.toString((int) distance),
							Integer.toString(connectionLink.getLinkDistance().intValue()), startLocation, endLocation);
				}
			}
		}

	}

	private void check3ConnectionLink3(Context context,  ConnectionLink connectionLink,
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

		checkLinkSpeed(context, connectionLink, connectionLink.getDefaultDuration(), distance, maxDefaultSpeed,
				CONNECTION_LINK_3, "1");
		checkLinkSpeed(context, connectionLink, connectionLink.getOccasionalTravellerDuration(), distance,
				maxOccasionalSpeed, CONNECTION_LINK_3, "2");
		checkLinkSpeed(context,connectionLink, connectionLink.getFrequentTravellerDuration(), distance,
				maxFrequentSpeed, CONNECTION_LINK_3, "3");
		checkLinkSpeed(context, connectionLink, connectionLink.getMobilityRestrictedTravellerDuration(), distance,
				maxMobilitySpeed, CONNECTION_LINK_3, "4");

	}

	private void check4ConnectionLink2(Context context,  ConnectionLink connectionLink) {
		StopArea start = connectionLink.getStartOfLink();
		StopArea end = connectionLink.getEndOfLink();
		if (start == null | end == null)
			return;
		if (start.getAreaType().ordinal() > ChouetteAreaEnum.BoardingPosition.ordinal()
				|| end.getAreaType().ordinal() > ChouetteAreaEnum.BoardingPosition.ordinal()) {
			DataLocation location = buildLocation(context,connectionLink);
			DataLocation startLocation = buildLocation(context,start);
			DataLocation endLocation = buildLocation(context,end);

			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context,L4_CONNECTION_LINK_2, location,null,null, startLocation, endLocation);
		}
	}

}
