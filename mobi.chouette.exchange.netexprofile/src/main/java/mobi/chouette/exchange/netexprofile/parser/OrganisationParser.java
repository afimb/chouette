package mobi.chouette.exchange.netexprofile.parser;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.Operator;
import org.rutebanken.netex.model.OrganisationsInFrame_RelStructure;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.OrganisationValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.Company;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class OrganisationParser extends AbstractParser {

    public static final String LOCAL_CONTEXT = "OrganisationContext";
    public static final String COMPANY_ID = "companyId";

    @Override
    public void initReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        OrganisationValidator validator = (OrganisationValidator) ValidatorFactory.create(OrganisationValidator.class.getName(), context);

        OrganisationsInFrame_RelStructure organisationsInFrameStruct =
                (OrganisationsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends DataManagedObjectStructure>> organisationElements =
                organisationsInFrameStruct.getOrganisation_();

        for (JAXBElement<? extends DataManagedObjectStructure> organisationElement : organisationElements) {
            DataManagedObjectStructure organisation = organisationElement.getValue();
            String objectId = organisation.getId();

            if (organisation instanceof Authority) {
                NetexObjectUtil.addAuthorityReference(referential, objectId, (Authority) organisation);
                validator.addObjectReference(context, organisation);
            } else if (organisation instanceof Operator) {
                NetexObjectUtil.addOperatorReference(referential, objectId, (Operator) organisation);
                validator.addObjectReference(context, organisation);
            }
        }
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        for(Authority authority : netexReferential.getAuthorities().values()) {
            Company company = ObjectFactory.getCompany(referential, authority.getId());
            company.setName(authority.getName().getValue());
            company.setRegistrationNumber(authority.getCompanyNumber());
            company.setPhone(authority.getContactDetails().getPhone());
            company.setUrl(authority.getContactDetails().getUrl());
            company.setEmail(authority.getContactDetails().getEmail());
            company.setFilled(true);
            addCompanyIdRef(context, company.getObjectId()	, company.getObjectId());
        }
        
        for(Operator operator : netexReferential.getOperators().values()) {
            Company company = ObjectFactory.getCompany(referential, operator.getId());
            company.setName(operator.getName().getValue());
            company.setRegistrationNumber(operator.getCompanyNumber());
            company.setPhone(operator.getContactDetails().getPhone());
            company.setUrl(operator.getContactDetails().getUrl());
            company.setEmail(operator.getContactDetails().getEmail());
            company.setFilled(true);
            addCompanyIdRef(context, company.getObjectId()	, company.getObjectId());
        }
    }

    private void addCompanyIdRef(Context context, String objectId, String companyId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(COMPANY_ID, companyId);
    }

    static {
        ParserFactory.register(OrganisationParser.class.getName(), new ParserFactory() {
            private OrganisationParser instance = new OrganisationParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
