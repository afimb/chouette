package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.model.Company;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.Operator;
import org.rutebanken.netex.model.OrganisationsInFrame_RelStructure;

import javax.xml.bind.JAXBElement;
import java.util.List;

@Log4j
public class OrganisationParser extends AbstractParser {

    @Override
    public void initReferentials(Context context) throws Exception {
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        OrganisationsInFrame_RelStructure organisationsInFrameStruct = (OrganisationsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends DataManagedObjectStructure>> organisationElements = organisationsInFrameStruct.getOrganisation_();

        for (JAXBElement<? extends DataManagedObjectStructure> organisationElement : organisationElements) {
            DataManagedObjectStructure organisation = organisationElement.getValue();
            String organisationId = organisation.getId();
            Integer version = Integer.valueOf(organisation.getVersion());

            if (organisation instanceof Authority) {
                Authority authority = (Authority) organisation;
                Company company = ObjectFactory.getCompany(referential, organisationId);
                company.setObjectVersion(version != null ? version : 0);
                company.setName(authority.getName().getValue());
                company.setRegistrationNumber(authority.getCompanyNumber());
                company.setPhone(authority.getContactDetails().getPhone());
                company.setUrl(authority.getContactDetails().getUrl());
                company.setEmail(authority.getContactDetails().getEmail());
                company.setFilled(true);
            } else if (organisation instanceof Operator) {
                Operator operator = (Operator) organisation;
                Company company = ObjectFactory.getCompany(referential, organisationId);
                company.setObjectVersion(version != null ? version : 0);
                company.setName(operator.getName().getValue());
                company.setRegistrationNumber(operator.getCompanyNumber());
                company.setPhone(operator.getContactDetails().getPhone());
                company.setUrl(operator.getContactDetails().getUrl());
                company.setEmail(operator.getContactDetails().getEmail());
                company.setFilled(true);
            }
        }
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
