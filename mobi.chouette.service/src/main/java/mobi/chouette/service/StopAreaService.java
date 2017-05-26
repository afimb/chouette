package mobi.chouette.service;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.stopplace.PublicationDeliveryStopPlaceParser;
import mobi.chouette.exchange.stopplace.StopAreaUpdateService;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.Referential;

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
        Collection<StopArea> stopAreas = new PublicationDeliveryStopPlaceParser().parseStopPlaces(inputStream);

        if (stopAreas.size() > 0) {
            log.info("Updating " + stopAreas.size() + " stop areas");
            Context context = createContext();
            stopAreaUpdateService.createOrUpdateStopAreas(context, stopAreas);
        } else {
            log.debug("Received update without any stop areas. Doing nothing");
        }
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
