package mobi.chouette.importer.updater;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;

import mobi.chouette.common.CollectionUtils;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.ConnectionLinkDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.Referential;

public class ChouetteUpdater {
	@EJB
	private StopAreaDAO stopAreaDAO;

	@EJB
	private ConnectionLinkDAO connectionLinkDAO;

	@EJB
	private LineDAO lineDAO;

	public void update(Referential referential) throws Exception {
		List<StopArea> stopAreas = stopAreaDAO.findAll();

		// StopArea
		Collection<StopArea> addedStopAreas = CollectionUtils.substract(
				referential.getStopAreas().values(), stopAreas,
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (StopArea item : addedStopAreas) {
			StopArea stopArea = new StopArea();
			stopArea.setObjectId(item.getObjectId());
			stopAreaDAO.create(stopArea);
			stopAreas.add(stopArea);
		}

		Updater<StopArea> stopAreaUpdater = UpdaterFactory
				.create(StopAreaUpdater.class.getName());
		Collection<Pair<StopArea, StopArea>> modifiedStopAreas = CollectionUtils
				.intersection(stopAreas, referential.getStopAreas().values(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<StopArea, StopArea> pair : modifiedStopAreas) {
			stopAreaUpdater.update(pair.getLeft(), pair.getRight());
			if (pair.getRight().getParent() != null) {
				StopArea stopArea = stopAreaDAO.findByObjectId(pair.getRight()
						.getObjectId());
				StopArea parent = stopAreaDAO.findByObjectId(pair.getRight()
						.getParent().getObjectId());
				stopArea.setParent(parent);
			}
			stopAreaDAO.update(pair.getLeft());
		}

		// ConnectionLink
		List<ConnectionLink> connectionLinks = connectionLinkDAO.findAll();
		Collection<ConnectionLink> addedConnectionLink = CollectionUtils
				.substract(referential.getConnectionLinks().values(),
						connectionLinks,
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (ConnectionLink item : addedConnectionLink) {
			ConnectionLink connectionLink = new ConnectionLink();
			connectionLink.setObjectId(item.getObjectId());
			connectionLinkDAO.create(connectionLink);
			connectionLinks.add(connectionLink);
		}
		Updater<ConnectionLink> connectionLinkUpdater = UpdaterFactory
				.create(ConnectionLinkUpdater.class.getName());
		Collection<Pair<ConnectionLink, ConnectionLink>> modifiedConnectionLink = CollectionUtils
				.intersection(connectionLinks, referential.getConnectionLinks()
						.values(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<ConnectionLink, ConnectionLink> pair : modifiedConnectionLink) {
			connectionLinkUpdater.update(pair.getLeft(), pair.getRight());
			connectionLinkDAO.update(pair.getLeft());
		}

		// Line
		for (Line newLine : referential.getLines().values()) {
			Line oldLine = lineDAO.findByObjectId(newLine.getObjectId());
			if (oldLine == null) {
				oldLine = new Line();
				oldLine.setObjectId(newLine.getObjectId());
				lineDAO.create(oldLine);
			}
			Updater<Line> lineUpdater = UpdaterFactory.create(LineUpdater.class
					.getName());
			lineUpdater.update(oldLine, newLine);
			lineDAO.update(oldLine);
		}
	}

}
