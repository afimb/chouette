package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.DataLocation;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.RoutePoint;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class RoutePointValidator extends AbstractValidator implements Validator<RoutePoint> {

    public static final String LOCAL_CONTEXT = "NetexRoutePoint";
    public static final String STOPPOINT_ID = "stopPointId";
    public static String NAME = "RoutePointValidator";

    private static final String ROUTEPOINT_1 = "2-NETEX-RoutePoint-1";
    private static final String ROUTEPOINT_2 = "2-NETEX-RoutePoint-2";
    private static final String ROUTEPOINT_3 = "2-NETEX-RoutePoint-3";
    private static final String ROUTEPOINT_4 = "2-NETEX-RoutePoint-4";

    @Override
    protected void initializeCheckPoints(Context context) {
        addItemToValidation(context, PREFIX, "RoutePoint", 4, "E", "E", "E", "E");
    }

    @Override
    public void addObjectReference(Context context, DataManagedObjectStructure object) {
        addObjectReference(context, LOCAL_CONTEXT, object);
    }

    @SuppressWarnings("unchecked")
    public void addStopPointReference(Context context, String objectId, String stopPointId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        List<String> stopPointIds = (List<String>) objectContext.get(STOPPOINT_ID);
        if (stopPointIds == null) {
            stopPointIds = new ArrayList<>();
            objectContext.put(STOPPOINT_ID, stopPointIds);
        }
        stopPointIds.add(stopPointId);
    }

    @Override
    public void validate(Context context, RoutePoint target) throws ValidationException {
        Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
        ValidationData data = (ValidationData) context.get(VALIDATION_DATA); // how should this be used?
        Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);

        if (localContext == null || localContext.isEmpty()) {
            return;
        }

        Context stopPointContext = (Context) validationContext.get(ScheduledStopPointValidator.LOCAL_CONTEXT);
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        Map<String, RoutePoint> routePoints = referential.getRoutePoints();

        for (String objectId : localContext.keySet()) {
            Context objectContext = (Context) localContext.get(objectId);
            RoutePoint routePoint = routePoints.get(objectId);

            // 2-NETEX-RoutePoint-1 : validate optional border crossing (cardinality 0:1)
            prepareCheckPoint(context, ROUTEPOINT_1);
            if (routePoint.isBorderCrossing() != null) {
                log.info("RoutePoint - Border Crossing present");
                // TODO validate
            }

            // validate name?

            // 2-NETEX-RoutePoint-2 : validate mandatory location (cardinality 1:1)
            prepareCheckPoint(context, ROUTEPOINT_2);
            LocationStructure locationStruct = routePoint.getLocation();

            if (locationStruct == null) {
                DataLocation dataLocation = new DataLocation((String)context.get(FILE_NAME));
                dataLocation.setName("Location");
                addValidationError(context, ROUTEPOINT_2, "Missing mandatory element : 'Location'", dataLocation);
            } else {
                // TODO validate location here...
            }

            // 2-NETEX-RoutePoint-3 : validate optional point number (cardinality 0:1)
            prepareCheckPoint(context, ROUTEPOINT_3);
            String pointNumber = routePoint.getPointNumber();

            if (StringUtils.isNotEmpty(pointNumber)) {
                log.info("RoutePoint - Point number present");
                // TODO validate point number here...
            }

            // TODO create a context/validator for scheduled stop points for reuse and references here

            // 2-NETEX-RoutePoint-4 : validate scheduled stop point references
            prepareCheckPoint(context, ROUTEPOINT_4);
            @SuppressWarnings("unchecked") List<String> stopPointIds = (List<String>) objectContext.get(STOPPOINT_ID);
            for (String stopPointId : stopPointIds) {
                if (!stopPointContext.containsKey(stopPointId)) {
                    DataLocation dataLocation = new DataLocation((String)context.get(FILE_NAME));
                    addValidationError(context, ROUTEPOINT_4, String.format("Non-existent stop point id : '%s'", stopPointId), dataLocation);
                }
            }
        }
    }

    public static class DefaultValidatorFactory extends ValidatorFactory {
        @Override
        protected Validator<RoutePoint> create(Context context) {
            RoutePointValidator instance = (RoutePointValidator) context.get(NAME);
            if (instance == null) {
                instance = new RoutePointValidator();
                context.put(NAME, instance);
            }
            return instance;
        }
    }

    static {
        ValidatorFactory.factories.put(RoutePointValidator.class.getName(), new RoutePointValidator.DefaultValidatorFactory());
    }

}
