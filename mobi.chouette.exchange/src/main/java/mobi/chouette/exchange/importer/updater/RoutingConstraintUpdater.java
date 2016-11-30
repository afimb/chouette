package mobi.chouette.exchange.importer.updater;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.CollectionUtil;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.exchange.ChouetteIdObjectUtil;
import mobi.chouette.model.RoutingConstraint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = RoutingConstraintUpdater.BEAN_NAME)
@Log4j
public class RoutingConstraintUpdater implements Updater<RoutingConstraint> {

	public static final String BEAN_NAME = "RoutingConstraintUpdater";
	
	@EJB
	private StopAreaDAO stopAreaDAO;

	@EJB(beanName = StopAreaUpdater.BEAN_NAME)
	private Updater<StopArea> stopAreaUpdater;

	@Override
	public void update(Context context, RoutingConstraint oldValue, RoutingConstraint newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		Monitor monitor = MonitorFactory.start(BEAN_NAME);
		Referential cache = (Referential) context.get(CACHE);
		Referential referential = (Referential) context.get(REFERENTIAL);
		
		if (oldValue.isDetached()) {
			oldValue.setChouetteId(newValue.getChouetteId());
			oldValue.setObjectVersion(newValue.getObjectVersion());
			oldValue.setCreationTime(newValue.getCreationTime());
			oldValue.setCreatorId(newValue.getCreatorId());
			oldValue.setName(newValue.getName());
			oldValue.setDetached(false);
		} else {
			if (newValue.getChouetteId().getTechnicalId() != null && !(newValue.getChouetteId().getTechnicalId().equals(oldValue.getChouetteId().getTechnicalId()))) {
				oldValue.setChouetteId(newValue.getChouetteId());
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
		}
		

		// TODO list routing_constraints_lines (routingConstraintLines)
		// TODO list stop_areas_stop_areas (routingConstraintAreas)
		Collection<StopArea> addedStopAreas = CollectionUtil.substract(newValue.getRoutingConstraintAreas(),
				oldValue.getRoutingConstraintAreas(), NeptuneIdentifiedObjectComparator.INSTANCE);

		List<StopArea> stopAreas = null;
		for (StopArea item : addedStopAreas) {

			StopArea area = cache.getStopAreas().get(item.getChouetteId());
			if (area == null) {
				if (stopAreas == null) {
					stopAreas = (List<StopArea>) stopAreaDAO.findByChouetteId(UpdaterUtils.getChouetteIdsByCodeSpace(addedStopAreas));
					for (StopArea object : addedStopAreas) {
						cache.getStopAreas().put(object.getChouetteId(), object);
					}
				}
				area = cache.getStopAreas().get(item.getChouetteId());
			}

			if (area == null) {
				area = ChouetteIdObjectUtil.getStopArea(cache, item.getChouetteId());
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
		monitor.stop();

	}
}

