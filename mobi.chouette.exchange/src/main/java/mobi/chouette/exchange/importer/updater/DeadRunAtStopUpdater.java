package mobi.chouette.exchange.importer.updater;

import mobi.chouette.common.Context;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.model.DeadRunAtStop;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.Referential;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless(name = DeadRunAtStopUpdater.BEAN_NAME)
public class DeadRunAtStopUpdater implements
        Updater<DeadRunAtStop> {

    public static final String BEAN_NAME = "DeadRunAtStopUpdater";

    @EJB
    private StopPointDAO stopPointDAO;

    @Override
    public void update(Context context, DeadRunAtStop oldValue,
                       DeadRunAtStop newValue) throws Exception {

        Referential cache = (Referential) context.get(CACHE);


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

        if (newValue.getArrivalTime() != null
                && !newValue.getArrivalTime().equals(oldValue.getArrivalTime())) {
            oldValue.setArrivalTime(newValue.getArrivalTime());
        }
        if (newValue.getDepartureTime() != null
                && !newValue.getDepartureTime().equals(
                oldValue.getDepartureTime())) {
            oldValue.setDepartureTime(newValue.getDepartureTime());
        }

        if (newValue.getArrivalDayOffset() != oldValue.getArrivalDayOffset()) {
            oldValue.setArrivalDayOffset(newValue.getArrivalDayOffset());
        }
        if (newValue.getDepartureDayOffset() != oldValue.getDepartureDayOffset()) {
            oldValue.setDepartureDayOffset(newValue.getDepartureDayOffset());
        }

        // StopPoint
        if (oldValue.getStopPoint() == null
                || !oldValue.getStopPoint().equals(newValue.getStopPoint())) {
            StopPoint stopPoint = stopPointDAO.findByObjectId(newValue
                    .getStopPoint().getObjectId());
            if (stopPoint != null) {
                oldValue.setStopPoint(stopPoint);
            }
        }

    }


}
