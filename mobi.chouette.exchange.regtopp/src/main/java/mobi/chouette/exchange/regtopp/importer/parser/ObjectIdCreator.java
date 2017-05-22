package mobi.chouette.exchange.regtopp.importer.parser;

import org.apache.commons.lang.StringUtils;

import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeHeaderDKO;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.util.ObjectIdTypes;

public abstract class ObjectIdCreator {

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

	public static String recomputeTimetableId(RegtoppImportParameters configuration, String adminCode,
			Timetable timetableToModify,RegtoppDayCodeHeaderDKO header) {

		
		String[] split = StringUtils.split(timetableToModify.getObjectId(),":");
		String dayCodeId = split[2].substring(3, 7);

		return createTimetableId(configuration, adminCode, dayCodeId, header);
	}

	public static String createOperatorId(RegtoppImportParameters configuration, String operatorCode) {
		return ObjectIdCreator.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.COMPANY_KEY, operatorCode);
	}

	public static String createAuthorityId(RegtoppImportParameters configuration, String adminCode) {
		// Same as operator for now
		return createOperatorId(configuration, adminCode);
	}

	public static String createNetworkId(RegtoppImportParameters configuration, String adminCode) {
		return ObjectIdCreator.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.PTNETWORK_KEY, adminCode);
	}

	public static String createLineId(RegtoppImportParameters configuration, String lineId, String calendarStartDate) {
		return ObjectIdCreator.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.LINE_KEY, lineId+"-"+calendarStartDate);
	}

	public static String getCalendarStartDate(String objectId) {
		// NOTE: Must be aligned with function createLineId
		return objectId.substring(objectId.length()-10);
	}
	
	private static String createStopPointId(RegtoppImportParameters configuration, String stopPointId) {
		return ObjectIdCreator.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.STOPPOINT_KEY, stopPointId);
	}

	public static String createStopPointId(RegtoppImportParameters configuration, RouteKey routeKey, String sequenceNumber) {
		String localId = routeKey.getLineId() + routeKey.getDirection() + routeKey.getRouteId() +sequenceNumber+ "-"+routeKey.getCalendarStartDate();
		return createStopPointId(configuration, localId);
	}

	public static String createStopPlaceId(RegtoppImportParameters configuration, String stopAreaId) {
		return ObjectIdCreator.composeGenericObjectId(configuration.getObjectIdPrefix(), "StopPlace", stopAreaId);
	}

	public static String createQuayId(RegtoppImportParameters configuration, String stopAreaId) {
		return ObjectIdCreator.composeGenericObjectId(configuration.getObjectIdPrefix(), "Quay", stopAreaId);
	}

	public static String createRouteId(RegtoppImportParameters configuration, RouteKey routeKey) {
		String localId = routeKey.getLineId() + routeKey.getDirection() + routeKey.getRouteId() + "-"+routeKey.getCalendarStartDate();
		return ObjectIdCreator.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.ROUTE_KEY, localId);
	}

	public static String createJourneyPatternId(RegtoppImportParameters configuration, RouteKey routeKey) {
		String localId = routeKey.getLineId() + routeKey.getDirection() + routeKey.getRouteId() + "-"+routeKey.getCalendarStartDate();
		return ObjectIdCreator.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.JOURNEYPATTERN_KEY, localId);

	}

	public static String createVehicleJourneyId(RegtoppImportParameters configuration, String lineId, String tripId, String calendarStartDate) {
		return ObjectIdCreator.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.VEHICLEJOURNEY_KEY,
				lineId + tripId +"-"+ calendarStartDate);
	}

	public static String createConnectionLinkId(RegtoppImportParameters configuration, String stopAreaFrom, String stopAreaTo) {
		return ObjectIdCreator.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.CONNECTIONLINK_KEY, stopAreaFrom + "-" + stopAreaTo);
	}


}
