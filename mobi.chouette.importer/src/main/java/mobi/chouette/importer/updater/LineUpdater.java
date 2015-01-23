package mobi.chouette.importer.updater;

import java.util.Collection;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.CollectionUtils;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.CompanyDAO;
import mobi.chouette.dao.GroupOfLineDAO;
import mobi.chouette.dao.PTNetworkDAO;
import mobi.chouette.dao.RouteDAO;
import mobi.chouette.model.Company;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.PTNetwork;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;

@Log4j
public class LineUpdater implements Updater<Line> {
	@EJB
	private PTNetworkDAO ptNetworkDAO;

	@EJB
	private CompanyDAO companyDAO;

	@EJB
	private GroupOfLineDAO groupOfLineDAO;

	@EJB
	private RouteDAO routeDAO;

	@Override
	public void update(Line oldValue, Line newValue) throws Exception {

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
		if (newValue.getNumber() != null
				&& newValue.getNumber().compareTo(oldValue.getNumber()) != 0) {
			oldValue.setNumber(newValue.getNumber());
		}
		if (newValue.getPublishedName() != null
				&& newValue.getPublishedName().compareTo(
						oldValue.getPublishedName()) != 0) {
			oldValue.setPublishedName(newValue.getPublishedName());
		}
		if (newValue.getRegistrationNumber() != null
				&& newValue.getRegistrationNumber().compareTo(
						oldValue.getRegistrationNumber()) != 0) {
			oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
		}
		if (newValue.getTransportModeName() != null
				&& newValue.getTransportModeName().compareTo(
						oldValue.getTransportModeName()) != 0) {
			oldValue.setTransportModeName(newValue.getTransportModeName());
		}
		if (newValue.getMobilityRestrictedSuitable() != null
				&& newValue.getMobilityRestrictedSuitable().compareTo(
						oldValue.getMobilityRestrictedSuitable()) != 0) {
			oldValue.setMobilityRestrictedSuitable(newValue
					.getMobilityRestrictedSuitable());
		}
		if (newValue.getIntUserNeeds() != null
				&& newValue.getIntUserNeeds().compareTo(
						oldValue.getIntUserNeeds()) != 0) {
			oldValue.setIntUserNeeds(newValue.getIntUserNeeds());
		}
		if (newValue.getUrl() != null
				&& newValue.getUrl().compareTo(oldValue.getUrl()) != 0) {
			oldValue.setUrl(newValue.getUrl());
		}
		if (newValue.getColor() != null
				&& newValue.getColor().compareTo(oldValue.getColor()) != 0) {
			oldValue.setColor(newValue.getColor());
		}
		if (newValue.getTextColor() != null
				&& newValue.getTextColor().compareTo(oldValue.getTextColor()) != 0) {
			oldValue.setTextColor(newValue.getTextColor());
		}

		// PTNetwork
		if (newValue.getPtNetwork() == null) {
			oldValue.setPTNetwork(null);
		} else {
			PTNetwork ptNetwork = ptNetworkDAO.findByObjectId(newValue
					.getPtNetwork().getObjectId());
			if (ptNetwork == null) {
				ptNetwork = new PTNetwork();
				ptNetwork.setObjectId(newValue.getPtNetwork().getObjectId());
				ptNetworkDAO.create(ptNetwork);
			}
			Updater<PTNetwork> ptNetworkUpdater = UpdaterFactory
					.create(PTNetworkUpdater.class.getName());
			ptNetworkUpdater.update(oldValue.getPtNetwork(),
					newValue.getPtNetwork());
			oldValue.setPTNetwork(ptNetwork);
		}

		// Company
		if (newValue.getCompany() == null) {
			oldValue.setPTNetwork(null);
		} else {
			Company company = companyDAO.findByObjectId(newValue.getPtNetwork()
					.getObjectId());
			if (company == null) {
				company = new Company();
				company.setObjectId(newValue.getCompany().getObjectId());
				companyDAO.create(company);
			}
			Updater<Company> companyUpdater = UpdaterFactory
					.create(CompanyUpdater.class.getName());
			companyUpdater.update(oldValue.getCompany(), newValue.getCompany());
			oldValue.setCompany(company);
		}

		// GroupOfLine
		Collection<GroupOfLine> addedGroupOfLine = CollectionUtils.substract(
				newValue.getGroupOfLines(), oldValue.getGroupOfLines(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (GroupOfLine item : addedGroupOfLine) {
			GroupOfLine groupOfLine = groupOfLineDAO.findByObjectId(item
					.getObjectId());
			if (groupOfLine == null) {
				groupOfLine = new GroupOfLine();
				groupOfLine.setObjectId(item.getObjectId());
				groupOfLineDAO.create(groupOfLine);
			}
			groupOfLine.addLine(oldValue);
		}

		Updater<GroupOfLine> groupOfLineUpdater = UpdaterFactory
				.create(GroupOfLineUpdater.class.getName());
		Collection<Pair<GroupOfLine, GroupOfLine>> modifiedGroupOfLine = CollectionUtils
				.intersection(oldValue.getGroupOfLines(),
						newValue.getGroupOfLines(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<GroupOfLine, GroupOfLine> pair : modifiedGroupOfLine) {
			groupOfLineUpdater.update(pair.getLeft(), pair.getRight());
		}

		// TODO remove ?
		Collection<GroupOfLine> removedGroupOfLine = CollectionUtils.substract(
				oldValue.getGroupOfLines(), newValue.getGroupOfLines(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (GroupOfLine groupOfLine : removedGroupOfLine) {
			groupOfLine.removeLine(oldValue);
		}

		// Route
		Collection<Route> addedRoute = CollectionUtils.substract(
				newValue.getRoutes(), oldValue.getRoutes(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Route item : addedRoute) {
			Route route = routeDAO.findByObjectId(item.getObjectId());
			if (route == null) {
				route = new Route();
				route.setObjectId(item.getObjectId());
				routeDAO.create(route);
			}
			route.setLine(oldValue);
		}

		Updater<Route> routeUpdater = UpdaterFactory.create(RouteUpdater.class
				.getName());
		Collection<Pair<Route, Route>> modifiedRoute = CollectionUtils
				.intersection(oldValue.getRoutes(), newValue.getRoutes(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<Route, Route> pair : modifiedRoute) {
			routeUpdater.update(pair.getLeft(), pair.getRight());
		}

		// TODO remove ?
		Collection<Route> removedRoute = CollectionUtils.substract(
				oldValue.getRoutes(), newValue.getRoutes(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Route route : removedRoute) {
			route.setLine(null);
		}

		// TODO stop area list (routingConstraintLines)
	}

	static {
		UpdaterFactory.register(LineUpdater.class.getName(),
				new UpdaterFactory() {
					private LineUpdater INSTANCE = new LineUpdater();

					@Override
					protected Updater<Line> create() {
						return INSTANCE;
					}
				});
	}

}
