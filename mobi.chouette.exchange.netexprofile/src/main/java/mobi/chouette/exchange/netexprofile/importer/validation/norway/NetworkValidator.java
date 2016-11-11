package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.DataLocation;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

@Log4j
public class NetworkValidator extends AbstractValidator implements Validator<Network> {

    public static final String LOCAL_CONTEXT = "NetexNetwork";
    public static final String ORGANISATION_ID = "organisationId";
    public static String NAME = "NetworkValidator";

    private static final String NETWORK_1 = "2-NETEX-Network-1";
    private static final String NETWORK_2 = "2-NETEX-Network-2";
    private static final String NETWORK_3 = "2-NETEX-Network-3";
    private static final String NETWORK_4 = "2-NETEX-Network-4";

    @Override
    protected void initializeCheckPoints(Context context) {
        //addItemToValidation(context, PREFIX, "Network", 4, "E", "E", "E", "E");
        addItemToValidation(context, PREFIX, "Network", 3, "E", "E", "E");
    }

    @Override
    public void addObjectReference(Context context, DataManagedObjectStructure object) {
        addObjectReference(context, LOCAL_CONTEXT, object);
    }

    @SuppressWarnings("unchecked")
    public void addOrganisationReference(Context context, String objectId, String organisationId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        List<String> organisationIds = (List<String>) objectContext.get(ORGANISATION_ID);
        if (organisationIds == null) {
            organisationIds = new ArrayList<>();
            objectContext.put(ORGANISATION_ID, organisationIds);
        }
        organisationIds.add(organisationId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void validate(Context context, Network target) throws ValidationException {
        // TODO validate common elements from extended types first, like id, version, etc...
        // TODO consider using the target instance when validating single elements

        Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
        ValidationData data = (ValidationData) context.get(VALIDATION_DATA); // how should this be used?
        Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);

        if (localContext == null || localContext.isEmpty()) {
            return;
        }

        Context organisationContext = (Context) validationContext.get(OrganisationValidator.LOCAL_CONTEXT);
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        // should probably use id from localContext keyset, and retrieve from referential by id
        // if we get null back the id is not valid, for now validating the target
        String objectId = target.getId();
        Context objectContext = (Context) localContext.get(objectId);

        // 2-NETEX-Network-1 : validate mandatory transport organisation element (cardinality 1:1)
        // TODO: check out why schema validation requires 'abstract' attribute set to false which is not available in netex model, disabled for now
        prepareCheckPoint(context, NETWORK_1);
        JAXBElement<? extends OrganisationRefStructure> transportOrganisationRef = target.getTransportOrganisationRef();

/*
        if (transportOrganisationRef == null) {
            DataLocation dataLocation = new DataLocation((String)context.get(FILE_NAME));
            dataLocation.setName("OrganisationRef");
            addValidationError(context, NETWORK_1, "Missing mandatory element : 'OrganisationRef'", dataLocation);
        } else {
            // 2-NETEX-Network-2 : validate if transport organisation reference exists
            prepareCheckPoint(context, NETWORK_2);
            String organisationId = (String) objectContext.get(ORGANISATION_ID);

            if (!organisationContext.containsKey(organisationId)) {
                DataLocation dataLocation = new DataLocation((String)context.get(FILE_NAME));
                dataLocation.setName("OrganisationId");
                addValidationError(context, NETWORK_2, String.format("Non-existent organisation id : '%s'", organisationId), dataLocation);
            }
        }
*/

        // TODO: change back to NETWORK_3 when validator complete
        // 2-NETEX-Network-3 : validate mandatory groups of lines (cardinality 1:*)
        //prepareCheckPoint(context, NETWORK_3);
        prepareCheckPoint(context, NETWORK_2);
        GroupsOfLinesInFrame_RelStructure groupsOfLinesStruct = target.getGroupsOfLines();

        // TODO consider separating the two checks in if test
        if (groupsOfLinesStruct == null || isCollectionEmpty(groupsOfLinesStruct.getGroupOfLines())) {
            DataLocation dataLocation = new DataLocation((String)context.get(FILE_NAME));
            dataLocation.setName("GroupOfLines");
            //addValidationError(context, NETWORK_3, "Missing mandatory element : 'groupsOfLines' or 'GroupOfLines'", dataLocation);
            addValidationError(context, NETWORK_2, "Missing mandatory element : 'groupsOfLines' or 'GroupOfLines'", dataLocation);
        } else {
            // TODO validate group of lines here...
        }

        // TODO: change back to NETWORK_4 when validator complete
        // 2-NETEX-Network-4 : validate optional tariff zones (cardinality 0:*)
        //prepareCheckPoint(context, NETWORK_4);
        prepareCheckPoint(context, NETWORK_3);
        TariffZoneRefs_RelStructure tariffZonesStruct = target.getTariffZones();

        if (tariffZonesStruct != null && !isCollectionEmpty(tariffZonesStruct.getTariffZoneRef())) {
            log.info("Network - Tariff Zones present");
            // TODO validate tariff zones here...
        }

        return;
    }

    public static class DefaultValidatorFactory extends ValidatorFactory {
        @Override
        protected Validator<Network> create(Context context) {
            NetworkValidator instance = (NetworkValidator) context.get(NAME);
            if (instance == null) {
                instance = new NetworkValidator();
                context.put(NAME, instance);
            }
            return instance;
        }
    }

    static {
        ValidatorFactory.factories.put(NetworkValidator.class.getName(), new NetworkValidator.DefaultValidatorFactory());
    }

}
