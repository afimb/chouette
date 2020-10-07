package mobi.chouette.exchange.importer.updater;

import mobi.chouette.common.CollectionUtil;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.DatedServiceJourneyDAO;
import mobi.chouette.model.DatedServiceJourney;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collection;
import java.util.List;

@Stateless(name = DatedServiceJourneyUpdater.BEAN_NAME)
public class DatedServiceJourneyUpdater implements Updater<DatedServiceJourney> {

    public static final String BEAN_NAME = "DatedServiceJourneyUpdater";

    @EJB
    private DatedServiceJourneyDAO datedServiceJourneyDAO;


    @Override
    public void update(Context context, DatedServiceJourney oldValue, DatedServiceJourney newValue) throws Exception {

        if (newValue.isSaved()) {
            return;
        }
        newValue.setSaved(true);

        Referential cache = (Referential) context.get(CACHE);
        cache.getDatedServiceJourneys().put(oldValue.getObjectId(), oldValue);

        if (oldValue.isDetached()) {
            // object does not exist in database
            oldValue.setObjectId(newValue.getObjectId());
            oldValue.setObjectVersion(newValue.getObjectVersion());
            oldValue.setCreationTime(newValue.getCreationTime());
            oldValue.setCreatorId(newValue.getCreatorId());
            oldValue.setServiceAlteration(newValue.getServiceAlteration());
            oldValue.setOperatingDay(newValue.getOperatingDay());
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

            if (newValue.getServiceAlteration() != null && !newValue.getServiceAlteration().equals(oldValue.getServiceAlteration())) {
                oldValue.setServiceAlteration(newValue.getServiceAlteration());
            }

            if (newValue.getOperatingDay() != null
                    && !newValue.getOperatingDay().equals(oldValue.getOperatingDay())) {
                oldValue.setOperatingDay(newValue.getOperatingDay());
            }
        }

        Collection<DatedServiceJourney> addedOriginalDatedServiceJourney = CollectionUtil.substract(newValue.getOriginalDatedServiceJourneys(),
                oldValue.getOriginalDatedServiceJourneys(), NeptuneIdentifiedObjectComparator.INSTANCE);
        List<DatedServiceJourney> datedServiceJourneys = null;
        for (DatedServiceJourney item : addedOriginalDatedServiceJourney) {
            DatedServiceJourney datedServiceJourney = cache.getDatedServiceJourneys().get(item.getObjectId());
            if (datedServiceJourney == null) {
                if (datedServiceJourneys == null) {
                    datedServiceJourneys = datedServiceJourneyDAO.findByObjectId(UpdaterUtils.getObjectIds(addedOriginalDatedServiceJourney));
                    for (DatedServiceJourney object : datedServiceJourneys) {
                        cache.getDatedServiceJourneys().put(object.getObjectId(), object);
                    }
                }
                datedServiceJourney = cache.getDatedServiceJourneys().get(item.getObjectId());
            }
            if (datedServiceJourney == null) {
                datedServiceJourney = ObjectFactory.getDatedServiceJourney(cache, item.getObjectId());
            }
            oldValue.addOriginalDatedServiceJourney(datedServiceJourney);
        }

        Collection<Pair<DatedServiceJourney, DatedServiceJourney>> modifiedDatedServiceJourney = CollectionUtil.intersection(
                oldValue.getOriginalDatedServiceJourneys(), newValue.getOriginalDatedServiceJourneys(), NeptuneIdentifiedObjectComparator.INSTANCE);
        for (Pair<DatedServiceJourney, DatedServiceJourney> pair : modifiedDatedServiceJourney) {
            update(context, pair.getLeft(), pair.getRight());
        }

        Collection<DatedServiceJourney> removedDatedServiceJourney = CollectionUtil.substract(oldValue.getOriginalDatedServiceJourneys(),
                newValue.getOriginalDatedServiceJourneys(), NeptuneIdentifiedObjectComparator.INSTANCE);
        for (DatedServiceJourney datedServiceJourney : removedDatedServiceJourney) {
            oldValue.removeOriginalDatedServiceJourney(datedServiceJourney);
        }

    }
}
