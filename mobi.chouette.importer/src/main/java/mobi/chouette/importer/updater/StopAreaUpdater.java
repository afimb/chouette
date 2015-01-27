package mobi.chouette.importer.updater;

import java.util.Collection;

import javax.ejb.EJB;

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

public class StopAreaUpdater implements Updater<StopArea> {

	@EJB
	private StopAreaDAO stopAreaDAO;

	@EJB
	private AccessPointDAO accessPointDAO;

	@EJB
	private AccessLinkDAO accessLinkDAO;

	@EJB
	private ConnectionLinkDAO connectionLinkDAO;

	@Override
	public void update(Context context, StopArea oldValue, StopArea newValue)
			throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		if (newValue.getObjectId() != null
				&& newValue.getObjectId().compareTo(oldValue.getObjectId()) != 0) {
			oldValue.setObjectId(newValue.getObjectId());
		}
		if (newValue.getObjectVersion() != null
				&& newValue.getObjectVersion().compareTo(
						oldValue.getObjectVersion()) != 0) {
			oldValue.setObjectVersion(newValue.getObjectVersion());
		}
		if (newValue.getCreationTime() != null
				&& newValue.getCreationTime().compareTo(
						oldValue.getCreationTime()) != 0) {
			oldValue.setCreationTime(newValue.getCreationTime());
		}
		if (newValue.getCreatorId() != null
				&& newValue.getCreatorId().compareTo(oldValue.getCreatorId()) != 0) {
			oldValue.setCreatorId(newValue.getCreatorId());
		}
		if (newValue.getName() != null
				&& newValue.getName().compareTo(oldValue.getName()) != 0) {
			oldValue.setName(newValue.getName());
		}
		if (newValue.getComment() != null
				&& newValue.getComment().compareTo(oldValue.getComment()) != 0) {
			oldValue.setComment(newValue.getComment());
		}
		if (newValue.getAreaType() != null
				&& newValue.getAreaType().compareTo(oldValue.getAreaType()) != 0) {
			oldValue.setAreaType(newValue.getAreaType());
		}
		if (newValue.getRegistrationNumber() != null
				&& newValue.getRegistrationNumber().compareTo(
						oldValue.getRegistrationNumber()) != 0) {
			oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
		}
		if (newValue.getNearestTopicName() != null
				&& newValue.getNearestTopicName().compareTo(
						oldValue.getNearestTopicName()) != 0) {
			oldValue.setNearestTopicName(newValue.getNearestTopicName());
		}
		if (newValue.getUrl() != null
				&& newValue.getUrl().compareTo(oldValue.getUrl()) != 0) {
			oldValue.setUrl(newValue.getUrl());
		}
		if (newValue.getTimeZone() != null
				&& newValue.getTimeZone().compareTo(oldValue.getTimeZone()) != 0) {
			oldValue.setTimeZone(newValue.getTimeZone());
		}
		if (newValue.getFareCode() != null
				&& newValue.getFareCode().compareTo(oldValue.getFareCode()) != 0) {
			oldValue.setFareCode(newValue.getFareCode());
		}
		if (newValue.getLiftAvailable() != null
				&& newValue.getLiftAvailable().compareTo(
						oldValue.getLiftAvailable()) != 0) {
			oldValue.setLiftAvailable(newValue.getLiftAvailable());
		}
		if (newValue.getMobilityRestrictedSuitable() != null
				&& newValue.getMobilityRestrictedSuitable().compareTo(
						oldValue.getMobilityRestrictedSuitable()) != 0) {
			oldValue.setMobilityRestrictedSuitable(newValue
					.getMobilityRestrictedSuitable());
		}
		if (newValue.getStairsAvailable() != null
				&& newValue.getStairsAvailable().compareTo(
						oldValue.getStairsAvailable()) != 0) {
			oldValue.setStairsAvailable(newValue.getStairsAvailable());
		}
		if (newValue.getIntUserNeeds() != null
				&& newValue.getIntUserNeeds().compareTo(
						oldValue.getIntUserNeeds()) != 0) {
			oldValue.setIntUserNeeds(newValue.getIntUserNeeds());
		}

		if (newValue.getLongitude() != null
				&& newValue.getLongitude().compareTo(oldValue.getLongitude()) != 0) {
			oldValue.setLongitude(newValue.getLongitude());
		}
		if (newValue.getLatitude() != null
				&& newValue.getLatitude().compareTo(oldValue.getLatitude()) != 0) {
			oldValue.setLatitude(newValue.getLatitude());
		}
		if (newValue.getLongLatType() != null
				&& newValue.getLongLatType().compareTo(
						oldValue.getLongLatType()) != 0) {
			oldValue.setLongLatType(newValue.getLongLatType());
		}
		if (newValue.getX() != null
				&& newValue.getX().compareTo(oldValue.getX()) != 0) {
			oldValue.setX(newValue.getX());
		}
		if (newValue.getY() != null
				&& newValue.getY().compareTo(oldValue.getY()) != 0) {
			oldValue.setY(newValue.getY());
		}
		if (newValue.getProjectionType() != null
				&& newValue.getProjectionType().compareTo(
						oldValue.getProjectionType()) != 0) {
			oldValue.setProjectionType(newValue.getProjectionType());
		}
		if (newValue.getCountryCode() != null
				&& newValue.getCountryCode().compareTo(
						oldValue.getCountryCode()) != 0) {
			oldValue.setCountryCode(newValue.getCountryCode());
		}
		if (newValue.getZipCode() != null
				&& newValue.getZipCode().compareTo(oldValue.getZipCode()) != 0) {
			oldValue.setZipCode(newValue.getZipCode());
		}
		if (newValue.getCityName() != null
				&& newValue.getCityName().compareTo(oldValue.getCityName()) != 0) {
			oldValue.setCityName(newValue.getCityName());
		}
		if (newValue.getStreetName() != null
				&& newValue.getStreetName().compareTo(oldValue.getStreetName()) != 0) {
			oldValue.setStreetName(newValue.getStreetName());
		}

		// StopArea Parent
		if (newValue.getParent() == null) {
			oldValue.setParent(null);
		} else {
			StopArea stopArea = stopAreaDAO.findByObjectId(newValue.getParent()
					.getObjectId());
			if (stopArea == null) {
				stopArea = new StopArea();
				stopArea.setObjectId(newValue.getParent().getObjectId());
				stopAreaDAO.create(stopArea);
			}
			Updater<StopArea> stopAreaUpdater = UpdaterFactory
					.create(StopAreaUpdater.class.getName());
			stopAreaUpdater.update(null, oldValue.getParent(),
					newValue.getParent());
			oldValue.setParent(stopArea);
		}

		// AccessPoint
		Collection<AccessPoint> addedAccessPoint = CollectionUtils.substract(
				newValue.getAccessPoints(), oldValue.getAccessPoints(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (AccessPoint item : addedAccessPoint) {
			AccessPoint accessPoint = accessPointDAO.findByObjectId(item
					.getObjectId());
			if (accessPoint == null) {
				accessPoint = new AccessPoint();
				accessPoint.setObjectId(item.getObjectId());
				accessPointDAO.create(accessPoint);
			}
			accessPoint.setContainedIn(oldValue);
		}

		Updater<AccessPoint> accessPointUpdater = UpdaterFactory
				.create(AccessPointUpdater.class.getName());
		Collection<Pair<AccessPoint, AccessPoint>> modifiedAccessPoint = CollectionUtils
				.intersection(oldValue.getAccessPoints(),
						newValue.getAccessPoints(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<AccessPoint, AccessPoint> pair : modifiedAccessPoint) {
			accessPointUpdater.update(null, pair.getLeft(), pair.getRight());
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
		for (AccessLink item : addedAccessLink) {
			AccessLink accessLink = accessLinkDAO.findByObjectId(item
					.getObjectId());
			if (accessLink == null) {
				accessLink = new AccessLink();
				accessLink.setObjectId(item.getObjectId());
				accessLinkDAO.create(accessLink);
			}
			accessLink.setStopArea(oldValue);
		}

		Updater<AccessLink> accessLinkUpdater = UpdaterFactory
				.create(AccessLinkUpdater.class.getName());
		Collection<Pair<AccessLink, AccessLink>> modifiedAccessLink = CollectionUtils
				.intersection(oldValue.getAccessLinks(),
						newValue.getAccessLinks(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<AccessLink, AccessLink> pair : modifiedAccessLink) {
			accessLinkUpdater.update(null, pair.getLeft(), pair.getRight());
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
		for (ConnectionLink item : addedStartOfLink) {
			ConnectionLink connectionLink = connectionLinkDAO
					.findByObjectId(item.getObjectId());
			if (connectionLink == null) {
				connectionLink = new ConnectionLink();
				connectionLink.setObjectId(item.getObjectId());
				connectionLinkDAO.create(connectionLink);
			}
			connectionLink.setStartOfLink(oldValue);
		}

		Updater<ConnectionLink> connectionLinkUpdater = UpdaterFactory
				.create(ConnectionLinkUpdater.class.getName());
		Collection<Pair<ConnectionLink, ConnectionLink>> modifiedStartOfLink = CollectionUtils
				.intersection(oldValue.getConnectionStartLinks(),
						newValue.getConnectionStartLinks(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<ConnectionLink, ConnectionLink> pair : modifiedStartOfLink) {
			connectionLinkUpdater.update(null, pair.getLeft(), pair.getRight());

		}

		// EndOfLink
		Collection<ConnectionLink> addedEndOfLink = CollectionUtils.substract(
				newValue.getConnectionEndLinks(),
				oldValue.getConnectionEndLinks(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (ConnectionLink item : addedEndOfLink) {
			ConnectionLink connectionLink = connectionLinkDAO
					.findByObjectId(item.getObjectId());
			if (connectionLink == null) {
				connectionLink = new ConnectionLink();
				connectionLink.setObjectId(item.getObjectId());
				connectionLinkDAO.create(connectionLink);
			}
			connectionLink.setEndOfLink(oldValue);
		}

		connectionLinkUpdater = UpdaterFactory
				.create(ConnectionLinkUpdater.class.getName());
		Collection<Pair<ConnectionLink, ConnectionLink>> modifiedEndOfLink = CollectionUtils
				.intersection(oldValue.getConnectionEndLinks(),
						newValue.getConnectionEndLinks(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<ConnectionLink, ConnectionLink> pair : modifiedEndOfLink) {
			connectionLinkUpdater.update(null, pair.getLeft(), pair.getRight());
		}

		// TODO list routing_constraints_lines (routingConstraintLines)
		// TODO list stop_areas_stop_areas (routingConstraintAreas)

	}

	static {
		UpdaterFactory.register(StopAreaUpdater.class.getName(),
				new UpdaterFactory() {
					private StopAreaUpdater INSTANCE = new StopAreaUpdater();

					@Override
					protected Updater<StopArea> create() {
						return INSTANCE;
					}
				});
	}

}
