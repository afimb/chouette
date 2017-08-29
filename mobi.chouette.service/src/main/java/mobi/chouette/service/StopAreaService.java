package mobi.chouette.service;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.stopplace.PublicationDeliveryStopPlaceParser;
import mobi.chouette.exchange.stopplace.StopAreaUpdateContext;
import mobi.chouette.exchange.stopplace.StopAreaUpdateService;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.Referential;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.rutebanken.netex.model.PublicationDeliveryStructure;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import java.io.InputStream;
import java.util.Collection;

@Singleton(name = StopAreaService.BEAN_NAME)
@Log4j
public class StopAreaService {

    public static final String BEAN_NAME = "StopAreaService";

    @EJB(beanName = StopAreaUpdateService.BEAN_NAME)
    private StopAreaUpdateService stopAreaUpdateService;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createOrUpdateStopPlacesFromNetexStopPlaces(InputStream inputStream) {
        PublicationDeliveryStopPlaceParser parser = new PublicationDeliveryStopPlaceParser(inputStream);

        StopAreaUpdateContext updateContext=parser.getUpdateContext();
        int changedStopCnt = updateContext.getChangedStopCount();

        if (changedStopCnt > 0) {
            log.info("Updating " + changedStopCnt + " stop areas");
            Context context = createContext();
            ContextHolder.clear();
            stopAreaUpdateService.createOrUpdateStopAreas(context, updateContext);
        } else {
            log.debug("Received update without any stop areas. Doing nothing");
        }
    }

    @TransactionAttribute
    public void deleteStopArea(String objectId) {
        ContextHolder.clear();
        stopAreaUpdateService.deleteStopArea(objectId);
    }

    private Context createContext() {
        Context context = new Context();
        Referential referential = new Referential();
        context.put(Constant.REFERENTIAL, referential);
        context.put(Constant.CACHE, referential);
        context.put(Constant.REPORT, new ActionReport());
        context.put(Constant.VALIDATION_REPORT, new ValidationReport());
        return context;
    }

}
