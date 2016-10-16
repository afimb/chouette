package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.validation.*;
import no.rutebanken.netex.model.Authority;
import no.rutebanken.netex.model.DataManagedObjectStructure;
import no.rutebanken.netex.model.Operator;
import no.rutebanken.netex.model.Organisation;

import java.util.Collection;
import java.util.Map;

@Log4j
public class OrganisationValidator extends AbstractValidator implements Validator<Organisation> {

    public static final String LOCAL_CONTEXT = "NetexOrganisation";
    public static final String NAME = "OrganisationValidator";

    private static final String ORGANISATION_1 = "2-NETEX-Organisation-1";

    @Override
    protected void initializeCheckPoints(Context context) {
        // TODO add all validation items
    }

    @Override
    public void addObjectReference(Context context, DataManagedObjectStructure object) {
        addObjectReference(context, LOCAL_CONTEXT, object);
    }

    @Override
    public ValidationConstraints validate(Context context, Organisation target) throws ValidationException {
        Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
        ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
        Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);

        if (localContext == null || localContext.isEmpty()) {
            return new ValidationConstraints();
        }

        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        Map<String, Organisation> organisations = referential.getOrganisations();
        Collection<Authority> authorities = referential.getAuthorities().values();
        Collection<Operator> operators = referential.getOperators().values();

        for (String objectId : localContext.keySet()) {
            Context objectContext = (Context) localContext.get(objectId);
            Organisation organisation = organisations.get(objectId);
        }
        return new ValidationConstraints();
    }

    public static class DefaultValidatorFactory extends ValidatorFactory {
        @Override
        protected Validator<Organisation> create(Context context) {
            OrganisationValidator instance = (OrganisationValidator) context.get(NAME);
            if (instance == null) {
                instance = new OrganisationValidator();
                context.put(NAME, instance);
            }
            return instance;
        }
    }

    static {
        ValidatorFactory.factories.put(OrganisationValidator.class.getName(), new OrganisationValidator.DefaultValidatorFactory());
    }

}
