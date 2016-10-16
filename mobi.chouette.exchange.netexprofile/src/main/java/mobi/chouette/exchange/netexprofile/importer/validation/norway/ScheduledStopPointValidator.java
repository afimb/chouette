package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import no.rutebanken.netex.model.DataManagedObjectStructure;
import no.rutebanken.netex.model.Network;
import no.rutebanken.netex.model.ScheduledStopPoint;

public class ScheduledStopPointValidator extends AbstractValidator implements Validator<ScheduledStopPoint> {

    public static final String LOCAL_CONTEXT = "NetexScheduledStopPoint";
    public static String NAME = "ScheduledStopPointValidator";

    @Override
    protected void initializeCheckPoints(Context context) {}

    @Override
    public void addObjectReference(Context context, DataManagedObjectStructure object) {
        addObjectReference(context, LOCAL_CONTEXT, object);
    }

    @Override
    public ValidationConstraints validate(Context context, ScheduledStopPoint target) throws ValidationException {
        return new ValidationConstraints();
    }

    public static class DefaultValidatorFactory extends ValidatorFactory {
        @Override
        protected Validator<ScheduledStopPoint> create(Context context) {
            ScheduledStopPointValidator instance = (ScheduledStopPointValidator) context.get(NAME);
            if (instance == null) {
                instance = new ScheduledStopPointValidator();
                context.put(NAME, instance);
            }
            return instance;
        }
    }

    static {
        ValidatorFactory.factories.put(ScheduledStopPointValidator.class.getName(),
                new ScheduledStopPointValidator.DefaultValidatorFactory());
    }

}
