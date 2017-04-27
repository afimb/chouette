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

@Log4j
public class OrganisationParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        OrganisationsInFrame_RelStructure organisationsInFrameStruct = (OrganisationsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

        for (JAXBElement<? extends DataManagedObjectStructure> organisationElement : organisationsInFrameStruct.getOrganisation_()) {
            DataManagedObjectStructure organisation = organisationElement.getValue();
            Organisation_VersionStructure organisationStruct = (Organisation_VersionStructure) organisation;

            Company company = ObjectFactory.getCompany(referential, organisation.getId());
            company.setObjectVersion(NetexParserUtils.getVersion(organisation));
            company.setName(organisationStruct.getName().getValue());

            if (organisationStruct.getLegalName() != null) {
                company.setOperatingDepartmentName(organisationStruct.getLegalName().getValue());
            }

            company.setRegistrationNumber(organisationStruct.getCompanyNumber());

            if (organisationStruct.getContactDetails() != null) {
                company.setPhone(organisationStruct.getContactDetails().getPhone());
                company.setUrl(organisationStruct.getContactDetails().getUrl());
                company.setEmail(organisationStruct.getContactDetails().getEmail());
            }

            company.setFilled(true);
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
