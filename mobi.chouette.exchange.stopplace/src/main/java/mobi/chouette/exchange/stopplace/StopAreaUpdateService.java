package mobi.chouette.exchange.stopplace;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.exchange.importer.updater.StopAreaUpdater;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton(name = StopAreaUpdateService.BEAN_NAME)
@Log4j
public class StopAreaUpdateService {

    public static final String BEAN_NAME = "StopAreaUpdateService";

    @EJB
    private StopAreaDAO stopAreaDAO;

    @EJB
    private StopPointDAO stopPointDAO;


    @EJB(beanName = StopAreaUpdater.BEAN_NAME)
    private Updater<StopArea> stopAreaUpdater;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createOrUpdateStopAreas(Context context, Collection<StopArea> stopAreas) {
        stopAreas.stream().filter(sa -> ChouetteAreaEnum.CommercialStopPoint.equals(sa.getAreaType())).forEach(sa -> createOrUpdate(context, sa));
    }

    private void createOrUpdate(Context context, StopArea stopArea) {
        // TODO deactivated stops
        StopArea existing = stopAreaDAO.findByObjectId(stopArea.getObjectId());
        if (existing == null) {
            log.debug("Creating new StopArea(StopPlace) : " + stopArea);
            stopAreaDAO.create(stopArea);
        } else {
            log.debug("Updating existing StopArea (StopPlace) : " + stopArea);

            try {
                Map<String, StopArea> existingQuays = existing.getContainedStopAreas().stream().collect(Collectors.toMap(StopArea::getObjectId,
                        Function.identity()));

                stopAreaUpdater.update(context, existing, stopArea);
                existing.getContainedStopAreas().clear();
                for (StopArea quay : new ArrayList<>(stopArea.getContainedStopAreas())) {

                    StopArea existingQuay = existingQuays.remove(quay.getObjectId());


                    if (existingQuay == null) {

                        // Quay with ID does not already exist for this StopArea, but may exist for another. If so, remove the existing quay.
                        StopArea quayAlreadyExisting = stopAreaDAO.findByObjectId(quay.getObjectId());
                        if (quayAlreadyExisting != null) {
                            removeQuay(quayAlreadyExisting);
                        }

                        log.debug("Creating new StopArea(Quay) : " + quay);
                        quay.setParent(existing);
                        stopAreaDAO.create(quay);

                    } else {
                        log.debug("Updating existing StopArea(Quay) : " + stopArea);
                        stopAreaDAO.update(existingQuay);
                    }
                }

                for (StopArea obsoleteStopArea : existingQuays.values()) {
                    removeQuay(obsoleteStopArea);
                }

                stopAreaDAO.update(existing);
            } catch (Exception e) {
                throw new RuntimeException("Failed to update stop place: " + e.getMessage(), e);
            }
        }
    }

    private void removeQuay(StopArea obsoleteStopArea) {
        log.debug("Deleting obsolete quay: " + obsoleteStopArea.getObjectId());
        obsoleteStopArea.getContainedStopPoints().forEach(stopPoint -> removeStopAreaReferenceFromStopPoint(stopPoint));
        stopAreaDAO.delete(obsoleteStopArea);
    }


    private void removeStopAreaReferenceFromStopPoint(StopPoint stopPoint) {
        stopPoint.setContainedInStopArea(null);
        stopPointDAO.update(stopPoint);
    }

}
