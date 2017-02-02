package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.Company;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.ContactStructure;
import org.rutebanken.netex.model.Operator;
import org.rutebanken.netex.model.OrganisationTypeEnumeration;

public class OperatorProducer extends AbstractJaxbNetexProducer<Operator, Company> {

    @Override
    public Operator produce(Company company, boolean addExtension) {
        Operator operator = netexFactory.createOperator();
        populateFromModel(operator, company);

        operator.setName(getMultilingualString(company.getName()));

        if (StringUtils.isNotEmpty(company.getShortName())) {
            operator.setShortName(getMultilingualString(company.getShortName()));
        }

        operator.setCompanyNumber(company.getRegistrationNumber());
        operator.getOrganisationType().add(OrganisationTypeEnumeration.OPERATOR);

        ContactStructure contactStructure = netexFactory.createContactStructure();

        // TODO null check on all fields
        contactStructure.setPhone(company.getPhone());
        //contactStructure.setFax(company.getFax());
        //contactStructure.setEmail(company.getEmail());
        contactStructure.setUrl(company.getUrl());
        operator.setContactDetails(contactStructure);
        operator.setCustomerServiceContactDetails(contactStructure);

        return operator;
    }
}
