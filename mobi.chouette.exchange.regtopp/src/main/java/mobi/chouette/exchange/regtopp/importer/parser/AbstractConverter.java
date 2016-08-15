package mobi.chouette.exchange.regtopp.importer.parser;

import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeHeaderDKO;
import mobi.chouette.model.util.ObjectIdTypes;

public abstract class AbstractConverter {

	public static String composeGenericObjectId(String prefix, String type, String id) {
		return prefix + ":" + type + ":" + id.trim();
	}

	public static String extractOriginalId(String chouetteObjectId) {
		return chouetteObjectId.split(":")[2];
	}

	public static String createTimetableId(RegtoppImportParameters configuration, String adminCode, String dayCodeId, RegtoppDayCodeHeaderDKO header) {

		String localId;

		switch (configuration.getCalendarStrategy()) {

		case ADD:
			localId = adminCode + dayCodeId + "-" + header.getDate().toString();
			break;
		case UPDATE:
		default:
			localId = adminCode + dayCodeId;
		}

		return composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.TIMETABLE_KEY, localId);

	}

	public static String createOperatorId(RegtoppImportParameters configuration, String operatorCode) {
		return AbstractConverter.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.COMPANY_KEY, operatorCode);
	}

	public static String createAuthorityId(RegtoppImportParameters configuration, String adminCode) {
		// Same as operator for now
		return createOperatorId(configuration, adminCode);
	}

	public static String createNetworkId(RegtoppImportParameters configuration, String adminCode) {
		return AbstractConverter.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.PTNETWORK_KEY, adminCode);
	}

	public static String createLineId(RegtoppImportParameters configuration, String lineId) {
		return AbstractConverter.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.LINE_KEY, lineId);
	}

	private static String createStopPointId(RegtoppImportParameters configuration, String stopPointId) {
		return AbstractConverter.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.STOPPOINT_KEY, stopPointId);
	}

	public static String createStopPointId(RegtoppImportParameters configuration, RouteKey routeKey, String sequenceNumber) {
		String localId = routeKey.getLineId() + routeKey.getDirection() + routeKey.getRouteId() +sequenceNumber+ "-"+routeKey.getCalendarStartDate();
		return createStopPointId(configuration, localId);
	}

	public static String createStopAreaId(RegtoppImportParameters configuration, String stopAreaId) {
		return AbstractConverter.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.STOPAREA_KEY, stopAreaId);
	}

	public static String createRouteId(RegtoppImportParameters configuration, RouteKey routeKey) {
		String localId = routeKey.getLineId() + routeKey.getDirection() + routeKey.getRouteId() + "-"+routeKey.getCalendarStartDate();
		return AbstractConverter.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.ROUTE_KEY, localId);
	}

	public static String createJourneyPatternId(RegtoppImportParameters configuration, RouteKey routeKey) {
		String localId = routeKey.getLineId() + routeKey.getDirection() + routeKey.getRouteId() + "-"+routeKey.getCalendarStartDate();
		return AbstractConverter.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.JOURNEYPATTERN_KEY, localId);

	}

	public static String createVehicleJourneyId(RegtoppImportParameters configuration, String lineId, String tripId, String calendarStartDate) {
		return AbstractConverter.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.VEHICLEJOURNEY_KEY,
				lineId + tripId +"-"+ calendarStartDate);
	}

	public static String createConnectionLinkId(RegtoppImportParameters configuration, String stopAreaFrom, String stopAreaTo) {
		return AbstractConverter.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.CONNECTIONLINK_KEY, stopAreaFrom + "-" + stopAreaTo);
	}

}
