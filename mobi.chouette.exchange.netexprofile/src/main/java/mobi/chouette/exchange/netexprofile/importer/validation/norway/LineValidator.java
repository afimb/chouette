package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import no.rutebanken.netex.model.DataManagedObjectStructure;
import no.rutebanken.netex.model.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class LineValidator extends AbstractValidator implements Validator<Line> {

    public static final String LOCAL_CONTEXT = "NetexLine";
    public static final String NAME = "LineValidator";
    public static final String ROUTE_ID = "routeId";
    public static final String OPERATOR_ID = "operatorId";

    private static final String LINE_1 = "2-NETEX-Line-1";
    private static final String LINE_2 = "2-NETEX-Line-2";
    private static final String LINE_3 = "2-NETEX-Line-3";
    private static final String LINE_4 = "2-NETEX-Line-4";
    private static final String LINE_5 = "2-NETEX-Line-5";
    private static final String LINE_6 = "2-NETEX-Line-6";
    private static final String LINE_7 = "2-NETEX-Line-7";
    private static final String LINE_8 = "2-NETEX-Line-8";
    private static final String LINE_9 = "2-NETEX-Line-9";

    @Override
    protected void initializeCheckPoints(Context context) {
        addItemToValidation(context, PREFIX, "Line", 9, "E", "E", "E", "E", "E", "E", "E", "E", "E");
    }

    @Override
    public void addObjectReference(Context context, DataManagedObjectStructure object) {
        addObjectReference(context, LOCAL_CONTEXT, object);
    }

    public void addOperatorReference(Context context, String objectId, String operatorId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        List<String> operatorIds = (List<String>) objectContext.get(OPERATOR_ID);
        if (operatorIds == null) {
            operatorIds = new ArrayList<>();
            objectContext.put(OPERATOR_ID, operatorIds);
        }
        operatorIds.add(operatorId);
    }

    public void addRouteReference(Context context, String objectId, String routeId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        List<String> routeIds = (List<String>) objectContext.get(ROUTE_ID);
        if (routeIds == null) {
            routeIds = new ArrayList<>();
            objectContext.put(ROUTE_ID, routeIds);
        }
        routeIds.add(routeId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void validate(Context context, Line target) throws ValidationException {
        Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
        ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
        Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);

        if (localContext == null || localContext.isEmpty()) {
            return;
        }

        Context operatorContext = (Context) validationContext.get(OrganisationValidator.LOCAL_CONTEXT);
        Context routeContext = (Context) validationContext.get(RouteValidator.LOCAL_CONTEXT);
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        Map<String, Line> lines = referential.getLines();

        for (String objectId : localContext.keySet()) {
            Context objectContext = (Context) localContext.get(objectId);
            Line line = lines.get(objectId);

            // TODO consider validating with xpath instead, if shorter
            // 2-NETEX-Line-1 : check presence of Name
            prepareCheckPoint(context, LINE_1);
            if (line.getName() == null || isEmpty(line.getName().getValue())) {
                addValidationError(context, LINE_1);
            }

            // TODO consider validating with xpath instead, if shorter
            // 2-NETEX-Line-2 : check presence of TransportMode
            prepareCheckPoint(context, LINE_2);
            if (line.getTransportMode() == null) {
                addValidationError(context, LINE_2);
            }

            // TODO consider validating with xpath instead, if shorter
            // 2-NETEX-Line-3 : check presence of PublicCode
            prepareCheckPoint(context, LINE_3);
            if (isEmpty(line.getPublicCode())) {
                addValidationError(context, LINE_3);
            }

            // TODO consider validating with xpath instead, if shorter
            // 2-NETEX-Line-4 : check presence of OperatorRef
            prepareCheckPoint(context, LINE_4);
            if (line.getOperatorRef() == null || isEmpty(line.getOperatorRef().getRef())) {
                addValidationError(context, LINE_4);
            }

            // 2-NETEX-Line-5 : check operator reference
            prepareCheckPoint(context, LINE_5);
            List<String> operatorIds = (List<String>) objectContext.get(OPERATOR_ID);
            for (String operatorId : operatorIds) {
                if (!operatorContext.containsKey(operatorId)) {
                    addValidationError(context, LINE_5);
                }
            }

            // 2-NETEX-Line-6 : check routes references
            prepareCheckPoint(context, LINE_6);
            List<String> routeIds = (List<String>) objectContext.get(ROUTE_ID);
            for (String routeId : routeIds) {
                if (!routeContext.containsKey(routeId)) {
                    addValidationError(context, LINE_6);
                }
            }

            // 2-NETEX-Line-7 : check routes references
            prepareCheckPoint(context, LINE_7);
            for (String routeId : routeContext.keySet()) {
                if (!routeIds.contains(routeId)) {
                    addValidationError(context, LINE_7);
                }
            }

            // TODO consider validating with xpath instead, if shorter
            // 2-NETEX-Line-9 : check presence of Monitored
            prepareCheckPoint(context, LINE_8);
            if (line.isMonitored() == null) {
                addValidationError(context, LINE_8);
            }

            // TODO consider validating with xpath instead, if shorter
            // 2-NETEX-Line-10 : check presence of AccessibilityAssessment
            prepareCheckPoint(context, LINE_9);
            if (line.getAccessibilityAssessment() == null) {
                // TODO validate full structure/list of elements
            }
        }
    }

    public static class DefaultValidatorFactory extends ValidatorFactory {
        @Override
        protected Validator<Line> create(Context context) {
            LineValidator instance = (LineValidator) context.get(NAME);
            if (instance == null) {
                instance = new LineValidator();
                context.put(NAME, instance);
            }
            return instance;
        }
    }

    static {
        ValidatorFactory.factories.put(LineValidator.class.getName(), new LineValidator.DefaultValidatorFactory());
    }

}
