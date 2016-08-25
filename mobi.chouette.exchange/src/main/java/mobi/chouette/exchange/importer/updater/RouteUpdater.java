package mobi.chouette.exchange.importer.updater;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.CollectionUtil;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.JourneyPatternDAO;
import mobi.chouette.dao.RouteDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.NeptuneUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Stateless(name = RouteUpdater.BEAN_NAME)
public class RouteUpdater implements Updater<Route> {

	public static final String BEAN_NAME = "RouteUpdater";

	@EJB
	private RouteDAO routeDAO;

	@EJB
	private StopPointDAO stopPointDAO;

	@EJB(beanName = StopPointUpdater.BEAN_NAME)
	private Updater<StopPoint> stopPointUpdater;

	@EJB
	private JourneyPatternDAO journeyPatternDAO;

	@EJB(beanName = JourneyPatternUpdater.BEAN_NAME)
	private Updater<JourneyPattern> journeyPatternUpdater;

	@Override
	public void update(Context context, Route oldValue, Route newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

//		Monitor monitor = MonitorFactory.start(BEAN_NAME);
		Referential cache = (Referential) context.get(CACHE);
		
		// Database test init
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, DATABASE_STOP_POINT_1, "E");
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
				
		if (oldValue.isDetached()) {
			// object does not exist in database
			oldValue.setObjectId(newValue.getObjectId());
			oldValue.setObjectVersion(newValue.getObjectVersion());
			oldValue.setCreationTime(newValue.getCreationTime());
			oldValue.setCreatorId(newValue.getCreatorId());
			oldValue.setName(newValue.getName());
			oldValue.setComment(newValue.getComment());
			oldValue.setPublishedName(newValue.getPublishedName());
			oldValue.setNumber(newValue.getNumber());
			oldValue.setDirection(newValue.getDirection());
			oldValue.setWayBack(newValue.getWayBack());
			oldValue.setDetached(false);
		} else {

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
			if (newValue.getPublishedName() != null && !newValue.getPublishedName().equals(oldValue.getPublishedName())) {
				oldValue.setPublishedName(newValue.getPublishedName());
			}
			if (newValue.getNumber() != null && !newValue.getNumber().equals(oldValue.getNumber())) {
				oldValue.setNumber(newValue.getNumber());
			}
			if (newValue.getDirection() != null && !newValue.getDirection().equals(oldValue.getDirection())) {
				oldValue.setDirection(newValue.getDirection());
			}
			if (newValue.getWayBack() != null && !newValue.getWayBack().equals(oldValue.getWayBack())) {
				oldValue.setWayBack(newValue.getWayBack());
			}
		}

		// OppositeRoute
		if (newValue.getOppositeRoute() != null) {

			String objectId = newValue.getOppositeRoute().getObjectId();
			Route opposite = cache.getRoutes().get(objectId);
			if (opposite == null) {
				opposite = routeDAO.findByObjectId(objectId);
				if (opposite != null) {
					cache.getRoutes().put(objectId, opposite);
				}
			}
			if (opposite != null) {
				oldValue.setOppositeRoute(opposite);
			}
		}

		// StopPoint
		Collection<StopPoint> addedStopPoint = CollectionUtil.substract(newValue.getStopPoints(),
				oldValue.getStopPoints(), NeptuneIdentifiedObjectComparator.INSTANCE);

		List<StopPoint> stopPoints = null;
		for (StopPoint item : addedStopPoint) {

			StopPoint stopPoint = cache.getStopPoints().get(item.getObjectId());
			if (stopPoint == null) {
				if (stopPoints == null) {
					stopPoints = stopPointDAO.findByObjectId(UpdaterUtils.getObjectIds(addedStopPoint));
					for (StopPoint object : stopPoints) {
						cache.getStopPoints().put(object.getObjectId(), object);
					}
				}
				stopPoint = cache.getStopPoints().get(item.getObjectId());
			}
			

			if (stopPoint == null) {
				stopPoint = ObjectFactory.getStopPoint(cache, item.getObjectId());
			}
			// If new stop point doesn't belong to route, we add temporarly it to the route and check if old stopPoint has same route as new stopPoint
			if(stopPoint.getRoute() != null) {
				twoDatabaseStopPointOneTest(validationReporter, context, stopPoint, item, data);
			} else {
				stopPoint.setRoute(oldValue);
			}
		}

		Collection<Pair<StopPoint, StopPoint>> modifiedStopPoint = CollectionUtil.intersection(
				oldValue.getStopPoints(), newValue.getStopPoints(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<StopPoint, StopPoint> pair : modifiedStopPoint) {
			stopPointUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		Collection<StopPoint> removedStopPoint = CollectionUtil.substract(oldValue.getStopPoints(),
				newValue.getStopPoints(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (StopPoint stopPoint : removedStopPoint) {
			stopPoint.setRoute(null);
			stopPointDAO.delete(stopPoint);
		}

		// JourneyPattern
		Collection<JourneyPattern> addedJourneyPattern = CollectionUtil.substract(newValue.getJourneyPatterns(),
				oldValue.getJourneyPatterns(), NeptuneIdentifiedObjectComparator.INSTANCE);

		List<JourneyPattern> journeyPatterns = null;
		for (JourneyPattern item : addedJourneyPattern) {

			JourneyPattern journeyPattern = cache.getJourneyPatterns().get(item.getObjectId());
			if (journeyPattern == null) {
				if (journeyPatterns == null) {
					journeyPatterns = journeyPatternDAO.findByObjectId(UpdaterUtils.getObjectIds(addedJourneyPattern));
					for (JourneyPattern object : journeyPatterns) {
						cache.getJourneyPatterns().put(object.getObjectId(), object);
					}
				}
				journeyPattern = cache.getJourneyPatterns().get(item.getObjectId());
			}

			if (journeyPattern == null) {
				journeyPattern = ObjectFactory.getJourneyPattern(cache, item.getObjectId());
			}
			if(journeyPattern.getRoute() != null) {
				twoDatabaseJourneyPatternOneTest(validationReporter, context, journeyPattern, item, data);
			} else {
				journeyPattern.setRoute(oldValue);
			}
			
		}

		Collection<Pair<JourneyPattern, JourneyPattern>> modifiedJourneyPattern = CollectionUtil.intersection(
				oldValue.getJourneyPatterns(), newValue.getJourneyPatterns(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<JourneyPattern, JourneyPattern> pair : modifiedJourneyPattern) {
			journeyPatternUpdater.update(context, pair.getLeft(), pair.getRight());
		}
//		monitor.stop();
	}
	
	/**
	 * Test 2-DATABASE-JourneyPattern-1 : // If new journey pattern doesn't belong to route, we add temporarly it to the route and check if old journey pattern has same route as new journey pattern
	 * @param validationReporter
	 * @param context
	 * @param oldRouteSection
	 * @param newRouteSection
	 */
	private void twoDatabaseJourneyPatternOneTest(ValidationReporter validationReporter, Context context, JourneyPattern oldValue, JourneyPattern newValue, ValidationData data) {
		if(!NeptuneUtil.sameValue(oldValue.getRoute(), newValue.getRoute()))
			validationReporter.addCheckPointReportError(context, DATABASE_JOURNEY_PATTERN_1, data.getDataLocations().get(newValue.getObjectId()));
		else
			validationReporter.reportSuccess(context, DATABASE_JOURNEY_PATTERN_1);
	}
	
	/**
	 * Test 2-DATABASE-StopPoint-1 : Check if old stop point has same route as new stop point
	 * @param validationReporter
	 * @param context
	 * @param oldSp
	 * @param newSp
	 */
	private void twoDatabaseStopPointOneTest(ValidationReporter validationReporter, Context context, StopPoint oldSp, StopPoint newSp, ValidationData data) {
		if(!NeptuneUtil.sameValue(oldSp.getRoute(), newSp.getRoute()))
			validationReporter.addCheckPointReportError(context, DATABASE_STOP_POINT_1, data.getDataLocations().get(newSp.getObjectId()));
		else
			validationReporter.reportSuccess(context, DATABASE_STOP_POINT_1);
	}
}
