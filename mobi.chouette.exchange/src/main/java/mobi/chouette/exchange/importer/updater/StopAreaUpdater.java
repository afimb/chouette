package mobi.chouette.exchange.importer.updater;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.CollectionUtils;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.AccessLinkDAO;
import mobi.chouette.dao.AccessPointDAO;
import mobi.chouette.dao.ConnectionLinkDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
@Stateless(name = StopAreaUpdater.BEAN_NAME)
public class StopAreaUpdater implements Updater<StopArea> {

	public static final String BEAN_NAME = "StopAreaUpdater";

	@EJB
	private StopAreaDAO stopAreaDAO;

	@EJB(beanName = StopAreaUpdater.BEAN_NAME)
	private Updater<StopArea> stopAreaUpdater;

	@EJB
	private AccessPointDAO accessPointDAO;

	@EJB(beanName = AccessPointUpdater.BEAN_NAME)
	private Updater<AccessPoint> accessPointUpdater;

	@EJB
	private AccessLinkDAO accessLinkDAO;

	@EJB(beanName = AccessLinkUpdater.BEAN_NAME)
	private Updater<AccessLink> accessLinkUpdater;

	@EJB
	private ConnectionLinkDAO connectionLinkDAO;

	@EJB(beanName = ConnectionLinkUpdater.BEAN_NAME)
	private Updater<ConnectionLink> connectionLinkUpdater;

	@Override
	public void update(Context context, StopArea oldValue, StopArea newValue)
			throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		Referential cache = (Referential) context.get(CACHE);

		if (newValue.getObjectId() != null
				&& !newValue.getObjectId().equals(oldValue.getObjectId())) {
			oldValue.setObjectId(newValue.getObjectId());
		}
		if (newValue.getObjectVersion() != null
				&& !newValue.getObjectVersion().equals(
						oldValue.getObjectVersion())) {
			oldValue.setObjectVersion(newValue.getObjectVersion());
		}
		if (newValue.getCreationTime() != null
				&& !newValue.getCreationTime().equals(
						oldValue.getCreationTime())) {
			oldValue.setCreationTime(newValue.getCreationTime());
		}
		if (newValue.getCreatorId() != null
				&& !newValue.getCreatorId().equals(oldValue.getCreatorId())) {
			oldValue.setCreatorId(newValue.getCreatorId());
		}
		if (newValue.getName() != null
				&& !newValue.getName().equals(oldValue.getName())) {
			oldValue.setName(newValue.getName());
		}
		if (newValue.getComment() != null
				&& !newValue.getComment().equals(oldValue.getComment())) {
			oldValue.setComment(newValue.getComment());
		}
		if (newValue.getAreaType() != null
				&& !newValue.getAreaType().equals(oldValue.getAreaType())) {
			oldValue.setAreaType(newValue.getAreaType());
		}
		if (newValue.getRegistrationNumber() != null
				&& !newValue.getRegistrationNumber().equals(
						oldValue.getRegistrationNumber())) {
			oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
		}
		if (newValue.getNearestTopicName() != null
				&& !newValue.getNearestTopicName().equals(
						oldValue.getNearestTopicName())) {
			oldValue.setNearestTopicName(newValue.getNearestTopicName());
		}
		if (newValue.getUrl() != null
				&& !newValue.getUrl().equals(oldValue.getUrl())) {
			oldValue.setUrl(newValue.getUrl());
		}
		if (newValue.getTimeZone() != null
				&& !newValue.getTimeZone().equals(oldValue.getTimeZone())) {
			oldValue.setTimeZone(newValue.getTimeZone());
		}
		if (newValue.getFareCode() != null
				&& !newValue.getFareCode().equals(oldValue.getFareCode())) {
			oldValue.setFareCode(newValue.getFareCode());
		}
		if (newValue.getLiftAvailable() != null
				&& !newValue.getLiftAvailable().equals(
						oldValue.getLiftAvailable())) {
			oldValue.setLiftAvailable(newValue.getLiftAvailable());
		}
		if (newValue.getMobilityRestrictedSuitable() != null
				&& !newValue.getMobilityRestrictedSuitable().equals(
						oldValue.getMobilityRestrictedSuitable())) {
			oldValue.setMobilityRestrictedSuitable(newValue
					.getMobilityRestrictedSuitable());
		}
		if (newValue.getStairsAvailable() != null
				&& !newValue.getStairsAvailable().equals(
						oldValue.getStairsAvailable())) {
			oldValue.setStairsAvailable(newValue.getStairsAvailable());
		}
		if (newValue.getIntUserNeeds() != null
				&& !newValue.getIntUserNeeds().equals(
						oldValue.getIntUserNeeds())) {
			oldValue.setIntUserNeeds(newValue.getIntUserNeeds());
		}

		if (newValue.getLongitude() != null
				&& !newValue.getLongitude().equals(oldValue.getLongitude())) {
			oldValue.setLongitude(newValue.getLongitude());
		}
		if (newValue.getLatitude() != null
				&& !newValue.getLatitude().equals(oldValue.getLatitude())) {
			oldValue.setLatitude(newValue.getLatitude());
		}
		if (newValue.getLongLatType() != null
				&& !newValue.getLongLatType().equals(oldValue.getLongLatType())) {
			oldValue.setLongLatType(newValue.getLongLatType());
		}
		if (newValue.getX() != null && !newValue.getX().equals(oldValue.getX())) {
			oldValue.setX(newValue.getX());
		}
		if (newValue.getY() != null && !newValue.getY().equals(oldValue.getY())) {
			oldValue.setY(newValue.getY());
		}
		if (newValue.getProjectionType() != null
				&& !newValue.getProjectionType().equals(
						oldValue.getProjectionType())) {
			oldValue.setProjectionType(newValue.getProjectionType());
		}
		if (newValue.getCountryCode() != null
				&& !newValue.getCountryCode().equals(oldValue.getCountryCode())) {
			oldValue.setCountryCode(newValue.getCountryCode());
		}
		if (newValue.getZipCode() != null
				&& !newValue.getZipCode().equals(oldValue.getZipCode())) {
			oldValue.setZipCode(newValue.getZipCode());
		}
		if (newValue.getCityName() != null
				&& !newValue.getCityName().equals(oldValue.getCityName())) {
			oldValue.setCityName(newValue.getCityName());
		}
		if (newValue.getStreetName() != null
				&& !newValue.getStreetName().equals(oldValue.getStreetName())) {
			oldValue.setStreetName(newValue.getStreetName());
		}

		// StopArea Parent
		if (newValue.getParent() == null) {
			oldValue.setParent(null);
		} else {
			String objectId = newValue.getParent().getObjectId();
			StopArea stopArea = cache.getStopAreas().get(objectId);
			if (stopArea == null) {
				stopArea = stopAreaDAO.findByObjectId(objectId);
				if (stopArea != null) {
					cache.getStopAreas().put(objectId, stopArea);
				}
			}

			if (stopArea == null) {
				stopArea = ObjectFactory.getStopArea(cache, objectId);
			}
			oldValue.setParent(stopArea);
			stopAreaUpdater.update(context, oldValue.getParent(),
					newValue.getParent());
		}

		// AccessPoint
		Collection<AccessPoint> addedAccessPoint = CollectionUtils.substract(
				newValue.getAccessPoints(), oldValue.getAccessPoints(),
				NeptuneIdentifiedObjectComparator.INSTANCE);

		List<AccessPoint> accessPoints = null;
		for (AccessPoint item : addedAccessPoint) {

			AccessPoint accessPoint = cache.getAccessPoints().get(
					item.getObjectId());
			if (accessPoint == null) {
				if (accessPoints == null) {
					accessPoints = accessPointDAO.findByObjectId(UpdaterUtils
							.getObjectIds(addedAccessPoint));
					for (AccessPoint object : accessPoints) {
						cache.getAccessPoints().put(object.getObjectId(),
								object);
					}
				}
				accessPoint = cache.getAccessPoints().get(item.getObjectId());
			}

			if (accessPoint == null) {
				accessPoint = ObjectFactory.getAccessPoint(cache,
						item.getObjectId());
			}
			accessPoint.setContainedIn(oldValue);
		}

		Collection<Pair<AccessPoint, AccessPoint>> modifiedAccessPoint = CollectionUtils
				.intersection(oldValue.getAccessPoints(),
						newValue.getAccessPoints(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<AccessPoint, AccessPoint> pair : modifiedAccessPoint) {
			accessPointUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		// Collection<AccessPoint> removedAccessPoint =
		// CollectionUtils.substract(
		// oldValue.getAccessPoints(), newValue.getAccessPoints(),
		// NeptuneIdentifiedObjectComparator.INSTANCE);
		// for (AccessPoint accessPoint : removedAccessPoint) {
		// accessPoint.setContainedIn(null);
		// accessPointDAO.delete(accessPoint);
		// }

		// AccessLink
		Collection<AccessLink> addedAccessLink = CollectionUtils.substract(
				newValue.getAccessLinks(), oldValue.getAccessLinks(),
				NeptuneIdentifiedObjectComparator.INSTANCE);

		List<AccessLink> accessLinks = null;
		for (AccessLink item : addedAccessLink) {

			AccessLink accessLink = cache.getAccessLinks().get(
					item.getObjectId());
			if (accessLink == null) {
				if (accessLinks == null) {
					accessLinks = accessLinkDAO.findByObjectId(UpdaterUtils
							.getObjectIds(addedAccessLink));
					for (AccessLink object : accessLinks) {
						cache.getAccessLinks()
						.put(object.getObjectId(), object);
					}
				}
				accessLink = cache.getAccessLinks().get(item.getObjectId());
			}

			if (accessLink == null) {
				accessLink = ObjectFactory.getAccessLink(cache,
						item.getObjectId());
			}
			accessLink.setStopArea(oldValue);
		}

		Collection<Pair<AccessLink, AccessLink>> modifiedAccessLink = CollectionUtils
				.intersection(oldValue.getAccessLinks(),
						newValue.getAccessLinks(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<AccessLink, AccessLink> pair : modifiedAccessLink) {
			accessLinkUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		// Collection<AccessLink> removedAccessLink = CollectionUtils.substract(
		// oldValue.getAccessLinks(), newValue.getAccessLinks(),
		// NeptuneIdentifiedObjectComparator.INSTANCE);
		// for (AccessLink accessLink : removedAccessLink) {
		// accessLink.setStopArea(null);
		// accessLinkDAO.delete(accessLink);
		// }

		// StartOfLink
		Collection<ConnectionLink> addedStartOfLink = CollectionUtils
				.substract(newValue.getConnectionStartLinks(),
						oldValue.getConnectionStartLinks(),
						NeptuneIdentifiedObjectComparator.INSTANCE);

		List<ConnectionLink> startOfLinks = null;
		for (ConnectionLink item : addedStartOfLink) {

			ConnectionLink startOfLink = cache.getConnectionLinks().get(
					item.getObjectId());
			//			if (startOfLink == null) {
			//				if (startOfLinks == null) {
			//					startOfLinks = connectionLinkDAO
			//							.findByObjectId(UpdaterUtils
			//									.getObjectIds(addedStartOfLink));
			//					for (ConnectionLink object : startOfLinks) {
			//						cache.getConnectionLinks().put(object.getObjectId(),
			//								object);
			//					}
			//				}
			//				startOfLink = cache.getConnectionLinks()
			//						.get(item.getObjectId());
			//			}
			//
			//			if (startOfLink == null) {
			//				startOfLink = ObjectFactory.getConnectionLink(cache,
			//						item.getObjectId());
			//				// startOfLink.setObjectId(item.getObjectId());
			//			}
			if (!item.getEndOfLink().isDetached() || item.getEndOfLink().isFilled())

				startOfLink.setStartOfLink(oldValue);
		}

		Collection<Pair<ConnectionLink, ConnectionLink>> modifiedStartOfLink = CollectionUtils
				.intersection(oldValue.getConnectionStartLinks(),
						newValue.getConnectionStartLinks(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<ConnectionLink, ConnectionLink> pair : modifiedStartOfLink) {
			connectionLinkUpdater.update(context, pair.getLeft(),
					pair.getRight());
		}

		// EndOfLink
		Collection<ConnectionLink> addedEndOfLink = CollectionUtils.substract(
				newValue.getConnectionEndLinks(),
				oldValue.getConnectionEndLinks(),
				NeptuneIdentifiedObjectComparator.INSTANCE);

		List<ConnectionLink> endOfLinks = null;
		for (ConnectionLink item : addedEndOfLink) {

			ConnectionLink endOfLink = cache.getConnectionLinks().get(
					item.getObjectId());
			//			if (endOfLink == null) {
			//				if (endOfLinks == null) {
			//					endOfLinks = connectionLinkDAO.findByObjectId(UpdaterUtils
			//							.getObjectIds(addedEndOfLink));
			//					for (ConnectionLink object : endOfLinks) {
			//						cache.getConnectionLinks().put(object.getObjectId(),
			//								object);
			//					}
			//				}
			//				endOfLink = cache.getConnectionLinks().get(item.getObjectId());
			//			}
			//
			//			if (endOfLink == null) {
			//				endOfLink = ObjectFactory.getConnectionLink(cache,
			//						item.getObjectId());
			//			}
			if (!item.getStartOfLink().isDetached() || item.getStartOfLink().isFilled())
				endOfLink.setEndOfLink(oldValue);
		}

		Collection<Pair<ConnectionLink, ConnectionLink>> modifiedEndOfLink = CollectionUtils
				.intersection(oldValue.getConnectionEndLinks(),
						newValue.getConnectionEndLinks(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<ConnectionLink, ConnectionLink> pair : modifiedEndOfLink) {
			connectionLinkUpdater.update(context, pair.getLeft(),
					pair.getRight());
		}

		// TODO list routing_constraints_lines (routingConstraintLines) 
		// TODO list stop_areas_stop_areas (routingConstraintAreas)
		Collection<StopArea> addedStopAreas = CollectionUtils.substract(
				newValue.getRoutingConstraintAreas(),
				oldValue.getRoutingConstraintAreas(),
				NeptuneIdentifiedObjectComparator.INSTANCE);

		List<StopArea> stopAreas = null;
		for (StopArea item : addedStopAreas) {

			StopArea area = cache.getStopAreas().get(
					item.getObjectId());
			if (area == null) {
				if (stopAreas == null) {
					stopAreas = stopAreaDAO.findByObjectId(UpdaterUtils
							.getObjectIds(addedEndOfLink));
					for (StopArea object : addedStopAreas) {
						cache.getStopAreas().put(object.getObjectId(),
								object);
					}
				}
				area = cache.getStopAreas().get(item.getObjectId());
			}

			if (area == null) {
				area = ObjectFactory.getStopArea(cache,
						item.getObjectId());
			}
			if (!area.isDetached() || area.isFilled())
			   oldValue.getRoutingConstraintAreas().add(area);
		}

		Collection<Pair<StopArea, StopArea>> modifiedStopArea = CollectionUtils
				.intersection(oldValue.getRoutingConstraintAreas(),
						newValue.getRoutingConstraintAreas(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<StopArea, StopArea> pair : modifiedStopArea) {
			stopAreaUpdater.update(context, pair.getLeft(),
					pair.getRight());
		}


	}
}
