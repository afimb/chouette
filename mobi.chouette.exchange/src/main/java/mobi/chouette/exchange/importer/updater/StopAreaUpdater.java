package mobi.chouette.exchange.importer.updater;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.CollectionUtil;
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

@Stateless(name = StopAreaUpdater.BEAN_NAME)
@Log4j
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
	public void update(Context context, StopArea oldValue, StopArea newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		Referential cache = (Referential) context.get(CACHE);
		Referential referential = (Referential) context.get(REFERENTIAL);

		if (newValue.getAreaType() == null)
		{
			log.error("stoparea without mandatory areatype "+newValue.getObjectId());
			throw new IllegalArgumentException("area type null");
		}

		if (oldValue.isDetached())
		{
				oldValue.setObjectId(newValue.getObjectId());
				oldValue.setObjectVersion(newValue.getObjectVersion());
				oldValue.setCreationTime(newValue.getCreationTime());
				oldValue.setCreatorId(newValue.getCreatorId());
				oldValue.setName(newValue.getName());
				oldValue.setComment(newValue.getComment());
				oldValue.setAreaType(newValue.getAreaType());
				oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
				oldValue.setNearestTopicName(newValue.getNearestTopicName());
				oldValue.setUrl(newValue.getUrl());
				oldValue.setTimeZone(newValue.getTimeZone());
				oldValue.setFareCode(newValue.getFareCode());
				oldValue.setLiftAvailable(newValue.getLiftAvailable());
				oldValue.setMobilityRestrictedSuitable(newValue.getMobilityRestrictedSuitable());
				oldValue.setStairsAvailable(newValue.getStairsAvailable());
				oldValue.setIntUserNeeds(newValue.getIntUserNeeds());
				oldValue.setLongitude(newValue.getLongitude());
				oldValue.setLatitude(newValue.getLatitude());
				oldValue.setLongLatType(newValue.getLongLatType());
				oldValue.setCountryCode(newValue.getCountryCode());
				oldValue.setZipCode(newValue.getZipCode());
				oldValue.setCityName(newValue.getCityName());
				oldValue.setStreetName(newValue.getStreetName());			
		}
		else
		{
		if (newValue.getObjectId() != null && !newValue.getObjectId().equals(oldValue.getObjectId())) {
			oldValue.setObjectId(newValue.getObjectId());
		}
		if (newValue.getObjectVersion() != null && !newValue.getObjectVersion().equals(oldValue.getObjectVersion())) {
			oldValue.setObjectVersion(newValue.getObjectVersion());
		}
		if (newValue.getCreationTime() != null && !newValue.getCreationTime().equals(oldValue.getCreationTime())) {
			oldValue.setCreationTime(newValue.getCreationTime());
		}
		if (newValue.getCreatorId() != null && !newValue.getCreatorId().equals(oldValue.getCreatorId())) {
			oldValue.setCreatorId(newValue.getCreatorId());
		}
		if (newValue.getName() != null && !newValue.getName().equals(oldValue.getName())) {
			oldValue.setName(newValue.getName());
		}
		if (newValue.getComment() != null && !newValue.getComment().equals(oldValue.getComment())) {
			oldValue.setComment(newValue.getComment());
		}
		if (newValue.getAreaType() != null && !newValue.getAreaType().equals(oldValue.getAreaType())) {
			oldValue.setAreaType(newValue.getAreaType());
		}
		if (newValue.getRegistrationNumber() != null
				&& !newValue.getRegistrationNumber().equals(oldValue.getRegistrationNumber())) {
			oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
		}
		if (newValue.getNearestTopicName() != null
				&& !newValue.getNearestTopicName().equals(oldValue.getNearestTopicName())) {
			oldValue.setNearestTopicName(newValue.getNearestTopicName());
		}
		if (newValue.getUrl() != null && !newValue.getUrl().equals(oldValue.getUrl())) {
			oldValue.setUrl(newValue.getUrl());
		}
		if (newValue.getTimeZone() != null && !newValue.getTimeZone().equals(oldValue.getTimeZone())) {
			oldValue.setTimeZone(newValue.getTimeZone());
		}
		if (newValue.getFareCode() != null && !newValue.getFareCode().equals(oldValue.getFareCode())) {
			oldValue.setFareCode(newValue.getFareCode());
		}
		if (newValue.getLiftAvailable() != null && !newValue.getLiftAvailable().equals(oldValue.getLiftAvailable())) {
			oldValue.setLiftAvailable(newValue.getLiftAvailable());
		}
		if (newValue.getMobilityRestrictedSuitable() != null
				&& !newValue.getMobilityRestrictedSuitable().equals(oldValue.getMobilityRestrictedSuitable())) {
			oldValue.setMobilityRestrictedSuitable(newValue.getMobilityRestrictedSuitable());
		}
		if (newValue.getStairsAvailable() != null
				&& !newValue.getStairsAvailable().equals(oldValue.getStairsAvailable())) {
			oldValue.setStairsAvailable(newValue.getStairsAvailable());
		}
		if (newValue.getIntUserNeeds() != null && !newValue.getIntUserNeeds().equals(oldValue.getIntUserNeeds())) {
			oldValue.setIntUserNeeds(newValue.getIntUserNeeds());
		}

		if (newValue.getLongitude() != null && !newValue.getLongitude().equals(oldValue.getLongitude())) {
			oldValue.setLongitude(newValue.getLongitude());
		}
		if (newValue.getLatitude() != null && !newValue.getLatitude().equals(oldValue.getLatitude())) {
			oldValue.setLatitude(newValue.getLatitude());
		}
		if (newValue.getLongLatType() != null && !newValue.getLongLatType().equals(oldValue.getLongLatType())) {
			oldValue.setLongLatType(newValue.getLongLatType());
		}
		if (newValue.getCountryCode() != null && !newValue.getCountryCode().equals(oldValue.getCountryCode())) {
			oldValue.setCountryCode(newValue.getCountryCode());
		}
		if (newValue.getZipCode() != null && !newValue.getZipCode().equals(oldValue.getZipCode())) {
			oldValue.setZipCode(newValue.getZipCode());
		}
		if (newValue.getCityName() != null && !newValue.getCityName().equals(oldValue.getCityName())) {
			oldValue.setCityName(newValue.getCityName());
		}
		if (newValue.getStreetName() != null && !newValue.getStreetName().equals(oldValue.getStreetName())) {
			oldValue.setStreetName(newValue.getStreetName());
		}
		}
		// StopArea Parent
		if (newValue.getParent() == null) {
			oldValue.setParent(null);
		} else {
			String objectId = newValue.getParent().getObjectId();
			StopArea stopArea = cache.getStopAreas().get(objectId);
			if (stopArea == null ) {
				stopArea = stopAreaDAO.findByObjectId(objectId);
				if (stopArea != null) {
					cache.getStopAreas().put(objectId, stopArea);
				}
			}

			if (stopArea == null) {
				stopArea = ObjectFactory.getStopArea(cache, objectId);
			}
			oldValue.setParent(stopArea);
			stopAreaUpdater.update(context, oldValue.getParent(), newValue.getParent());
		}

		// AccessPoint
		Collection<AccessPoint> addedAccessPoint = CollectionUtil.substract(newValue.getAccessPoints(),
				oldValue.getAccessPoints(), NeptuneIdentifiedObjectComparator.INSTANCE);

		List<AccessPoint> accessPoints = null;
		for (AccessPoint item : addedAccessPoint) {
			AccessPoint accessPoint = cache.getAccessPoints().get(item.getObjectId());
			if (accessPoint == null) {
				if (accessPoints == null) {
					accessPoints = accessPointDAO.findByObjectId(UpdaterUtils.getObjectIds(addedAccessPoint));
					for (AccessPoint object : accessPoints) {
						cache.getAccessPoints().put(object.getObjectId(), object);
					}
				}
				accessPoint = cache.getAccessPoints().get(item.getObjectId());
			}

			if (accessPoint == null) {
				accessPoint = ObjectFactory.getAccessPoint(cache, item.getObjectId());
			}
			accessPoint.setContainedIn(oldValue);
		}

		Collection<Pair<AccessPoint, AccessPoint>> modifiedAccessPoint = CollectionUtil.intersection(
				oldValue.getAccessPoints(), newValue.getAccessPoints(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<AccessPoint, AccessPoint> pair : modifiedAccessPoint) {
			accessPointUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		// AccessLink
		Collection<AccessLink> addedAccessLink = CollectionUtil.substract(newValue.getAccessLinks(),
				oldValue.getAccessLinks(), NeptuneIdentifiedObjectComparator.INSTANCE);

		List<AccessLink> accessLinks = null;
		for (AccessLink item : addedAccessLink) {

			AccessLink accessLink = cache.getAccessLinks().get(item.getObjectId());
			if (accessLink == null) {
				if (accessLinks == null) {
					accessLinks = accessLinkDAO.findByObjectId(UpdaterUtils.getObjectIds(addedAccessLink));
					for (AccessLink object : accessLinks) {
						cache.getAccessLinks().put(object.getObjectId(), object);
					}
				}
				accessLink = cache.getAccessLinks().get(item.getObjectId());
			}

			if (accessLink == null) {
				accessLink = ObjectFactory.getAccessLink(cache, item.getObjectId());
			}
			accessLink.setStopArea(oldValue);
		}

		Collection<Pair<AccessLink, AccessLink>> modifiedAccessLink = CollectionUtil.intersection(
				oldValue.getAccessLinks(), newValue.getAccessLinks(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<AccessLink, AccessLink> pair : modifiedAccessLink) {
			accessLinkUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		if (!context.containsKey(AREA_BLOC))
		{
		// StartOfLink
		Collection<ConnectionLink> addedStartOfLink = CollectionUtil.substract(newValue.getConnectionStartLinks(),
				oldValue.getConnectionStartLinks(), NeptuneIdentifiedObjectComparator.INSTANCE);

		for (ConnectionLink item : addedStartOfLink) {

			ConnectionLink startOfLink = cache.getConnectionLinks().get(item.getObjectId());
			if (startOfLink == null) {
				startOfLink = ObjectFactory.getConnectionLink(cache, item.getObjectId());
			}
			StopArea endOfLinkArea = cache.getStopAreas().get(item.getEndOfLink().getObjectId()) ;
			if (endOfLinkArea == null)
				endOfLinkArea = stopAreaDAO.findByObjectId(item.getEndOfLink().getObjectId());
			else 
			{
				StopArea localArea = referential.getSharedStopAreas().get(endOfLinkArea.getObjectId());
				if (!localArea.isSaved()) 
					endOfLinkArea = null; // ignored if not already saved
			}
		    if (endOfLinkArea != null)
		    {
				startOfLink.setStartOfLink(oldValue);
				startOfLink.setEndOfLink(endOfLinkArea);
		    }
			
		}

		Collection<Pair<ConnectionLink, ConnectionLink>> modifiedStartOfLink = CollectionUtil.intersection(
				oldValue.getConnectionStartLinks(), newValue.getConnectionStartLinks(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<ConnectionLink, ConnectionLink> pair : modifiedStartOfLink) {
			connectionLinkUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		// EndOfLink
		Collection<ConnectionLink> addedEndOfLink = CollectionUtil.substract(newValue.getConnectionEndLinks(),
				oldValue.getConnectionEndLinks(), NeptuneIdentifiedObjectComparator.INSTANCE);

		for (ConnectionLink item : addedEndOfLink) {
			ConnectionLink endOfLink = cache.getConnectionLinks().get(item.getObjectId());
			if (endOfLink == null) {
				endOfLink = ObjectFactory.getConnectionLink(cache, item.getObjectId());
			}
			StopArea startOfLinkArea = cache.getStopAreas().get(item.getStartOfLink().getObjectId()) ;
			if (startOfLinkArea == null)
				startOfLinkArea = stopAreaDAO.findByObjectId(item.getStartOfLink().getObjectId());
			else 
			{
				StopArea localArea = referential.getSharedStopAreas().get(startOfLinkArea.getObjectId());
				if (!localArea.isSaved()) 
					startOfLinkArea = null; // ignored if not already saved
			}
				
		    if (startOfLinkArea != null)
		    {
		    	endOfLink.setStartOfLink(startOfLinkArea);
		    	endOfLink.setEndOfLink(oldValue);
		    }
		}

		Collection<Pair<ConnectionLink, ConnectionLink>> modifiedEndOfLink = CollectionUtil.intersection(
				oldValue.getConnectionEndLinks(), newValue.getConnectionEndLinks(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<ConnectionLink, ConnectionLink> pair : modifiedEndOfLink) {
			connectionLinkUpdater.update(context, pair.getLeft(), pair.getRight());
		}
		}

		// TODO list routing_constraints_lines (routingConstraintLines)
		// TODO list stop_areas_stop_areas (routingConstraintAreas)
		Collection<StopArea> addedStopAreas = CollectionUtil.substract(newValue.getRoutingConstraintAreas(),
				oldValue.getRoutingConstraintAreas(), NeptuneIdentifiedObjectComparator.INSTANCE);

		List<StopArea> stopAreas = null;
		for (StopArea item : addedStopAreas) {

			StopArea area = cache.getStopAreas().get(item.getObjectId());
			if (area == null) {
				if (stopAreas == null) {
					stopAreas = stopAreaDAO.findByObjectId(UpdaterUtils.getObjectIds(addedStopAreas));
					for (StopArea object : addedStopAreas) {
						cache.getStopAreas().put(object.getObjectId(), object);
					}
				}
				area = cache.getStopAreas().get(item.getObjectId());
			}

			if (area == null) {
				area = ObjectFactory.getStopArea(cache, item.getObjectId());
			}
			if (!area.isDetached() || area.isFilled())
				oldValue.getRoutingConstraintAreas().add(area);
		}

		Collection<Pair<StopArea, StopArea>> modifiedStopArea = CollectionUtil.intersection(
				oldValue.getRoutingConstraintAreas(), newValue.getRoutingConstraintAreas(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<StopArea, StopArea> pair : modifiedStopArea) {
			stopAreaUpdater.update(context, pair.getLeft(), pair.getRight());
		}

	}
}
