package mobi.chouette.exchange.importer.updater;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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

@Log4j
@Stateless(name = StopAreaUpdater.BEAN_NAME)
public class StopAreaUpdater implements Updater<StopArea> {

	public static final String BEAN_NAME = "StopAreaUpdater";

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

		InitialContext initialContext = (InitialContext) context
				.get(INITIAL_CONTEXT);

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

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
			StopArea stopArea = stopAreaDAO.findByObjectId(newValue.getParent()
					.getObjectId());
			if (stopArea == null) {
				stopArea = new StopArea();
				stopArea.setObjectId(newValue.getParent().getObjectId());
				stopAreaDAO.create(stopArea);
			}
			Updater<StopArea> stopAreaUpdater = UpdaterFactory.create(
					initialContext, StopAreaUpdater.class.getName());
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

		Updater<AccessPoint> accessPointUpdater = UpdaterFactory.create(
				initialContext, AccessPointUpdater.class.getName());
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

		Updater<AccessLink> accessLinkUpdater = UpdaterFactory.create(
				initialContext, AccessLinkUpdater.class.getName());
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

		Updater<ConnectionLink> connectionLinkUpdater = UpdaterFactory.create(
				initialContext, ConnectionLinkUpdater.class.getName());
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

		connectionLinkUpdater = UpdaterFactory.create(initialContext,
				ConnectionLinkUpdater.class.getName());
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
		UpdaterFactory.register(LineUpdater.class.getName(),
				new UpdaterFactory() {

					@Override
					protected <T> Updater<T> create(InitialContext context) {
						Updater result = null;
						try {
							result = (Updater) context
									.lookup("java:app/mobi.chouette.exchange/"
											+ BEAN_NAME);
						} catch (NamingException e) {
							log.error(e);
						}
						return result;
					}
				});
	}

}
