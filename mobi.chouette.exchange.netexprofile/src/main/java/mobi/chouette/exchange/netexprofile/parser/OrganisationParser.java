package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.Company;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.Organisation_VersionStructure;
import org.rutebanken.netex.model.OrganisationsInFrame_RelStructure;

import javax.xml.bind.JAXBElement;
import java.util.List;

@Log4j
public class OrganisationParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        OrganisationsInFrame_RelStructure organisationsInFrameStruct = (OrganisationsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends DataManagedObjectStructure>> organisationElements = organisationsInFrameStruct.getOrganisation_();

        for (JAXBElement<? extends DataManagedObjectStructure> organisationElement : organisationElements) {
            DataManagedObjectStructure organisation = organisationElement.getValue();
            String organisationId = organisation.getId();

            Organisation_VersionStructure org = (Organisation_VersionStructure) organisation;
            Company company = ObjectFactory.getCompany(referential, organisationId);
            company.setObjectVersion(NetexParserUtils.getVersion(organisation));
            company.setName(org.getName().getValue());
            if (org.getLegalName() != null) {
                company.setOperatingDepartmentName(org.getLegalName().getValue());
            }
            company.setRegistrationNumber(org.getCompanyNumber());
            if(org.getContactDetails() != null) {
                company.setPhone(org.getContactDetails().getPhone());
                company.setUrl(org.getContactDetails().getUrl());
                company.setEmail(org.getContactDetails().getEmail());
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
