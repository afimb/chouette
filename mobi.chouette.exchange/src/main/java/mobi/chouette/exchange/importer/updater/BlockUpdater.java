package mobi.chouette.exchange.importer.updater;

import mobi.chouette.common.CollectionUtil;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.DeadRunDAO;
import mobi.chouette.dao.ScheduledStopPointDAO;
import mobi.chouette.dao.TimetableDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.model.Block;
import mobi.chouette.model.DeadRun;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collection;
import java.util.List;

@Stateless(name = BlockUpdater.BEAN_NAME)
public class BlockUpdater implements Updater<Block> {

    public static final String BEAN_NAME = "BlockUpdater";

    @EJB(beanName = TimetableUpdater.BEAN_NAME)
    private Updater<Timetable> timetableUpdater;

    @EJB
    private VehicleJourneyDAO vehicleJourneyDAO;

    @EJB
    private DeadRunDAO deadRunDAO;

    @EJB
    private TimetableDAO timetableDAO;

    @EJB
    private ScheduledStopPointDAO scheduledStopPointDAO;

    @EJB(beanName = ScheduledStopPointUpdater.BEAN_NAME)
    private Updater<ScheduledStopPoint> scheduledStopPointUpdater;


    @Override
    public void update(Context context, Block oldValue, Block newValue) throws Exception {

        if (newValue.isSaved()) {
            return;
        }
        newValue.setSaved(true);

        Referential cache = (Referential) context.get(CACHE);
        cache.getBlocks().put(oldValue.getObjectId(), oldValue);

        if (oldValue.isDetached()) {
            // object does not exist in database
            oldValue.setObjectId(newValue.getObjectId());
            oldValue.setObjectVersion(newValue.getObjectVersion());
            oldValue.setCreationTime(newValue.getCreationTime());
            oldValue.setCreatorId(newValue.getCreatorId());
            oldValue.setName(newValue.getName());
            oldValue.setPrivateCode(newValue.getPrivateCode());
            oldValue.setDescription(newValue.getDescription());
            oldValue.setStartTime(newValue.getStartTime());
            oldValue.setEndTime(newValue.getEndTime());
            oldValue.setEndTimeDayOffset(newValue.getEndTimeDayOffset());
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
        }

        // Day Types
        Collection<Timetable> addedTimetables = CollectionUtil.substract(newValue.getTimetables(),
                oldValue.getTimetables(), NeptuneIdentifiedObjectComparator.INSTANCE);
        List<Timetable> timetables = null;
        for (Timetable item : addedTimetables) {
            Timetable timetable = cache.getTimetables().get(item.getObjectId());
            if (timetable == null) {
                if (timetables == null) {
                    timetables = timetableDAO.findByObjectId(UpdaterUtils.getObjectIds(addedTimetables));
                    for (Timetable object : timetables) {
                        cache.getTimetables().put(object.getObjectId(), object);
                    }
                }
                timetable = cache.getTimetables().get(item.getObjectId());
            }
            if (timetable == null) {
                timetable = ObjectFactory.getTimetable(cache, item.getObjectId());
            }
            oldValue.getTimetables().add(timetable);
        }
        Collection<Pair<Timetable, Timetable>> modifiedTimetable = CollectionUtil.intersection(
                oldValue.getTimetables(), newValue.getTimetables(), NeptuneIdentifiedObjectComparator.INSTANCE);
        for (Pair<Timetable, Timetable> pair : modifiedTimetable) {
            timetableUpdater.update(context, pair.getLeft(), pair.getRight());
        }

        // Vehicle Journeys
        Collection<VehicleJourney> addedVehicleJourneys = CollectionUtil.substract(newValue.getVehicleJourneys(),
                oldValue.getVehicleJourneys(), NeptuneIdentifiedObjectComparator.INSTANCE);
        List<VehicleJourney> vehicleJourneys = null;
        for (VehicleJourney item : addedVehicleJourneys) {
            VehicleJourney vehicleJourney = cache.getVehicleJourneys().get(item.getObjectId());
            if (vehicleJourney == null) {
                if (vehicleJourneys == null) {
                    vehicleJourneys = vehicleJourneyDAO.findByObjectId(UpdaterUtils.getObjectIds(addedVehicleJourneys));
                    for (VehicleJourney object : vehicleJourneys) {
                        cache.getVehicleJourneys().put(object.getObjectId(), object);
                    }
                }
                vehicleJourney = cache.getVehicleJourneys().get(item.getObjectId());
            }
            if (vehicleJourney == null) {
                vehicleJourney = ObjectFactory.getVehicleJourney(cache, item.getObjectId());
            }
            oldValue.getVehicleJourneys().add(vehicleJourney);
        }

        Collection<VehicleJourney> removedVehicleJourney = CollectionUtil.substract(oldValue.getVehicleJourneys(),
                newValue.getVehicleJourneys(), NeptuneIdentifiedObjectComparator.INSTANCE);
        for (VehicleJourney vehicleJourney : removedVehicleJourney) {
            oldValue.getVehicleJourneys().remove(vehicleJourney);
        }

        // Start point
        if (newValue.getStartPoint() == null) {
            oldValue.setStartPoint(null);
        } else {
            String objectId = newValue.getStartPoint().getObjectId();
            ScheduledStopPoint scheduledStopPoint = cache.getScheduledStopPoints().get(objectId);
            if (scheduledStopPoint == null) {
                scheduledStopPoint = scheduledStopPointDAO.findByObjectId(objectId);
                if (scheduledStopPoint != null) {
                    cache.getScheduledStopPoints().put(objectId, scheduledStopPoint);
                }
            }

            if (scheduledStopPoint == null) {
                scheduledStopPoint = ObjectFactory.getScheduledStopPoint(cache, objectId);
            }
            oldValue.setStartPoint(scheduledStopPoint);
            scheduledStopPointUpdater.update(context, oldValue.getStartPoint(), newValue.getStartPoint());
        }

        // End point
        if (newValue.getEndPoint() == null) {
            oldValue.setEndPoint(null);
        } else {
            String objectId = newValue.getEndPoint().getObjectId();
            ScheduledStopPoint scheduledStopPoint = cache.getScheduledStopPoints().get(objectId);
            if (scheduledStopPoint == null) {
                scheduledStopPoint = scheduledStopPointDAO.findByObjectId(objectId);
                if (scheduledStopPoint != null) {
                    cache.getScheduledStopPoints().put(objectId, scheduledStopPoint);
                }
            }

            if (scheduledStopPoint == null) {
                scheduledStopPoint = ObjectFactory.getScheduledStopPoint(cache, objectId);
            }
            oldValue.setEndPoint(scheduledStopPoint);
            scheduledStopPointUpdater.update(context, oldValue.getEndPoint(), newValue.getEndPoint());
        }

        // Dead Runs
        Collection<DeadRun> addedDeadRuns = CollectionUtil.substract(newValue.getDeadRuns(),
                oldValue.getDeadRuns(), NeptuneIdentifiedObjectComparator.INSTANCE);
        List<DeadRun> deadRuns = null;
        for (DeadRun item : addedDeadRuns) {
            DeadRun deadRun = cache.getDeadRuns().get(item.getObjectId());
            if (deadRun == null) {
                if (deadRuns == null) {
                    deadRuns = deadRunDAO.findByObjectId(UpdaterUtils.getObjectIds(addedDeadRuns));
                    for (DeadRun object : deadRuns) {
                        cache.getDeadRuns().put(object.getObjectId(), object);
                    }
                }
                deadRun = cache.getDeadRuns().get(item.getObjectId());
            }
            if (deadRun == null) {
                deadRun = ObjectFactory.getDeadRun(cache, item.getObjectId());
            }
            oldValue.getDeadRuns().add(deadRun);
        }

        Collection<DeadRun> removedDeadRun = CollectionUtil.substract(oldValue.getDeadRuns(),
                newValue.getDeadRuns(), NeptuneIdentifiedObjectComparator.INSTANCE);
        for (DeadRun deadRun : removedDeadRun) {
            oldValue.getDeadRuns().remove(deadRun);
        }



    }
}
