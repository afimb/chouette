package mobi.chouette.exchange.importer.updater;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.CollectionUtil;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.CompanyDAO;
import mobi.chouette.dao.GroupOfLineDAO;
import mobi.chouette.dao.NetworkDAO;
import mobi.chouette.dao.RouteDAO;
import mobi.chouette.dao.RoutingConstraintDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.ChouetteId;
import mobi.chouette.model.Company;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.RoutingConstraint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.NeptuneUtil;
import mobi.chouette.exchange.ChouetteIdObjectFactory;
import mobi.chouette.model.util.Referential;

@Stateless(name = LineUpdater.BEAN_NAME)
public class LineUpdater implements Updater<Line> {

	public static final String BEAN_NAME = "LineUpdater";

	@EJB
	private NetworkDAO ptNetworkDAO;

	@EJB(beanName = PTNetworkUpdater.BEAN_NAME)
	private Updater<Network> ptNetworkUpdater;

	@EJB
	private CompanyDAO companyDAO;

	@EJB(beanName = CompanyUpdater.BEAN_NAME)
	private Updater<Company> companyUpdater;

	@EJB
	private GroupOfLineDAO groupOfLineDAO;

	@EJB(beanName = GroupOfLineUpdater.BEAN_NAME)
	private Updater<GroupOfLine> groupOfLineUpdater;

	@EJB
	private RouteDAO routeDAO;

	@EJB(beanName = RouteUpdater.BEAN_NAME)
	private Updater<Route> routeUpdater;

	@EJB
	private RoutingConstraintDAO routingConstraintDAO;

	@EJB(beanName = RoutingConstraintUpdater.BEAN_NAME)
	private Updater<RoutingConstraint> routingConstraintUpdater;

	@Override
	public void update(Context context, Line oldValue, Line newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);
//		Monitor monitor = MonitorFactory.start(BEAN_NAME);
		Referential cache = (Referential) context.get(CACHE);
		
		// Database test init
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, "2-DATABASE-", "Line", 2, "W", "W");
		validationReporter.addItemToValidationReport(context, DATABASE_ROUTE_1, "E");
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		
		if (oldValue.isDetached()) {
			// object does not exist in database
			oldValue.getChouetteId().setObjectId(newValue.getChouetteId().getObjectId());
			oldValue.setObjectVersion(newValue.getObjectVersion());
			oldValue.setCreationTime(newValue.getCreationTime());
			oldValue.setCreatorId(newValue.getCreatorId());
			oldValue.setName(newValue.getName());
			oldValue.setComment(newValue.getComment());
			oldValue.setNumber(newValue.getNumber());
			oldValue.setPublishedName(newValue.getPublishedName());
			oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
			oldValue.setTransportModeName(newValue.getTransportModeName());
			oldValue.setMobilityRestrictedSuitable(newValue.getMobilityRestrictedSuitable());
			oldValue.setIntUserNeeds(newValue.getIntUserNeeds());
			oldValue.setUrl(newValue.getUrl());
			oldValue.setColor(newValue.getColor());
			oldValue.setTextColor(newValue.getTextColor());
			oldValue.setDetached(false);
		} else {
			twoDatabaseLineOneTest(validationReporter, context, oldValue, newValue, data);
			twoDatabaseLineTwoTest(validationReporter, context, oldValue, newValue, data);

			if (newValue.getChouetteId().getObjectId() != null && !newValue.getChouetteId().getObjectId().equals(oldValue.getChouetteId().getObjectId())) {
				oldValue.getChouetteId().setObjectId(newValue.getChouetteId().getObjectId());
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
			if (newValue.getNumber() != null && !newValue.getNumber().equals(oldValue.getNumber())) {
				oldValue.setNumber(newValue.getNumber());
			}
			if (newValue.getPublishedName() != null && !newValue.getPublishedName().equals(oldValue.getPublishedName())) {
				oldValue.setPublishedName(newValue.getPublishedName());
			}
			if (newValue.getRegistrationNumber() != null
					&& !newValue.getRegistrationNumber().equals(oldValue.getRegistrationNumber())) {
				oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
			}
			if (newValue.getTransportModeName() != null
					&& !newValue.getTransportModeName().equals(oldValue.getTransportModeName())) {
				oldValue.setTransportModeName(newValue.getTransportModeName());
			}
			if (newValue.getMobilityRestrictedSuitable() != null
					&& !newValue.getMobilityRestrictedSuitable().equals(oldValue.getMobilityRestrictedSuitable())) {
				oldValue.setMobilityRestrictedSuitable(newValue.getMobilityRestrictedSuitable());
			}
			if (newValue.getIntUserNeeds() != null && !newValue.getIntUserNeeds().equals(oldValue.getIntUserNeeds())) {
				oldValue.setIntUserNeeds(newValue.getIntUserNeeds());
			}
			if (newValue.getUrl() != null && !newValue.getUrl().equals(oldValue.getUrl())) {
				oldValue.setUrl(newValue.getUrl());
			}
			if (newValue.getColor() != null && !newValue.getColor().equals(oldValue.getColor())) {
				oldValue.setColor(newValue.getColor());
			}
			if (newValue.getTextColor() != null && !newValue.getTextColor().equals(oldValue.getTextColor())) {
				oldValue.setTextColor(newValue.getTextColor());
			}
		}
		
		// PTNetwork
		
		if (newValue.getNetwork() == null) {
			oldValue.setNetwork(null);
		} else {
			String codeSpace = newValue.getNetwork().getChouetteId().getCodeSpace();
			String objectId = newValue.getNetwork().getChouetteId().getObjectId();
			ChouetteId chouetteId = newValue.getNetwork().getChouetteId();
			Network ptNetwork = cache.getPtNetworks().get(objectId);
			if (ptNetwork == null) {
				ptNetwork = ptNetworkDAO.findByChouetteId(codeSpace, objectId);
				if (ptNetwork != null) {
					cache.getPtNetworks().put(chouetteId, ptNetwork);
				}
			}
			if (ptNetwork == null) {
				ptNetwork = ChouetteIdObjectFactory.getPTNetwork(cache, chouetteId);
			}
			oldValue.setNetwork(ptNetwork);
			ptNetworkUpdater.update(context, oldValue.getNetwork(), newValue.getNetwork());
		}

		// Company
		
		if (newValue.getCompany() == null) {
			oldValue.setCompany(null);
		} else {
			String codeSpace = newValue.getCompany().getChouetteId().getCodeSpace();
			String objectId = newValue.getCompany().getChouetteId().getObjectId();
			ChouetteId chouetteId = newValue.getCompany().getChouetteId();
			Company company = cache.getCompanies().get(objectId);
			if (company == null) {
				company = companyDAO.findByChouetteId(codeSpace, objectId);
				if (company != null) {
					cache.getCompanies().put(chouetteId, company);
				}
			}
			if (company == null) {
				company = ChouetteIdObjectFactory.getCompany(cache, chouetteId);
			}
			oldValue.setCompany(company);
			
			companyUpdater.update(context, oldValue.getCompany(), newValue.getCompany());
		}

		// GroupOfLine
		Collection<GroupOfLine> addedGroupOfLine = CollectionUtil.substract(newValue.getGroupOfLines(),
				oldValue.getGroupOfLines(), NeptuneIdentifiedObjectComparator.INSTANCE);
		List<GroupOfLine> groupOfLines = null;
		for (GroupOfLine item : addedGroupOfLine) {
			GroupOfLine groupOfLine = cache.getGroupOfLines().get(item.getChouetteId());
			if (groupOfLine == null) {
				if (groupOfLines == null) {
					String codeSpace = item.getChouetteId().getCodeSpace();
					groupOfLines = groupOfLineDAO.findByChouetteId(codeSpace, UpdaterUtils.getObjectIds(addedGroupOfLine));
					for (GroupOfLine object : groupOfLines) {
						cache.getGroupOfLines().put(object.getChouetteId(), object);
					}
				}
				groupOfLine = cache.getGroupOfLines().get(item.getChouetteId().getObjectId());
			}
			if (groupOfLine == null) {
				groupOfLine = ChouetteIdObjectFactory.getGroupOfLine(cache, item.getChouetteId());
			}
			groupOfLine.addLine(oldValue);
		}

		Collection<Pair<GroupOfLine, GroupOfLine>> modifiedGroupOfLine = CollectionUtil.intersection(
				oldValue.getGroupOfLines(), newValue.getGroupOfLines(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<GroupOfLine, GroupOfLine> pair : modifiedGroupOfLine) {
			groupOfLineUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		Collection<GroupOfLine> removedGroupOfLine = CollectionUtil.substract(oldValue.getGroupOfLines(),
				newValue.getGroupOfLines(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (GroupOfLine groupOfLine : removedGroupOfLine) {
			groupOfLine.removeLine(oldValue);
		}

		// Route
		Collection<Route> addedRoute = CollectionUtil.substract(newValue.getRoutes(), oldValue.getRoutes(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		List<Route> routes = null;
		for (Route item : addedRoute) {
			Route route = cache.getRoutes().get(item.getChouetteId().getObjectId());
			if (route == null) {
				if (routes == null) {
					String codeSpace = item.getChouetteId().getCodeSpace();
					routes = routeDAO.findByChouetteId(codeSpace, UpdaterUtils.getObjectIds(addedRoute));
					for (Route object : routes) {
						cache.getRoutes().put(object.getChouetteId(), object);
					}
				}
				route = cache.getRoutes().get(item.getChouetteId().getObjectId());
			}
			if (route == null) {
				route = ChouetteIdObjectFactory.getRoute(cache, item.getChouetteId());
			}
			// If new route doesn't belong to line, we add temporarly it to the line and check if old route has same line as new route
			if(route.getLine() != null) {
				twoDatabaseRouteOneTest(validationReporter, context, route, item, data);
			} else {
				route.setLine(oldValue);
			}
			
		}

		Collection<Pair<Route, Route>> modifiedRoute = CollectionUtil.intersection(oldValue.getRoutes(),
				newValue.getRoutes(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<Route, Route> pair : modifiedRoute) {
			routeUpdater.update(context, pair.getLeft(), pair.getRight());
		}

//		// TODO stop area list (routingConstraintLines)
//		Collection<StopArea> addedRoutingConstraint = CollectionUtil.substract(newValue.getRoutingConstraints(),
//				oldValue.getRoutingConstraints(), NeptuneIdentifiedObjectComparator.INSTANCE);
//		List<StopArea> routingConstraints = null;
//		for (StopArea item : addedRoutingConstraint) {
//			StopArea routingConstraint = cache.getStopAreas().get(item.getChouetteId().getObjectId());
//			if (routingConstraint == null) {
//				if (routingConstraints == null) {
//					routingConstraints = stopAreaDAO.findByObjectId(UpdaterUtils.getObjectIds(addedRoutingConstraint));
//					for (StopArea object : routingConstraints) {
//						cache.getStopAreas().put(object.getChouetteId().getObjectId(), object);
//					}
//				}
//				routingConstraint = cache.getStopAreas().get(item.getChouetteId().getObjectId());
//			}
//			if (routingConstraint == null) {
//				routingConstraint = ChouetteIdObjectFactory.getStopArea(cache, item.getChouetteId().getObjectId());
//			}
//			oldValue.addRoutingConstraint(routingConstraint);
//		}
//
//		Collection<Pair<StopArea, StopArea>> modifiedRoutingConstraint = CollectionUtil.intersection(
//				oldValue.getRoutingConstraints(), newValue.getRoutingConstraints(),
//				NeptuneIdentifiedObjectComparator.INSTANCE);
//		for (Pair<StopArea, StopArea> pair : modifiedRoutingConstraint) {
//			stopAreaUpdater.update(context, pair.getLeft(), pair.getRight());
//		}
//
//		Collection<StopArea> removedRoutingConstraint = CollectionUtil.substract(oldValue.getRoutingConstraints(),
//				newValue.getRoutingConstraints(), NeptuneIdentifiedObjectComparator.INSTANCE);
//		for (StopArea stopArea : removedRoutingConstraint) {
//			oldValue.removeRoutingConstraint(stopArea);
//		}
		
		// TODO routing constraint list netex (routingConstraintLines)
		Collection<RoutingConstraint> addedRoutingConstraint = CollectionUtil.substract(newValue.getRoutingConstraints(),
				oldValue.getRoutingConstraints(), NeptuneIdentifiedObjectComparator.INSTANCE);
		List<RoutingConstraint> routingConstraints = null;
		for (RoutingConstraint item : addedRoutingConstraint) {
			RoutingConstraint routingConstraint = cache.getRoutingConstraints().get(item.getChouetteId().getObjectId());
			if (routingConstraint == null) {
				if (routingConstraints == null) {
					String codeSpace = item.getChouetteId().getCodeSpace();
					routingConstraints = routingConstraintDAO.findByChouetteId(codeSpace, UpdaterUtils.getObjectIds(addedRoutingConstraint));
					for (RoutingConstraint object : routingConstraints) {
						cache.getRoutingConstraints().put(object.getChouetteId(), object);
					}
				}
				routingConstraint = cache.getRoutingConstraints().get(item.getChouetteId().getObjectId());
			}
			if (routingConstraint == null) {
				routingConstraint = ChouetteIdObjectFactory.getRoutingConstraint(cache, item.getChouetteId());
			}
			oldValue.addRoutingConstraint(routingConstraint);
		}

		Collection<Pair<RoutingConstraint, RoutingConstraint>> modifiedRoutingConstraint = CollectionUtil.intersection(
				oldValue.getRoutingConstraints(), newValue.getRoutingConstraints(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<RoutingConstraint, RoutingConstraint> pair : modifiedRoutingConstraint) {
			routingConstraintUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		Collection<RoutingConstraint> removedRoutingConstraint = CollectionUtil.substract(oldValue.getRoutingConstraints(),
				newValue.getRoutingConstraints(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (RoutingConstraint routingConstraint : removedRoutingConstraint) {
			oldValue.removeRoutingConstraint(routingConstraint);
		}
//		monitor.stop();
	}
	
	/**
	 * Test 2-Line-1
	 * @param validationReporter
	 * @param context
	 * @param oldLine
	 * @param newLine
	 */
	private void twoDatabaseLineOneTest(ValidationReporter validationReporter, Context context, Line oldLine, Line newLine, ValidationData data) {
		if(!NeptuneUtil.sameValue(oldLine.getNetwork(), newLine.getNetwork()))
			validationReporter.addCheckPointReportError(context, DATABASE_LINE_1, data.getDataLocations().get(newLine.getChouetteId().getObjectId()));
		else
			validationReporter.reportSuccess(context, DATABASE_LINE_1);
	}
	
	/**
	 * Test 2-Line-2
	 * @param validationReporter
	 * @param context
	 * @param oldLine
	 * @param newLine
	 */
	private void twoDatabaseLineTwoTest(ValidationReporter validationReporter, Context context, Line oldLine, Line newLine, ValidationData data) {
		if(!NeptuneUtil.sameValue(oldLine.getCompany(), newLine.getCompany()))
			validationReporter.addCheckPointReportError(context, DATABASE_LINE_2, data.getDataLocations().get(newLine.getChouetteId().getObjectId()));
		else
			validationReporter.reportSuccess(context, DATABASE_LINE_2);
	}
	
	/**
	 * Test 2-Route-1
	 * @param validationReporter
	 * @param context
	 * @param oldRoute
	 * @param newRoute
	 */
	private void twoDatabaseRouteOneTest(ValidationReporter validationReporter, Context context, Route oldRoute, Route newRoute, ValidationData data) {
		if(!NeptuneUtil.sameValue(oldRoute.getLine(), newRoute.getLine()))
			validationReporter.addCheckPointReportError(context, DATABASE_ROUTE_1, data.getDataLocations().get(newRoute.getChouetteId().getObjectId()));
		else
			validationReporter.reportSuccess(context, DATABASE_ROUTE_1);
	}
}
