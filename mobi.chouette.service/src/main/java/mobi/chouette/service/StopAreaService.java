package mobi.chouette.service;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.dao.ReferentialDAO;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.stopplace.PublicationDeliveryStopPlaceParser;
import mobi.chouette.exchange.stopplace.StopAreaUpdateContext;
import mobi.chouette.exchange.stopplace.StopAreaUpdateService;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.util.Referential;
import mobi.chouette.persistence.hibernate.ContextHolder;

@Singleton(name = StopAreaService.BEAN_NAME)
@Log4j
public class StopAreaService {

    public static final String BEAN_NAME = "StopAreaService";

    @EJB(beanName = StopAreaUpdateService.BEAN_NAME)
    private StopAreaUpdateService stopAreaUpdateService;

    @EJB
    private ReferentialDAO referentialDAO;


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

    private void updateStopAreaReferencesPerReferential(Map<String, Set<String>> replacementMap) {
        int updatedStopPointCnt = 0;
        String orgContext = ContextHolder.getContext();
        try {
            for (String referential : referentialDAO.getReferentials()) {
                ContextHolder.setContext(referential);
                updatedStopPointCnt += stopAreaUpdateService.updateStopAreaReferences(replacementMap);
            }
        } finally {
            ContextHolder.setContext(orgContext); // reset context
        }
        log.info("Updated stop area references for " + updatedStopPointCnt + " stop points");
    }

    public void deleteStopArea(String objectId) {
        ContextHolder.clear();
        stopAreaUpdateService.deleteStopArea(objectId);
    }

    public void deleteUnusedStopAreas() {
        ContextHolder.clear();
        stopAreaUpdateService.deleteUnusedStopAreas();
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
