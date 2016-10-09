package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.validation.*;
import no.rutebanken.netex.model.DataManagedObjectStructure;
import no.rutebanken.netex.model.Route;

import java.util.Map;

@Log4j
public class RouteValidator extends AbstractValidator implements Validator<Route> {

    public static final String LOCAL_CONTEXT = "NetexRoute";
    public static final String NAME = "RouteValidator";

    private static final String ROUTE_1 = "2-NETEX-Route-1";

    @Override
    protected void initializeCheckPoints(Context context) {
        // TODO add all validation items
    }

    @Override
    public void addObjectReference(Context context, DataManagedObjectStructure object) {
        addObjectReference(context, LOCAL_CONTEXT, object);
    }

    @Override
    public ValidationConstraints validate(Context context, Route target) throws ValidationException {
        Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
        ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
        Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);

        if (localContext == null || localContext.isEmpty()) {
            return new ValidationConstraints();
        }

        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        Map<String, Route> routes = referential.getRoutes();

        for (String objectId : localContext.keySet()) {
            Context objectContext = (Context) localContext.get(objectId);
            Route route = routes.get(objectId);
        }

        return null;
    }

    public static class DefaultValidatorFactory extends ValidatorFactory {
        @Override
        protected Validator<Route> create(Context context) {
            RouteValidator instance = (RouteValidator) context.get(NAME);
            if (instance == null) {
                instance = new RouteValidator();
                context.put(NAME, instance);
            }
            return instance;
        }
    }

    static {
        ValidatorFactory.factories.put(RouteValidator.class.getName(), new RouteValidator.DefaultValidatorFactory());
    }

}
